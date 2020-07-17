package com.px.tool.domain.user.service.impl;

import com.px.tool.domain.user.User;
import com.px.tool.domain.user.service.UserService;
import com.px.tool.infrastructure.model.payload.TokenInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;

@Service
public class AuthServiceImpl implements UserDetailsService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Lazy
    @Autowired
    private UserService userService;

    @Value("${app.jwtExpirationInMs}")
    private int jwtExpirationInMs;

    @Value("${app.jwtSecret}")
    private String jwtSecret;
    private JwtBuilder jwtBuilder;

    @PostConstruct
    public void init() {
        jwtBuilder = Jwts.builder().signWith(SignatureAlgorithm.HS512, jwtSecret);
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail)
            throws UsernameNotFoundException {
        return userService.loadUserByUsername(usernameOrEmail);
    }


    public TokenInfo generateToken(User user) {
        Date now = new Date();
        long expireTime = now.getTime() + jwtExpirationInMs;

        String accessToken = jwtBuilder
                .setSubject(Long.toString(user.getUserId()))
                .setIssuedAt(now)
                .setExpiration(new Date(expireTime))
                .compact();

        String refreshToken = jwtBuilder
                .setSubject(Long.toString(user.getUserId()))
                .setIssuedAt(now)
                .setExpiration(new Date(7 * expireTime))
                .compact();

        return TokenInfo.builder()
                .accessToken(accessToken)
                .expireAt(expireTime)
                .refreshToken(refreshToken)
                .tokenType("bearer")
                .build();

    }

    public Long getUserIdFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty.");
        }
        return false;
    }

    public User loadUserById(Long userId) {
        return userService.findById(userId);
    }
}