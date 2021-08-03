package com.privateboat.forum.backend.serviceimpl;

import com.privateboat.forum.backend.entity.Post;
import com.privateboat.forum.backend.entity.UserAuth;
import com.privateboat.forum.backend.enumerate.UserType;
import com.privateboat.forum.backend.exception.AdminException;
import com.privateboat.forum.backend.exception.PostException;
import com.privateboat.forum.backend.repository.PostRepository;
import com.privateboat.forum.backend.repository.UserAuthRepository;
import com.privateboat.forum.backend.service.AdminService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional
public class AdminServiceImpl implements AdminService {
    final UserAuthRepository userAuthRepository;
    final PostRepository postRepository;

    @Override
    public void silenceUser(Long operatorId, Long userId) throws AdminException {
        UserAuth operatorAuth = userAuthRepository.getByUserId(operatorId);
        if (operatorAuth == null || operatorAuth.getUserType() != UserType.ADMIN) {
            throw new AdminException(AdminException.AdminExceptionType.OPERATOR_NOT_ADMIN);
        }
        UserAuth userAuth = userAuthRepository.getByUserId(userId);
        if (userAuth == null || userAuth.getUserType() != UserType.USER) {
            throw new AdminException(AdminException.AdminExceptionType.INVALID_SILENCE_TARGET);
        }
        userAuth.setUserType(UserType.SILENCED);
        userAuthRepository.save(userAuth);
    }

    @Override
    public void freeUser(Long operatorId, Long freeUserId) throws AdminException {
        UserAuth operatorAuth = userAuthRepository.getByUserId(operatorId);
        if (operatorAuth == null || operatorAuth.getUserType() != UserType.ADMIN) {
            throw new AdminException(AdminException.AdminExceptionType.OPERATOR_NOT_ADMIN);
        }
        UserAuth userAuth = userAuthRepository.getByUserId(freeUserId);
        if (userAuth == null ||
                (userAuth.getUserType() != UserType.SILENCED &&
                        userAuth.getUserType() != UserType.BANNED)) {
            throw new AdminException(AdminException.AdminExceptionType.INVALID_FREE_TARGET);
        }
        userAuth.setUserType(UserType.USER);
        userAuthRepository.save(userAuth);
    }

    @Override
    public void freezePost(Long operatorId, Long postId) throws AdminException, PostException {
        UserAuth operatorAuth = userAuthRepository.getByUserId(operatorId);
        if (operatorAuth == null || operatorAuth.getUserType() != UserType.ADMIN) {
            throw new AdminException(AdminException.AdminExceptionType.OPERATOR_NOT_ADMIN);
        }
        Post post = postRepository.getByPostId(postId);
        if (post == null) {
            throw new PostException(PostException.PostExceptionType.POST_NOT_EXIST);
        }
        post.setIsFrozen(true);
        postRepository.save(post);
    }

    @Override
    public void releasePost(Long operatorId, Long postId) throws AdminException, PostException {
        UserAuth operatorAuth = userAuthRepository.getByUserId(operatorId);
        if (operatorAuth == null || operatorAuth.getUserType() != UserType.ADMIN) {
            throw new AdminException(AdminException.AdminExceptionType.OPERATOR_NOT_ADMIN);
        }
        Post post = postRepository.getByPostId(postId);
        if (post == null) {
            throw new PostException(PostException.PostExceptionType.POST_NOT_EXIST);
        }
        post.setIsFrozen(false);
        postRepository.save(post);
    }
}
