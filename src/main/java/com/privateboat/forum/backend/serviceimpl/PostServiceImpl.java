package com.privateboat.forum.backend.serviceimpl;

import com.privateboat.forum.backend.dto.QuoteDTO;
import com.privateboat.forum.backend.dto.request.NewCommentDTO;
import com.privateboat.forum.backend.dto.request.NewPostDTO;
import com.privateboat.forum.backend.dto.request.ReplyRecordReceiveDTO;
import com.privateboat.forum.backend.dto.response.PageDTO;
import com.privateboat.forum.backend.entity.Comment;
import com.privateboat.forum.backend.entity.Post;
import com.privateboat.forum.backend.entity.UserInfo;
import com.privateboat.forum.backend.enumerate.PostTag;
import com.privateboat.forum.backend.enumerate.SortPolicy;
import com.privateboat.forum.backend.exception.PostException;
import com.privateboat.forum.backend.repository.*;
import com.privateboat.forum.backend.service.PostService;
import com.privateboat.forum.backend.service.ReplyRecordService;
import com.privateboat.forum.backend.util.ImageUtil;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@Component
@AllArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserInfoRepository userInfoRepository;
    private final ApprovalRecordRepository approvalRecordRepository;
    private final StarRecordRepository starRecordRepository;

    private final ReplyRecordService replyRecordService;

    private static final String imageFolderName = "comment/";
    private static final String baseUrl = "http://192.168.0.101:8088/images/";

    @Override
    public Page<Post> findByTag(PostTag tag, Integer pageNum, Integer pageSize, Long userId) throws PostException {
        Optional<UserInfo> userInfo = userInfoRepository.findByUserId(userId);
        if (userInfo.isEmpty()) {
            throw new PostException(PostException.PostExceptionType.POSTER_NOT_EXIST);
        }
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        Page<Post> posts = postRepository.findByTag(tag, pageable);
        for (Post post : posts.getContent()) {
            Comment hostComment = post.getHostComment();
            hostComment.setApprovalStatus(approvalRecordRepository.checkIfHaveApproved(userInfo.get(), hostComment));
            post.setIsStarred(starRecordRepository.checkIfHaveStarred(userInfo.get(), post));
        }
        return posts;
    }

    @Override
    public Page<Post> findAll(Integer pageNum, Integer pageSize, Long userId) throws PostException {
        Optional<UserInfo> userInfo = userInfoRepository.findByUserId(userId);
        if (userInfo.isEmpty()) {
            throw new PostException(PostException.PostExceptionType.POSTER_NOT_EXIST);
        }
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        Page<Post> posts = postRepository.findAll(pageable);
        for (Post post : posts.getContent()) {
            Comment hostComment = post.getHostComment();
            hostComment.setApprovalStatus(approvalRecordRepository.checkIfHaveApproved(userInfo.get(), hostComment));
            post.setIsStarred(starRecordRepository.checkIfHaveStarred(userInfo.get(), post));
        }
        return posts;
    }

    @Override
    public Post postPost(Long userId, NewPostDTO newPostDTO) throws PostException {
        Optional<UserInfo> userInfo = userInfoRepository.findByUserId(userId);
        if (userInfo.isEmpty()) {
            throw new PostException(PostException.PostExceptionType.POSTER_NOT_EXIST);
        }
        Post post = new Post(newPostDTO.getTitle(), newPostDTO.getTag());
        Comment hostComment = new Comment(post, userInfo.get(), 0L, newPostDTO.getContent());
        post.setHostComment(hostComment);
        post.addComment(hostComment);

        for (MultipartFile imageFile : newPostDTO.getUploadFiles()) {
            String newName = getNewImageName(imageFile);
            if (!ImageUtil.uploadImage(imageFile, newName, imageFolderName)) {
                throw new PostException(PostException.PostExceptionType.UPLOAD_IMAGE_FAILED);
            }
            hostComment.getImageUrl().add(baseUrl + imageFolderName + newName);
            System.out.println(baseUrl + imageFolderName + newName);
        }

        postRepository.save(post);
        commentRepository.save(hostComment);
        return post;
    }

    @Override
    public Comment postComment(Long userId, NewCommentDTO commentDTO) throws PostException {
        Optional<UserInfo> userInfo = userInfoRepository.findByUserId(userId);
        if (userInfo.isEmpty()) {
            throw new PostException(PostException.PostExceptionType.POSTER_NOT_EXIST);
        }
        Optional<Post> post = postRepository.findByPostId(commentDTO.getPostId());
        if (post.isEmpty()) {
            throw new PostException(PostException.PostExceptionType.POST_NOT_EXIST);
        }
        Comment comment = new Comment(post.get(), userInfo.get(),
                commentDTO.getQuoteId(), commentDTO.getContent());
        post.get().addComment(comment);
        commentRepository.save(comment);

        Long postUserId = post.get().getUserInfo().getId();
        if (!postUserId.equals(userId)) {
            ReplyRecordReceiveDTO reply = new ReplyRecordReceiveDTO(postUserId, commentDTO.getPostId(), 0);
            replyRecordService.postReplyRecord(userId, reply);
        }
        if (comment.getQuoteId() != 0) {
            List<Comment> finder =
                    post.get().getComments().stream().filter(
                            c -> c.getId().equals(comment.getQuoteId())
                    ).collect(Collectors.toList());
            if (finder.size() != 1) throw new PostException(PostException.PostExceptionType.QUOTE_OUT_OF_BOUND);
            Comment target = finder.get(0);
            Long quoteUserId = target.getUserInfo().getId();
            if (!quoteUserId.equals(userId)) {
                ReplyRecordReceiveDTO reply = new ReplyRecordReceiveDTO(
                        target.getUserInfo().getId(),
                        commentDTO.getPostId(),
                        target.getFloor());
                replyRecordService.postReplyRecord(userId, reply);
            }
        }

        for (MultipartFile imageFile : commentDTO.getUploadFiles()) {
            String newName = getNewImageName(imageFile);
            if (!ImageUtil.uploadImage(imageFile, newName, imageFolderName)) {
                throw new PostException(PostException.PostExceptionType.UPLOAD_IMAGE_FAILED);
            }
            comment.getImageUrl().add(baseUrl + imageFolderName + newName);
        }

        return comment;
    }

    @Override
    @Deprecated
    public Post getPost(Long postId, Long userId) throws PostException {
        Optional<Post> post = postRepository.findByPostId(postId);
        if (post.isEmpty()) {
            throw new PostException(PostException.PostExceptionType.POST_NOT_EXIST);
        }
        Optional<UserInfo> userInfo = userInfoRepository.findByUserId(userId);
        if (userInfo.isEmpty()) {
            throw new PostException(PostException.PostExceptionType.VIEWER_NOT_EXIST);
        }
        Comment host = new Comment();
        for (Comment comment : post.get().getComments()) {
            if (comment.getFloor() == 0) host = comment;
            comment.setApprovalStatus(approvalRecordRepository.checkIfHaveApproved(userInfo.get(), comment));
            if (comment.getQuoteId() != 0) {
                comment.setQuoteDTO(new QuoteDTO(commentRepository.getById(comment.getQuoteId())));
            }
        }
        if(post.get().getComments().remove(host)) {
            post.get().getComments().add(0, host);
        }
        post.get().setIsStarred(starRecordRepository.checkIfHaveStarred(userInfo.get(), post.get()));
        return post.get();
    }

    @Override
    public PageDTO<Comment> findByPostIdOrderByPolicy(Long postId, SortPolicy policy,
                                                   Integer pageNum, Integer pageSize, Long userId) {
        Optional<UserInfo> userInfo = userInfoRepository.findByUserId(userId);
        if (userInfo.isEmpty()) {
            throw new PostException(PostException.PostExceptionType.VIEWER_NOT_EXIST);
        }
        Sort.Direction direction = policy == SortPolicy.EARLIEST ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by(direction, "floor"));
        Page<Comment> comments = commentRepository.findByPostId(postId, pageable);

        Comment host = null;
        for (Comment comment : comments.getContent()) {
            if (comment.getFloor() == 0) host = comment;
            comment.setApprovalStatus(approvalRecordRepository.checkIfHaveApproved(userInfo.get(), comment));
            if (comment.getQuoteId() != 0) {
                comment.setQuoteDTO(new QuoteDTO(commentRepository.getById(comment.getQuoteId())));
            }
        }
        if (host != null) {
            List<Comment> commentList = new ArrayList<>(comments.getContent());
            commentList.remove(host);
            commentList.add(0, host);
            return new PageDTO<>(commentList, comments.getTotalElements());
        }


        return new PageDTO<>(comments);
    }

    @Override
    public void deletePost(Long postId) throws PostException {
        Optional<Post> post = postRepository.findByPostId(postId);
        if (post.isEmpty()) {
            throw new PostException(PostException.PostExceptionType.POST_NOT_EXIST);
        }
        postRepository.delete(post.get());
    }

    @Override
    public void deleteComment(Long commentId) throws PostException {
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (comment.isEmpty()) {
            throw new PostException(PostException.PostExceptionType.COMMENT_NOT_EXIST);
        }
        Post post = comment.get().getPost();
        post.deleteComment(comment.get());
        postRepository.save(post);
        commentRepository.delete(comment.get());
    }

    private String getNewImageName(MultipartFile file) {
        String originName = file.getOriginalFilename();
        assert originName != null;
        String suffix = originName.substring(originName.lastIndexOf("."));
        return RandomStringUtils.randomAlphanumeric(12) + suffix;
    }
}
