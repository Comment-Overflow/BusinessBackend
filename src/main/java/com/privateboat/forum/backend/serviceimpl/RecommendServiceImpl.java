package com.privateboat.forum.backend.serviceimpl;

import com.privateboat.forum.backend.entity.KeyWord;
import com.privateboat.forum.backend.entity.Post;
import com.privateboat.forum.backend.entity.PreferredWord;
import com.privateboat.forum.backend.entity.UserInfo;
import com.privateboat.forum.backend.enumerate.PostTag;
import com.privateboat.forum.backend.enumerate.PreferenceDegree;
import com.privateboat.forum.backend.exception.UserInfoException;
import com.privateboat.forum.backend.repository.*;
import com.privateboat.forum.backend.service.RecommendService;
import com.privateboat.forum.backend.util.Constant;
import com.privateboat.forum.backend.util.LogUtil;
import com.privateboat.forum.backend.util.RecommendUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ansj.app.keyword.Keyword;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.jdbc.ReloadFromJDBCDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class RecommendServiceImpl implements RecommendService {
    private final StarRecordRepository starRecordRepository;
    private final ApprovalRecordRepository approvalRecordRepository;
    private final PreferredWordRepository preferredWordRepository;
    private final PostRepository postRepository;
    private final UserInfoRepository userInfoRepository;
    private final KeyWordRepository keyWordRepository;
    private final CFItemRepository cfItemRepository;
    private final RecommendUtil<NlpAnalysis> recommendUtil;
    private final PreferencePostRepository preferencePostRepository;

    /**
     * get Content Based Recommendations
     */
    @Override
    public List<Post> getCBRecommendations(Long userId) {
//        long start;

        Optional<UserInfo> optionalViewerInfo = userInfoRepository.findByUserId(userId);
        if (optionalViewerInfo.isEmpty()) {
            throw new UserInfoException(UserInfoException.UserInfoExceptionType.USER_NOT_EXIST);
        }
        UserInfo viewerInfo = optionalViewerInfo.get();

        HashMap<PostTag, HashMap<String, Long>> preferredWordMap = preferredWordRepository.findAllByUserId(userId);

//        start = System.currentTimeMillis();
//        log.info("========== findAllRecentPost ==========");
        List<Post.allPostIdWithTag> postList = postRepository.findAllRecentPost();
//        log.info(String.format("========== findAllRecentPost time: %d", System.currentTimeMillis() - start));

        HashMap<Post.allPostIdWithTag, Long> CBRecommendMap = new HashMap<>();
        for(Post.allPostIdWithTag post : postList) {
            if(preferredWordMap.get(post.getTag()) != null) {
                List<KeyWord> keyWordList = keyWordRepository.getKeyWordByPostId(post.getId());
                CBRecommendMap.put(post, getScore(preferredWordMap.get(post.getTag()), keyWordList));
            }
        }

        CBRecommendMap.entrySet().removeIf(e -> e.getValue() == 0);
//        CBRecommendMap.entrySet().removeIf(e -> redisUtil.filterReadPosts(userId, e.getKey().getId()));
        return CBRecommendMap.entrySet().stream().sorted(Map.Entry.comparingByValue())
                .limit(Constant.CB_RECOMMEND_POST_NUMBER)
                .map(e -> {
                    Post post = postRepository.getByPostId(e.getKey().getId());
                    starRecordRepository.setPostIsStarred(post, viewerInfo);
                    approvalRecordRepository.setCommentApprovalStatus(post.getHostComment(), viewerInfo);
                    return post;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Post> getCFRecommendations(Long userId) {
        Optional<UserInfo> optionalViewerInfo = userInfoRepository.findByUserId(userId);
        if (optionalViewerInfo.isEmpty()) {
            throw new UserInfoException(UserInfoException.UserInfoExceptionType.USER_NOT_EXIST);
        }
        UserInfo viewerInfo = optionalViewerInfo.get();
        return cfItemRepository.getCFItemByUserId(userId).stream().map(item -> {
            Post post = postRepository.getByPostId(item);
            starRecordRepository.setPostIsStarred(post, viewerInfo);
            approvalRecordRepository.setCommentApprovalStatus(post.getHostComment(), viewerInfo);
            return post;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateRecommendSystem(Long userId, Long postId, PreferenceDegree preferenceDegree){
        //Content-Based Recommendation
        UserInfo user = userInfoRepository.getById(userId);
        PostTag postTag = postRepository.getByPostId(postId).getTag();
        List<KeyWord> keyWordList = keyWordRepository.getKeyWordByPostId(postId);
        HashMap<String, PreferredWord.wordWithId> preferredWordMap = preferredWordRepository.findAllByUserIdAndPostTag(userId, postTag);
        for (KeyWord keyWord : keyWordList) {
            String word = keyWord.getWord();
            Long score = keyWord.getScore();
            switch (preferenceDegree) {
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

        //Collaborative-filter recommendation
        preferencePostRepository.addPreferencePostRecord(userId, postId, preferenceDegree);
    }

    @Override
    public void addNewPost(PostTag postTag, Long postId, String title, String content) {
        List<Keyword> keyWordList = recommendUtil.computeArticleTfidf(title, content, Constant.POST_KEYS_WORDS);
        List<KeyWord> ret = new LinkedList<>();
        for(Keyword keyword : keyWordList) {
            LogUtil.debug(keyword.toString());
            ret.add(new KeyWord(postId, keyword.getName(), (long) (keyword.getScore()) * 100));
        }
        keyWordRepository.saveNewPostKeyWord(ret);
    }

    @Override
    @Scheduled(cron = "0 0 0 * * *")
    public void updateCFItems() {
        List<Long> userIdList = userInfoRepository.getAllUserId().stream().map(UserInfo.UserInfoId::getUserId).collect(Collectors.toList());
        try {
            ReloadFromJDBCDataModel dataModel = recommendUtil.getDataSource();
            UserSimilarity similarity = new
                    EuclideanDistanceSimilarity(dataModel);
            /* other similarity algorithm
             *    LogLikelihoodSimilarity(dataModel);
             *    PearsonCorKrelationSimilarity(dataModel);
             *    UncenteredCosineSimilarity(dataModel);
             */
            UserNeighborhood neighborhood = new NearestNUserNeighborhood(Constant.NEAREST_N_USER, similarity, dataModel);

            Recommender recommender = new GenericUserBasedRecommender(dataModel, neighborhood, similarity);
            for(Long userId: userIdList) {
                List<Long> CFRecommendList = recommender.recommend(userId, Constant.CF_RECOMMEND_POST_NUMBER).stream().map(RecommendedItem::getItemID).collect(Collectors.toList());
                cfItemRepository.saveOneUserCFItem(userId, CFRecommendList);
            }
        } catch (TasteException e) {
            LogUtil.error(e);
            e.printStackTrace();
        }
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
}
