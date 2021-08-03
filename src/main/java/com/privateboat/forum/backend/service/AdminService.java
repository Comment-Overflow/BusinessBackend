package com.privateboat.forum.backend.service;

import com.privateboat.forum.backend.exception.AdminException;
import com.privateboat.forum.backend.exception.PostException;

public interface AdminService {
    void silenceUser(Long operatorId, Long userId) throws AdminException;
    void freeUser(Long operatorId, Long userId) throws AdminException;
    void freezePost(Long operatorId, Long postId) throws AdminException, PostException;
    void releasePost(Long operatorId, Long postId) throws AdminException, PostException;
}
