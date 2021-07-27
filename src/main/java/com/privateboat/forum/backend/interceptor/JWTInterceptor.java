package com.privateboat.forum.backend.interceptor;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.privateboat.forum.backend.exception.AuthException;
import com.privateboat.forum.backend.service.AuthService;
import com.privateboat.forum.backend.util.JWTUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@AllArgsConstructor
public class JWTInterceptor implements HandlerInterceptor, ChannelInterceptor {
    private final AuthService authService;

    // Web Socket
    @SneakyThrows
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        Map<String, Object> headers = message.getHeaders();
        for (String key : headers.keySet()) {
            System.out.print(key);
            System.out.println(' ');
            System.out.println(headers.get(key).toString());
        }
        System.out.println();

        String messageType = Objects.requireNonNull(headers.get("simpMessageType")).toString();
        Object commandObj = headers.get("stompCommand");

        if (
                // Client connects to Server.
                messageType.equals("CONNECT") ||
                // Client subscribes to a channel.
                messageType.equals("SUBSCRIBE") ||
                // Client sends a message to another client.
                (messageType.equals("MESSAGE") &&
                        commandObj != null && commandObj.toString().equals("SEND"))
        ) {
            // Extract token from the native headers.
            String nativeHeaders = Objects.requireNonNull(headers.get("nativeHeaders")).toString();
            Pattern authPattern = Pattern.compile("Authorization=\\[(.*?)]");
            Matcher authMatcher = authPattern.matcher(nativeHeaders);
            if (!authMatcher.find()) throw new AssertionError();
            String token = authMatcher.group(1);

            // Verify the token.
            Map<String, Claim> claims = JWTUtil.getClaims(token);
            Long userId = claims.get("userId").asLong();
            String password = claims.get("password").asString();
            authService.verifyAuth(userId, password);

            if (messageType.equals("SUBSCRIBE")) {
                String destination = Objects.requireNonNull(headers.get("simpDestination")).toString();
                if (destination.startsWith("/user")) {
                    Long subscribeUserId = Long.valueOf(destination.split("/")[2]);
                    assert subscribeUserId.equals(userId);
                }
            }
        }

        return message;
    }

    // HTTP
    @Override
    public boolean preHandle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler) {

        if (!(handler instanceof HandlerMethod)) {
            return false;
        }

        // Check the request authentication. ALl the methods in the controller should be annotated with @Authentication.
        HandlerMethod handlerMethod = (HandlerMethod) handler;
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
        if (token == null)
            return false;

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
