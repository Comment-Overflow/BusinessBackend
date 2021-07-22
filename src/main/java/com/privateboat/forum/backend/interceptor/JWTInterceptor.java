package com.privateboat.forum.backend.interceptor;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.privateboat.forum.backend.exception.AuthException;
import com.privateboat.forum.backend.service.AuthService;
import com.privateboat.forum.backend.util.JWTUtil;
import lombok.AllArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Map;

@Component
@AllArgsConstructor
public class JWTInterceptor implements HandlerInterceptor {
    private final AuthService authService;

    public boolean preHandle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler) {

        if (!(handler instanceof HandlerMethod)) {
            return false;
        }

        // Check the request authentication. ALl the methods in the controller should be annotated with @Authentication.
        HandlerMethod handlerMethod = (HandlerMethod)handler;
        Method method = handlerMethod.getMethod();
        if (!method.isAnnotationPresent(JWTUtil.Authentication.class)) {
            return false;
        }

        JWTUtil.AuthenticationType authenticationType = method.getAnnotation(JWTUtil.Authentication.class).type();
        if (authenticationType == JWTUtil.AuthenticationType.PASS) {
            return true;
        }

        // Acquire token.
        String token = request.getHeader("Authorization");

        // Get fields from token.
        Map<String, Claim> claims;
        try {
            claims = JWTUtil.getClaims(token);
        } catch (TokenExpiredException e) {
            return false;
        }

        // Add user Id to request attribute.
        Long userId = claims.get("userId").asLong();
        request.setAttribute("userId", userId);

        String password = claims.get("password").asString();
        try {
            authService.verifyAuth(userId, password);
        } catch (AuthException e) {
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(@NonNull HttpServletRequest request,
                           @NonNull HttpServletResponse response,
                           @NonNull Object handler,
                           @Nullable ModelAndView modelAndView) {

    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request,
                                @NonNull HttpServletResponse response,
                                @NonNull Object handler,
                                Exception ex) {

    }
}
