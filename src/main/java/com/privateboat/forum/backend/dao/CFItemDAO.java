package com.privateboat.forum.backend.dao;

import com.privateboat.forum.backend.entity.CFItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CFItemDAO extends JpaRepository<CFItem, Long> {
    List<Long> getByUserId(Long userId);
}
