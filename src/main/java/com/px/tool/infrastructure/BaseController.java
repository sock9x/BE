package com.px.tool.infrastructure;

import com.px.tool.infrastructure.exception.PXException;
import com.px.tool.infrastructure.model.ErrorResponse;
import com.px.tool.infrastructure.utils.CommonUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestControllerAdvice
public abstract class BaseController {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Autowired
    private Environment environment;

    @ExceptionHandler(PXException.class)
    public ResponseEntity<ErrorResponse> handleRootException(PXException e) {
        e.printStackTrace();
        return ResponseEntity
                .badRequest()
                .body(ErrorResponse
                        .builder()
                        .message(getStrVal(e.getMessage()))
                        .code("500")
                        .build());
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<ErrorResponse> sizeLimitExceeded(MultipartException e) {
        e.printStackTrace();
        return ResponseEntity
                .badRequest()
                .body(ErrorResponse
                        .builder()
                        .message(getStrVal("fileSize.limitExceeded"))
                        .code("500")
                        .build());
    }

    private String getStrVal(String k) {
        return environment.getProperty(k, k);
    }

    public Long extractUserInfo(HttpServletRequest httpServletRequest) {
        String token = CommonUtils.extractRequestToken(httpServletRequest);
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong(claims.getSubject());
    }


    protected ResponseEntity<Resource> toFile(HttpServletRequest request, Resource resource) {
        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

}
