package com.privateboat.forum.backend.service;

import com.privateboat.forum.backend.entity.KeyWord;
import com.privateboat.forum.backend.entity.Post;
import com.privateboat.forum.backend.enumerate.PostTag;
import com.privateboat.forum.backend.enumerate.PreferenceDegree;

import java.util.List;

public interface RecommendService {
    List<Post> getCBRecommendations(Long userId);
    List<Post> getCFRecommendations(Long userId);
    void updateRecommendSystem(Long userId, Long postId, PreferenceDegree preferenceDegree);
    List<KeyWord> addNewPost(PostTag postTag, Long postId, String title, String content);
}
