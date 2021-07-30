package com.privateboat.forum.backend.servicetest;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.privateboat.forum.backend.exception.AuthException;
import com.privateboat.forum.backend.repository.UserAuthRepository;
import com.privateboat.forum.backend.repository.UserInfoRepository;
import com.privateboat.forum.backend.serviceimpl.AuthServiceImpl;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;

public class AuthServiceTest {
    static final private String EMAIL = "gungnir_guo@sjtu.edu.cn";
    static final private String DUPLICATE_EMAIL = "guozhidong12@126.com";
    static final private String PASSWORD = "guozhidong12";
    static final private String CORRECT_USER_CODE = "123456";
    static final private String WRONG_USER_CODE = "654321";
    static final private String JWT_SECRET = "comment_overflow";
    private String CORRECT_EMAIL_TOKEN;
    private String EXPIRED_EMAIL_TOKEN;

    @InjectMocks
    private AuthServiceImpl authService;
    private AutoCloseable closeable;

    @Mock
    private UserAuthRepository userAuthRepository;

    @Mock
    private UserInfoRepository userInfoRepository;

    @Mock
    private BCryptPasswordEncoder encoder;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);

        // JWT header
        Map<String, Object> map = new HashMap<>() {{
            put("alg", "HS256");
            put("typ", "JWT");
        }};
        Date now = new Date();
        Date expire = DateUtils.addMinutes(now, 10);

        // Mock duplicate email.
        Mockito.when(userAuthRepository.existsByEmail(DUPLICATE_EMAIL)).
                thenReturn(true);
//        // Mock verify password.
//        Mockito.when(authService.verifyAuth(EMAIL, PASSWORD)).
//                thenReturn(true);

        CORRECT_EMAIL_TOKEN = JWT.create()
                .withHeader(map)
                .withClaim("code", CORRECT_USER_CODE)
                .withIssuedAt(now)
                .withExpiresAt(expire)
                .sign(Algorithm.HMAC256(JWT_SECRET));

        EXPIRED_EMAIL_TOKEN = JWT.create()
                .withHeader(map)
                .withClaim("code", CORRECT_USER_CODE)
                .withIssuedAt(now)
                .withExpiresAt(now)
                .sign(Algorithm.HMAC256(JWT_SECRET));
    }

    @Test
    void testRegister() {
        // Test successful registration.
        authService.register(
                EMAIL,
                PASSWORD,
                CORRECT_USER_CODE,
                CORRECT_EMAIL_TOKEN
        );
        Mockito.verify(userInfoRepository).save(any());

        // Test non-existent email token
        AuthException absentEmailTokenException =
                Assertions.assertThrows(AuthException.class, () -> {
            authService.register(
                    EMAIL,
                    PASSWORD,
                    CORRECT_USER_CODE,
                    null
            );
        });
        assertSame(absentEmailTokenException.getType(), AuthException.AuthExceptionType.WRONG_EMAIL_TOKEN);

        // Test expired email token.
        AuthException expiredEmailTokenException =
                Assertions.assertThrows(AuthException.class, () -> {
                    authService.register(
                            EMAIL,
                            PASSWORD,
                            CORRECT_USER_CODE,
                            EXPIRED_EMAIL_TOKEN
                    );
                });
        assertSame(expiredEmailTokenException.getType(), AuthException.AuthExceptionType.EXPIRED_EMAIL_TOKEN);

        // Test empty user code.
        AuthException nullUserCodeException =
                Assertions.assertThrows(AuthException.class, () -> {
                    authService.register(
                            EMAIL,
                            PASSWORD,
                            null,
                            CORRECT_EMAIL_TOKEN
                    );
                });
        assertSame(nullUserCodeException.getType(), AuthException.AuthExceptionType.WRONG_EMAIL_TOKEN);

        // Test wrong user code.
        AuthException wrongUserCodeException =
                Assertions.assertThrows(AuthException.class, () -> {
                    authService.register(
                            EMAIL,
                            PASSWORD,
                            WRONG_USER_CODE,
                            CORRECT_EMAIL_TOKEN
                    );
                });
        assertSame(wrongUserCodeException.getType(), AuthException.AuthExceptionType.WRONG_EMAIL_TOKEN);

        // Test duplicate user.
        // Test wrong user code.
        AuthException duplicateEmailException =
                Assertions.assertThrows(AuthException.class, () -> {
                    authService.register(
                            DUPLICATE_EMAIL,
                            PASSWORD,
                            CORRECT_USER_CODE,
                            CORRECT_EMAIL_TOKEN
                    );
                });
        assertSame(duplicateEmailException.getType(), AuthException.AuthExceptionType.DUPLICATE_EMAIL);
    }

    @Test
    void testLogin() {

    }

    @Test
    void testSendEmail() {
        assertSame(authService.sendEmail(EMAIL), any(String.class));
    }
}
