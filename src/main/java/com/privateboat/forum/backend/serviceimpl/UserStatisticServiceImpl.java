package com.privateboat.forum.backend.serviceimpl;

import com.privateboat.forum.backend.dto.response.NewlyRecordDTO;
import com.privateboat.forum.backend.entity.UserStatistic;
import com.privateboat.forum.backend.exception.UserInfoException;
import com.privateboat.forum.backend.repository.UserStatisticRepository;
import com.privateboat.forum.backend.service.UserStatisticService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;


@Service
@AllArgsConstructor
@Transactional
public class UserStatisticServiceImpl implements UserStatisticService {
    private final UserStatisticRepository userStatisticRepository;

    @Override
    public UserStatistic getNewlyRecords(Long userId) throws UserInfoException {
        return userStatisticRepository.getByUserId(userId);
    }
}
