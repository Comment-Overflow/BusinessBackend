package com.privateboat.forum.backend.repositoryimpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.privateboat.forum.backend.dao.PostDAO;
import com.privateboat.forum.backend.entity.Post;
import com.privateboat.forum.backend.enumerate.PostTag;
import com.privateboat.forum.backend.exception.PostException;
import com.privateboat.forum.backend.repository.PostRepository;
import com.privateboat.forum.backend.util.Constant;
import lombok.AllArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

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
    public List<Post> findAllRecentPost() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.WEEK_OF_YEAR, Constant.RECOMMEND_EXPIRED_TIME);
        return postDAO.findAllByPostTimeAfter(new Timestamp(calendar.getTime().getTime()));
    }

    @Override
    public Page<Post> findByTag(PostTag tag, Pageable pageable) {
        return postDAO.findByTagAndIsDeletedOrderByLastCommentTimeDesc(tag, false, pageable);
    }

    @Override
    public Post save(Post post) {
        return postDAO.save(post);
    }

    @Override
    public Post getByPostId(Long postId) throws PostException {
        try {
            return postDAO.getById(postId);
        } catch (EntityNotFoundException e){
            throw new PostException(PostException.PostExceptionType.POST_NOT_EXIST);
        }
    }

    @Override
    public void delete(Post post) {
        post.setIsDeleted(true);
        postDAO.save(post);
    }

    @Override
    public List<Post> generateHotPosts(Integer limit) {
        PageRequest pageRequest = PageRequest.of(0, limit, Sort.by(Sort.Order.desc("hotIndex")));
        return postDAO.findAll(pageRequest).getContent();
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
}
