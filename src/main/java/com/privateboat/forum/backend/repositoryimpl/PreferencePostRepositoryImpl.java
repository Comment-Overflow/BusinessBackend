package com.privateboat.forum.backend.repositoryimpl;

import com.privateboat.forum.backend.dao.PreferencePostDAO;
import com.privateboat.forum.backend.entity.PreferencePost;
import com.privateboat.forum.backend.entity.PreferencePostID;
import com.privateboat.forum.backend.enumerate.PreferenceDegree;
import com.privateboat.forum.backend.repository.PreferencePostRepository;
import com.privateboat.forum.backend.util.RecommendUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class PreferencePostRepositoryImpl implements PreferencePostRepository {

    private final PreferencePostDAO preferencePostDAO;

    @Override
    public void addPreferencePostRecord(Long userId, Long postId, PreferenceDegree preferenceDegree) {
        Optional<PreferencePost> preferencePost = preferencePostDAO.findById(new PreferencePostID(userId, postId));
        if(preferencePost.isPresent()){
            PreferenceDegree previousDegree = RecommendUtil.VALUE2DEGREE.get(preferencePost.get().getPreferenceDegree());
            switch (preferenceDegree) {
                case BROWSE:
                    break;
                case STAR:
                    if(!previousDegree.equals(PreferenceDegree.STAR)) {
                        preferencePost.get().setPreferenceDegree(RecommendUtil.DEGREE2VALUE.get(PreferenceDegree.STAR));
                        preferencePostDAO.save(preferencePost.get());
                    }
                    break;
                case REPLY:
                    if(previousDegree.equals(PreferenceDegree.BROWSE)) {
                        preferencePost.get().setPreferenceDegree(RecommendUtil.DEGREE2VALUE.get(PreferenceDegree.REPLY));
                        preferencePostDAO.save(preferencePost.get());
                    }
                    break;
            }
        } else {
            PreferencePost newPreferencePost = new PreferencePost(userId, postId, RecommendUtil.DEGREE2VALUE.get(preferenceDegree), new Timestamp(System.currentTimeMillis()));
            preferencePostDAO.save(newPreferencePost);
        }
    }

    @Override
    public void clearExpiredRecord() {

    }
}
