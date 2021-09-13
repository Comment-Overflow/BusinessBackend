package com.privateboat.forum.backend.repositoryimpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.privateboat.forum.backend.dao.PostDAO;
import com.privateboat.forum.backend.entity.Post;
import com.privateboat.forum.backend.entity.UserInfo;
import com.privateboat.forum.backend.enumerate.PostTag;
import com.privateboat.forum.backend.exception.PostException;
import com.privateboat.forum.backend.repository.PostRepository;
import com.privateboat.forum.backend.util.Constant;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.privateboat.forum.backend.util.Constant.REDIS_HOT_LIST_KEY;

@Repository
@AllArgsConstructor
public class PostRepositoryImpl implements PostRepository {
    private final PostDAO postDAO;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public Optional<Post> findByPostId(Long postId) {
        return postDAO.findById(postId);
    }

    @Override
    public Page<Post> findByUserId(Long userId, Pageable pageable) {
        return postDAO.findByUserInfo_IdAndIsDeletedOrderByPostTimeDesc(userId, false, pageable);
    }

    @Override
    public Page<Post> findAll(Pageable pageable) {
        return postDAO.findByIsDeletedOrderByLastCommentTimeDesc(false, pageable);
    }

    @Override
    public Page<Post> findByIsDeleted(boolean isDeleted, Pageable pageable) {
        return postDAO.findByIsDeleted(isDeleted, pageable);
    }

    @Override
    public List<Post.allPostIdWithTag> findAllRecentPost(UserInfo userInfo) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.WEEK_OF_YEAR, Constant.RECOMMEND_EXPIRED_TIME);
        return postDAO.findAllByPostTimeAfterAndUserInfoNot(new Timestamp(calendar.getTime().getTime()), userInfo);
    }

    @Override
    public Page<Post> findByTag(PostTag tag, Pageable pageable) {
        return postDAO.findByTagAndIsDeletedOrderByLastCommentTimeDesc(tag, false, pageable);
    }

    @Override
    public Post save(Post post) {
        return postDAO.save(post);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public Post saveAndFlush(Post post) {
        return postDAO.saveAndFlush(post);
    }

    @Override
    public Post getByPostId(Long postId) throws PostException {
        try {
            return postDAO.getById(postId);
        } catch (EntityNotFoundException e){
            throw new PostException(PostException.PostExceptionType.POST_NOT_EXIST);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void setIsDeletedAndFlush(Post post) {
        post.setIsDeleted(true);
        postDAO.save(post);
    }

    @Override
    public List<Post> generateHotPosts(Integer limit) {
        PageRequest pageRequest = PageRequest.of(0, limit, Sort.by(Sort.Order.desc("hotIndex")));
        return postDAO.findByIsDeleted(false, pageRequest).getContent();
    }

    @Override
    public List<Post> getHotPosts(Pageable pageable) {
        long pageNum = pageable.getPageNumber();
        long pageSize = pageable.getPageSize();
        long startIndex = pageNum * pageSize;
        return Objects.requireNonNull(redisTemplate
                .opsForList()
                .range(REDIS_HOT_LIST_KEY, startIndex, startIndex + pageSize))
                .stream()
                .map(object -> objectMapper.convertValue(object, Post.class))
                .collect(Collectors.toList());
    }

    @Override
    public Page<Post> findByTitleContainingAndIsDeletedOrderByPostTime(String searchKey,
                                                                       boolean isDeleted,
                                                                       Pageable pageable) {
        return postDAO.findByTitleContainingAndIsDeletedOrderByPostTime(
                searchKey, isDeleted, pageable
        );
    }

    @Override
    public Page<Post> findByTitleContainingAndTagAndIsDeletedOrderByPostTime(String searchKey,
                                                                             PostTag tag,
                                                                             boolean isDeleted,
                                                                             Pageable pageable) {
        return postDAO.findByTitleContainingAndTagAndIsDeletedOrderByPostTime(
                searchKey, tag, isDeleted, pageable
        );
    }
}
