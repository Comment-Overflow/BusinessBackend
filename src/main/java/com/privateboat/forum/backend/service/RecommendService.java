package com.privateboat.forum.backend.service;

import com.privateboat.forum.backend.entity.KeyWord;
import com.privateboat.forum.backend.entity.Post;
import com.privateboat.forum.backend.enumerate.PostTag;
import com.privateboat.forum.backend.enumerate.PreferDegree;

import java.util.List;

public interface RecommendService {
    List<Post> getCBRecommendations(Long userId);
    void updatePreferredWordList(Long userId, Long postId, PreferDegree preferDegree);
    List<KeyWord> addNewPost(PostTag postTag, Long postId, String title, String content);
}
