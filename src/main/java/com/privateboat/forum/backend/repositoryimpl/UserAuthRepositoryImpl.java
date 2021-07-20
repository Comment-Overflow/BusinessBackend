package com.privateboat.forum.backend.repositoryimpl;

import com.privateboat.forum.backend.dao.UserAuthDAO;
import com.privateboat.forum.backend.entity.UserAuth;
import com.privateboat.forum.backend.repository.UserAuthRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserAuthRepositoryImpl implements UserAuthRepository {
    private final UserAuthDAO userAuthDAO;

    @Override
    public Boolean existsByEmail(String email) {
        return userAuthDAO.existsByEmail(email);
    }
}
