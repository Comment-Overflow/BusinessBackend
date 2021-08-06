package com.privateboat.forum.backend.serviceimpl;

import com.privateboat.forum.backend.entity.KeyWord;
import com.privateboat.forum.backend.entity.Post;
import com.privateboat.forum.backend.entity.PreferredWord;
import com.privateboat.forum.backend.entity.UserInfo;
import com.privateboat.forum.backend.enumerate.PostTag;
import com.privateboat.forum.backend.enumerate.PreferDegree;
import com.privateboat.forum.backend.repository.PostRepository;
import com.privateboat.forum.backend.repository.PreferredWordRepository;
import com.privateboat.forum.backend.repository.UserInfoRepository;
import com.privateboat.forum.backend.service.RecommendService;
import com.privateboat.forum.backend.util.Constant;
import com.privateboat.forum.backend.util.LogUtil;
import com.privateboat.forum.backend.util.RedisUtil;
import org.ansj.app.keyword.KeyWordComputer;
import org.ansj.app.keyword.Keyword;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RecommendServiceImpl implements RecommendService {
    private final PreferredWordRepository preferredWordRepository;
    private final PostRepository postRepository;
    private final UserInfoRepository userInfoRepository;
    private final RedisUtil redisUtil;

    /**
     * get Content Based Recommendations
     */
    @Override
    public List<Post> getCBRecommendations(Long userId) {
        HashMap<PostTag, HashMap<String, Long>> preferredWordMap = preferredWordRepository.findAllByUserId(userId);
        List<Post> postList = postRepository.findAllRecentPost();
        HashMap<Post, Long> CBRecommendMap = new HashMap<>();
        for(Post post : postList) {
            if(preferredWordMap.get(post.getTag()) != null) {
                List<KeyWord> keyWordList = post.getKeyWordList();
                CBRecommendMap.put(post, getScore(preferredWordMap.get(post.getTag()), keyWordList));
            }
        }
        removeZeroItems(CBRecommendMap);
        removeReadItems(userId, CBRecommendMap);
        return CBRecommendMap.entrySet().stream().sorted(Map.Entry.comparingByValue())
                .limit(Constant.CB_RECOMMEND_POST_NUMBER)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updatePreferredWordList(Long userId, Long postId, PreferDegree preferDegree){
        UserInfo user = userInfoRepository.getById(userId);
        Post post = postRepository.getByPostId(postId);
        PostTag postTag = post.getTag();
        List<KeyWord> keyWordList = post.getKeyWordList();
        HashMap<String, PreferredWord.wordWithId> preferredWordMap = preferredWordRepository.findAllByUserIdAndPostTag(userId, postTag);
        for (KeyWord keyWord : keyWordList) {
            String word = keyWord.getWord();
            Long score = keyWord.getScore();
            switch (preferDegree) {
                //once one REPLYs or STARs, he must have BROWSED
                case REPLY:
                    score *= 1;
                    break;
                case STAR:
                    score *= 2;
                    break;
            }
            if (preferredWordMap.get(word) != null) {
                PreferredWord.wordWithId wordWithId = preferredWordMap.get(word);
                LogUtil.debug(word);
                LogUtil.debug(score);
                LogUtil.debug(wordWithId.getScore());
                preferredWordRepository.updatePreferredWord(wordWithId.getId(), wordWithId.getScore() + score);
            } else {
                PreferredWord newPreferredWord = new PreferredWord(userId, word, score, postTag);
                user.getPreferredWordList().add(newPreferredWord);
            }
        }
    }

    @Override
    public List<KeyWord> addNewPost(PostTag postTag, Long postId, String title, String content) {
        KeyWordComputer keyWordComputer = new KeyWordComputer(Constant.POST_KEYS_WORDS);
        List<Keyword> keyWordList = keyWordComputer.computeArticleTfidf(title, content);
        List<KeyWord> ret = new LinkedList<>();
        for(Keyword keyword : keyWordList) {
            LogUtil.debug(keyword.toString());
            ret.add(new KeyWord(keyword.getName(), (long) (keyword.getScore()) * 100));
        }
        return ret;
    }


    private Long getScore(HashMap<String, Long> preferredWordMap, List<KeyWord> keyWordList) {
        long score = 0L;
        for(KeyWord keyWord : keyWordList) {
            String word = keyWord.getWord();
            if(preferredWordMap.get(word) != null) {
                score += preferredWordMap.get(word) + keyWord.getScore();
            }
        }
        return score;
    }

    private void removeZeroItems(HashMap<Post, Long> map) {
        map.entrySet().removeIf(e -> e.getValue() == 0);
    }

    private void removeReadItems(Long userId, HashMap<Post, Long> map) {
        map.entrySet().removeIf(e -> redisUtil.filterReadPosts(userId, e.getKey().getId()));
    }
}
