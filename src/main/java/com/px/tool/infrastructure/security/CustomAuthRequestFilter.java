package com.px.tool.infrastructure.security;

import com.px.tool.domain.user.User;
import com.px.tool.domain.user.service.UserService;
import com.px.tool.domain.user.service.impl.AuthServiceImpl;
import com.px.tool.infrastructure.exception.PXException;
import com.px.tool.infrastructure.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthRequestFilter extends OncePerRequestFilter {
    @Autowired
    private UserService userService;

    @Autowired
    private AuthServiceImpl authService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = CommonUtils.extractRequestToken(request);
            if (token != null && token.length() > 0 && authService.validateToken(token)) {
                Long userId = authService.getUserIdFromJWT(token);
                User userDetails = userService.findById(userId);
                if (userDetails.getDeleted()) {
                    throw new PXException("User đã bị xóa");
                }
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            SecurityContextHolder.clearContext();
            logger.error("Could not set user authentication in service context", ex);
        }
        filterChain.doFilter(request, response);
    }

}
