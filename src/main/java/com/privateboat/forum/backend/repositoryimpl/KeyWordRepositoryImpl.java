package com.privateboat.forum.backend.repositoryimpl;

import com.privateboat.forum.backend.dao.KeyWordDAO;
import com.privateboat.forum.backend.entity.KeyWord;
import com.privateboat.forum.backend.repository.KeyWordRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AllArgsConstructor
public class KeyWordRepositoryImpl implements KeyWordRepository {

    private final KeyWordDAO keyWordDAO;

    @Override
    public List<KeyWord> getKeyWordByPostId(Long postId) {
        return keyWordDAO.getKeyWordsByPostId(postId);
    }

    @Override
    public void saveNewPostKeyWord(List<KeyWord> keyWordList) {
        keyWordDAO.saveAll(keyWordList);
    }
}
