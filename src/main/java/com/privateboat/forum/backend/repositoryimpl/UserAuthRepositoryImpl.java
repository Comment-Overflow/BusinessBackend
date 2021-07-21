package com.privateboat.forum.backend.repositoryimpl;

import com.privateboat.forum.backend.dao.UserAuthDAO;
import com.privateboat.forum.backend.entity.UserAuth;
import com.privateboat.forum.backend.repository.UserAuthRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class UserAuthRepositoryImpl implements UserAuthRepository {
    private final UserAuthDAO userAuthDAO;

    @Override
    public Boolean existsByEmail(String email) {
        return userAuthDAO.existsByEmail(email);
    }

    @Override
    public Optional<UserAuth> findByEmail(String email) {
        return userAuthDAO.findByEmail(email);
    }
}
