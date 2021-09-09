package com.privateboat.forum.backend.service;

import com.privateboat.forum.backend.entity.UserInfo;
import com.privateboat.forum.backend.enumerate.PostTag;
import com.privateboat.forum.backend.repository.*;
import com.privateboat.forum.backend.serviceimpl.ApprovalRecordServiceImpl;
import com.privateboat.forum.backend.serviceimpl.RecommendServiceImpl;
import com.privateboat.forum.backend.util.RecommendUtil;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Optional;

import static com.privateboat.forum.backend.fakedata.Recommend.*;
import static org.junit.jupiter.api.Assertions.*;

class RecommendServiceUnitTest {
    @Mock
    private StarRecordRepository starRecordRepository;

    @Mock
    private ApprovalRecordRepository approvalRecordRepository;

    @Mock
    private PreferredWordRepository preferredWordRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserInfoRepository userInfoRepository;

    @Mock
    private KeyWordRepository keyWordRepository;

    @Mock
    private CFItemRepository cfItemRepository;

    @Mock
    private RecommendUtil<NlpAnalysis> recommendUtil;

    @Mock
    private PreferencePostRepository preferencePostRepository;

    @InjectMocks
    private RecommendServiceImpl recommendService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Optional<UserInfo> userInfo = Optional.of(new UserInfo());
        Mockito.when(userInfoRepository.findByUserId(VALID_USER_ID))
                .thenReturn(userInfo);
        Mockito.when(preferredWordRepository.findAllByUserId(VALID_USER_ID))
                .thenReturn(PREFERRED_WORD_MAP);
    }

    @Test
    void testGetCBRecommendations() {
        try{
            recommendService.getCBRecommendations(VALID_USER_ID);
            Mockito.verify(userInfoRepository).findByUserId(VALID_USER_ID);
            Mockito.verify(preferredWordRepository).findAllByUserId(VALID_USER_ID);
        } catch (Exception e){
            assertNull(e);
        }
    }
}