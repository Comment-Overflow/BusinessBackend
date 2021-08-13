package com.privateboat.forum.backend.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.privateboat.forum.backend.dto.response.LoginDTO;
import com.privateboat.forum.backend.entity.UserInfo;
import com.privateboat.forum.backend.exception.AuthException;
import com.privateboat.forum.backend.repository.UserAuthRepository;
import com.privateboat.forum.backend.repository.UserInfoRepository;
import com.privateboat.forum.backend.serviceimpl.AuthServiceImpl;
import com.privateboat.forum.backend.util.RedisUtil;
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
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.privateboat.forum.backend.fakedata.UserData.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

public class AuthServiceImplUnitTest {
    static final private String JWT_SECRET = "comment_overflow";

    static final private UserInfo.UserNameAndAvatarUrl USER_NAME_AND_AVATAR_URL = new UserInfo.UserNameAndAvatarUrl() {
        @Override
        public String getUserName() {
            return EMAIL;
        }

        @Override
        public String getAvatarUrl() {
            return null;
        }
    };

    private String CORRECT_EMAIL_TOKEN;
    private String EXPIRED_EMAIL_TOKEN;

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private UserAuthRepository userAuthRepository;

    @Mock
    private UserInfoRepository userInfoRepository;

    @Mock
    private BCryptPasswordEncoder encoder;

    @Mock
    private RedisUtil redisUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

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

        // Mock USER_ID will return USER_AUTH.
        Mockito.when(userAuthRepository.getByUserId(USER_ID)).
                thenReturn(USER_AUTH);

        // Mock userNameAndAvatarUrl projection.
        Mockito.when(userInfoRepository.getUserNameAndAvatarUrlById(USER_ID)).
                thenReturn(USER_NAME_AND_AVATAR_URL);

        // Mock optional user auth when password is correct.
        Mockito.when(userAuthRepository.findByEmail(EMAIL)).
                thenReturn(Optional.of(USER_AUTH));

        // Mock optional user auth when password is wrong.
        Mockito.when(userAuthRepository.findByEmail(DUPLICATE_EMAIL)).
                thenReturn(Optional.empty());

        // Mock correct password matching.
        Mockito.when(this.encoder.matches(PASSWORD, ENCODED_PASSWORD)).
                thenReturn(true);

        // Mock correct password matching.
        Mockito.when(this.encoder.matches(WRONG_PASSWORD, ENCODED_PASSWORD)).
                thenReturn(false);

        CORRECT_EMAIL_TOKEN = JWT.create()
                .withHeader(map)
                .withClaim("code", CORRECT_EMAIL_CODE)
                .withIssuedAt(now)
                .withExpiresAt(expire)
                .sign(Algorithm.HMAC256(JWT_SECRET));

        EXPIRED_EMAIL_TOKEN = JWT.create()
                .withHeader(map)
                .withClaim("code", CORRECT_EMAIL_CODE)
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
                CORRECT_EMAIL_CODE,
                CORRECT_EMAIL_TOKEN
        );
        Mockito.verify(userInfoRepository).save(any());

        // Test non-existent email token
        AuthException absentEmailTokenException =
                assertThrows(AuthException.class, () -> authService.register(
                        EMAIL,
                        PASSWORD,
                        CORRECT_EMAIL_CODE,
                        null
                ));
        assertSame(absentEmailTokenException.getType(), AuthException.AuthExceptionType.WRONG_EMAIL_TOKEN);

        // Wait for one sec to ensure token has expired.
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Test expired email token.
        AuthException expiredEmailTokenException =
                assertThrows(AuthException.class, () -> authService.register(
                        EMAIL,
                        PASSWORD,
                        CORRECT_EMAIL_CODE,
                        EXPIRED_EMAIL_TOKEN
                ));
        assertSame(expiredEmailTokenException.getType(), AuthException.AuthExceptionType.EXPIRED_EMAIL_TOKEN);

        // Test empty user code.
        AuthException nullUserCodeException =
                assertThrows(AuthException.class, () -> authService.register(
                        EMAIL,
                        PASSWORD,
                        null,
                        CORRECT_EMAIL_TOKEN
                ));
        assertSame(nullUserCodeException.getType(), AuthException.AuthExceptionType.WRONG_EMAIL_TOKEN);

        // Test wrong user code.
        AuthException wrongUserCodeException =
                assertThrows(AuthException.class, () -> authService.register(
                        EMAIL,
                        PASSWORD,
                        WRONG_EMAIL_CODE,
                        CORRECT_EMAIL_TOKEN
                ));
        assertSame(wrongUserCodeException.getType(), AuthException.AuthExceptionType.WRONG_EMAIL_TOKEN);

        // Test duplicate user.
        // Test wrong user code.
        AuthException duplicateEmailException =
                assertThrows(AuthException.class, () -> authService.register(
                        DUPLICATE_EMAIL,
                        PASSWORD,
                        CORRECT_EMAIL_CODE,
                        CORRECT_EMAIL_TOKEN
                ));
        assertSame(duplicateEmailException.getType(), AuthException.AuthExceptionType.DUPLICATE_EMAIL);
    }

    @Test
    void testLogin() {
        // Test successful login.
        try {
            LoginDTO result = authService.login(EMAIL, PASSWORD);
            assertNotNull(result.getToken());
            assertSame(result.getUserId(), USER_ID);
            assertSame(result.getUserName(), EMAIL);
            assertSame(result.getAvatarUrl(), null);
        } catch (AuthException e) {
            assertNull(e);
        }

        // Test wrong password.
        AuthException wrongPasswordException =
                Assertions.assertThrows(AuthException.class, () -> authService.login(
                        EMAIL,
                        WRONG_PASSWORD
                ));
        assertSame(wrongPasswordException.getType(), AuthException.AuthExceptionType.WRONG_PASSWORD);

        // Test non-existent email.
        AuthException nonExistentUserException =
                Assertions.assertThrows(AuthException.class, () -> authService.login(
                        DUPLICATE_EMAIL,
                        PASSWORD
                ));
        assertSame(nonExistentUserException.getType(), AuthException.AuthExceptionType.WRONG_EMAIL);
    }

    @Test
    void testRefreshToken() {
        // The expire time of the new token should be later than now plus 1 month.
        // Because refreshToken is invoked after now is computed.
        Date now = new Date();
        Date oneMonthLater = DateUtils.addMonths(now, 1);

        // Sleep for one second to make sure oneMonthLater is earlier than token expire time.
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        LoginDTO result = authService.refreshToken(USER_ID);
        assertSame(result.getUserId(), USER_ID);
        assertSame(result.getUserName(), EMAIL);
        assertSame(result.getAvatarUrl(), null);

        String token = result.getToken();
        Date newExpireDate = JWT.decode(token).getExpiresAt();
        assertTrue(oneMonthLater.before(newExpireDate));
    }

    @Test
    void testSendEmail() {
        assertSame(authService.sendEmail(EMAIL).getClass(), String.class);
    }

    @Test
    void testVerifyAuth() {
        // Test successful verification.
        try {
            authService.verifyAuth(USER_ID, ENCODED_PASSWORD);
        } catch (AuthException e) {
            assertNull(e);
        }

        // Test unsuccessful verification.
        AuthException authException =
                Assertions.assertThrows(AuthException.class, () -> authService.verifyAuth(USER_ID, WRONG_ENCODED_PASSWORD));
        assertSame(authException.getType(), AuthException.AuthExceptionType.WRONG_PASSWORD);
    }
}
