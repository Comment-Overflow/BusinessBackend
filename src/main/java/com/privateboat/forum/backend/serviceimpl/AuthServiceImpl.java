

package com.privateboat.forum.backend.serviceimpl;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.privateboat.forum.backend.dto.response.LoginDTO;
import com.privateboat.forum.backend.entity.UserAuth;
import com.privateboat.forum.backend.entity.UserInfo;
import com.privateboat.forum.backend.entity.UserStatistic;
import com.privateboat.forum.backend.exception.AuthException;
import com.privateboat.forum.backend.repository.UserAuthRepository;
import com.privateboat.forum.backend.repository.UserInfoRepository;
import com.privateboat.forum.backend.service.AuthService;
import com.privateboat.forum.backend.util.EmailUtil;
import com.privateboat.forum.backend.util.JWTUtil;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Map;
import java.util.Optional;

@Transactional
@AllArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {
    private final BCryptPasswordEncoder encoder;
    private final UserAuthRepository userAuthRepository;
    private final UserInfoRepository userInfoRepository;

    @Override
    public void register(String email, String password, String userCode, String emailToken) throws AuthException {
        // Verify email confirmation code
        Map<String, Claim> claims;
        String actualCode;

        if (emailToken == null) {
            throw new AuthException(AuthException.AuthExceptionType.WRONG_EMAIL_TOKEN);
        }

        // Token expired.
        try {
            claims = JWTUtil.getClaims(emailToken);
            actualCode = claims.get("code").asString();
        } catch (TokenExpiredException e) {
            throw new AuthException(AuthException.AuthExceptionType.EXPIRED_EMAIL_TOKEN);
        }

        // Email confirmation code does not exist or is wrong.
        if (userCode == null || !userCode.equals(actualCode)) {
            throw new AuthException(AuthException.AuthExceptionType.WRONG_EMAIL_TOKEN);
        }

        // Check duplicate user.
        if (userAuthRepository.existsByEmail(email)) {
            throw new AuthException(AuthException.AuthExceptionType.DUPLICATE_EMAIL);
        }

        // Save user authentication information.
        UserInfo userInfo = new UserInfo();
        UserAuth userAuth = new UserAuth(email, encoder.encode(password), userInfo);
        UserStatistic userStatistic = new UserStatistic(userInfo);
        userInfo.setUserAuth(userAuth);
        userInfo.setUserStatistic(userStatistic);
        userInfoRepository.save(userInfo);
    }

    @Override
    public LoginDTO login(String email, String rawPassword) throws AuthException {
        UserAuth verifiedUserAuth = verifyAuth(email, rawPassword);

        return new LoginDTO(verifiedUserAuth.getUserId(), JWTUtil.getLoginToken(verifiedUserAuth));
    }

    @Override
    public LoginDTO refreshToken(Long userId) {
        UserAuth userAuth = userAuthRepository.getByUserId(userId);

        return new LoginDTO(userId, JWTUtil.getLoginToken(userAuth));
    }

    @Override
    public String sendEmail(String email) {
        String confirmationCode = EmailUtil.sendEmail(email);

        return JWTUtil.getEmailToken(confirmationCode);
    }

    @Override
    public void verifyAuth(Long userId, String encodedPassword) throws AuthException {
        UserAuth userAuth = userAuthRepository.getByUserId(userId);

        if (!encodedPassword.equals(userAuth.getPassword())) {
            throw new AuthException(AuthException.AuthExceptionType.WRONG_PASSWORD);
        }
    }

    private UserAuth verifyAuth(String email, String rawPassword) throws AuthException {
        Optional<UserAuth> optionalUserAuth = userAuthRepository.findByEmail(email);

        if (optionalUserAuth.isEmpty()) {
            throw new AuthException(AuthException.AuthExceptionType.WRONG_EMAIL);
        }

        UserAuth userAuth = optionalUserAuth.get();

        if (!encoder.matches(rawPassword, userAuth.getPassword())) {
            throw new AuthException(AuthException.AuthExceptionType.WRONG_PASSWORD);
        }

        return userAuth;
    }
}

