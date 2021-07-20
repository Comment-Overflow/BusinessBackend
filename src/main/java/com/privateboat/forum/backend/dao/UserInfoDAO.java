package com.privateboat.forum.backend.dao;

import com.privateboat.forum.backend.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInfoDAO extends JpaRepository<UserInfo, Long> {
}
