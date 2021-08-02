package com.privateboat.forum.backend.service;

import com.privateboat.forum.backend.exception.AdminException;

public interface AdminService {
    void silenceUser(Long operatorId, Long userId) throws AdminException;
    void freeUser(Long operatorId, Long userId) throws AdminException;
}
