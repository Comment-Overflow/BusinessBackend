package com.privateboat.forum.backend.repositoryimpl;

import com.privateboat.forum.backend.dao.PreferredWordDAO;
import com.privateboat.forum.backend.entity.PreferredWord;
import com.privateboat.forum.backend.enumerate.PostTag;
import com.privateboat.forum.backend.repository.PreferredWordRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Repository
@AllArgsConstructor
public class PreferredWordRepositoryImpl implements PreferredWordRepository {
    private final PreferredWordDAO preferredWordDAO;

    @Override
    public HashMap<PostTag, HashMap<String, Long>> findAllByUserId(Long userId) {
        HashMap<PostTag, HashMap<String, Long>> ret = new HashMap<>();
        List<PreferredWord> preferredWordList = preferredWordDAO.findAllByUserId(userId);
        for(PreferredWord word : preferredWordList) {
            PostTag postTag = word.getPostTag();
            if(ret.get(postTag) == null) {
                HashMap<String, Long> newHashMap = new HashMap<>();
                newHashMap.put(word.getWord(), word.getScore());
                ret.put(postTag, newHashMap);
            } else {
              ret.get(postTag).put(word.getWord(), word.getScore());
            }
        }
        return ret;
    }

    @Override
    public HashMap<String, PreferredWord.wordWithId> findAllByUserIdAndPostTag(Long userId, PostTag postTag) {
        List<PreferredWord.wordWithId> preferredWordList = preferredWordDAO.findAllByUserIdAndPostTag(userId, postTag);
        HashMap<String, PreferredWord.wordWithId> ret = new HashMap<>();
        for(PreferredWord.wordWithId preferredWord : preferredWordList) {
            ret.put(preferredWord.getWord(), preferredWord);
        }
        return ret;
    }

    @Override
    public void addPreferredWord(PreferredWord preferredWord) {
        preferredWordDAO.saveAndFlush(preferredWord);
    }

    @Override
    @Transactional
    public void updatePreferredWord(Long wordId, Long score) {
        preferredWordDAO.getById(wordId).setScore(score);
    }
}
