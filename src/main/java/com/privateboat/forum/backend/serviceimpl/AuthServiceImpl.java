package com.privateboat.forum.backend.serviceimpl;

import com.privateboat.forum.backend.entity.UserAuth;
import com.privateboat.forum.backend.entity.UserInfo;
import com.privateboat.forum.backend.entity.UserStatistic;
import com.privateboat.forum.backend.exception.AuthException;
import com.privateboat.forum.backend.repository.UserAuthRepository;
import com.privateboat.forum.backend.repository.UserInfoRepository;
import com.privateboat.forum.backend.repository.UserStatisticRepository;
import com.privateboat.forum.backend.service.AuthService;
import com.privateboat.forum.backend.util.JWTUtil;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Transactional
@AllArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {
    private final BCryptPasswordEncoder encoder;
    private final UserAuthRepository userAuthRepository;
    private final UserInfoRepository userInfoRepository;
    private final UserStatisticRepository userStatisticRepository;

    @Override
    public void register(String email, String password) throws AuthException {
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
    public String login(String email, String password) throws AuthException {
        UserAuth verifiedUserAuth = verifyAuth(email, password);

        return JWTUtil.getToken(verifiedUserAuth);
    }

    private UserAuth verifyAuth(String email, String password) throws AuthException {
        Optional<UserAuth> optionalUserAuth = userAuthRepository.findByEmail(email);

        if (optionalUserAuth.isEmpty()) {
            throw new AuthException(AuthException.AuthExceptionType.WRONG_EMAIL);
        }

        UserAuth userAuth = optionalUserAuth.get();

        if (!encoder.matches(password, userAuth.getPassword())) {
            throw new AuthException(AuthException.AuthExceptionType.WRONG_PASSWORD);
        }

        return userAuth;
    }
}
