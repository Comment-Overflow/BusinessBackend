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
import com.privateboat.forum.backend.enumerate.UserType;
import com.privateboat.forum.backend.exception.PostException;
import com.privateboat.forum.backend.repository.*;
import com.privateboat.forum.backend.service.PostService;
import com.privateboat.forum.backend.service.ReplyRecordService;
import com.privateboat.forum.backend.util.ImageUtil;
import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserInfoRepository userInfoRepository;
    private final ApprovalRecordRepository approvalRecordRepository;
    private final StarRecordRepository starRecordRepository;
    private final ReplyRecordService replyRecordService;
    private final UserStatisticRepository userStatisticRepository;

    private final Environment environment;
    private static final String imageFolderName = "comment/";

    @Override
    public Page<Post> findByTag(PostTag tag, Integer pageNum, Integer pageSize, Long userId) throws PostException {
        Optional<UserInfo> userInfo = userInfoRepository.findByUserId(userId);
        if (userInfo.isEmpty()) {
            throw new PostException(PostException.PostExceptionType.VIEWER_NOT_EXIST);
        }
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        Page<Post> posts = postRepository.findByTag(tag, pageable);
        for (Post post: posts.getContent()) {
            setPostTransientField(post, userInfo.get());
        }
        return posts;
    }

    @Override
    public Page<Post> findAll(Integer pageNum, Integer pageSize, Long userId) throws PostException {
        Optional<UserInfo> userInfo = userInfoRepository.findByUserId(userId);
        if (userInfo.isEmpty()) {
            throw new PostException(PostException.PostExceptionType.VIEWER_NOT_EXIST);
        }
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        Page<Post> posts = postRepository.findAll(pageable);
        for (Post post: posts.getContent()) {
            setPostTransientField(post, userInfo.get());
        }
        return posts;
    }

    @Transactional
    @Override
    public Post postPost(Long userId, NewPostDTO newPostDTO) throws PostException {
        Optional<UserInfo> userInfo = userInfoRepository.findByUserId(userId);
        if (userInfo.isEmpty()) {
            throw new PostException(PostException.PostExceptionType.POSTER_NOT_EXIST);
        }
        if (userInfo.get().getUserAuth().getUserType() == UserType.SILENCED) {
            throw new PostException(PostException.PostExceptionType.USER_SILENCED);
        }
        Post post = new Post(newPostDTO.getTitle(), newPostDTO.getTag());
        Comment hostComment = new Comment(post, userInfo.get(), 0L, newPostDTO.getContent());
        userInfo.get().getUserStatistic().addPost();
        userStatisticRepository.save(userInfo.get().getUserStatistic());
        post.setHostComment(hostComment);
        post.addComment(hostComment);

        for (MultipartFile imageFile : newPostDTO.getUploadFiles()) {
            String newName = ImageUtil.getNewImageName(imageFile);
            if (!ImageUtil.uploadImage(imageFile, newName, imageFolderName)) {
                throw new PostException(PostException.PostExceptionType.UPLOAD_IMAGE_FAILED);
            }
            String imageUrl = environment.getProperty("com.privateboat.forum.backend.image-base-url") + imageFolderName + newName;
            hostComment.getImageUrl().add(imageUrl);
            System.out.println(imageUrl);
        }

        postRepository.save(post);
        commentRepository.save(hostComment);
        return post;
    }

    @Transactional
    @Override
    public Comment postComment(Long userId, NewCommentDTO commentDTO) throws PostException {
        Optional<UserInfo> userInfo = userInfoRepository.findByUserId(userId);
        if (userInfo.isEmpty()) {
            throw new PostException(PostException.PostExceptionType.POSTER_NOT_EXIST);
        }
        if (userInfo.get().getUserAuth().getUserType() == UserType.SILENCED) {
            throw new PostException(PostException.PostExceptionType.USER_SILENCED);
        }
        Optional<Post> post = postRepository.findByPostId(commentDTO.getPostId());
        if (post.isEmpty()) {
            throw new PostException(PostException.PostExceptionType.POST_NOT_EXIST);
        }
        if (post.get().getIsFrozen()) {
            throw new PostException(PostException.PostExceptionType.POST_FROZEN);
        }
        Comment comment = new Comment(post.get(), userInfo.get(),
                commentDTO.getQuoteId(), commentDTO.getContent());
        post.get().addComment(comment);
        userInfo.get().getUserStatistic().addComment();
        userStatisticRepository.save(userInfo.get().getUserStatistic());
        commentRepository.save(comment);
        Long newCommentId = comment.getId();
        postRepository.save(post.get());

        Long postUserId = post.get().getUserInfo().getId();
        if (!postUserId.equals(userId)) {
            ReplyRecordReceiveDTO reply = new ReplyRecordReceiveDTO(postUserId, commentDTO.getPostId(), newCommentId, commentDTO.getQuoteId());
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
            if (!quoteUserId.equals(userId) && !quoteUserId.equals(postUserId)) {
                ReplyRecordReceiveDTO reply = new ReplyRecordReceiveDTO(
                        target.getUserInfo().getId(),
                        commentDTO.getPostId(),
                        newCommentId,
                        commentDTO.getQuoteId());
                replyRecordService.postReplyRecord(userId, reply);
            }
        }

        for (MultipartFile imageFile : commentDTO.getUploadFiles()) {
            String newName = ImageUtil.getNewImageName(imageFile);
            if (!ImageUtil.uploadImage(imageFile, newName, imageFolderName)) {
                throw new PostException(PostException.PostExceptionType.UPLOAD_IMAGE_FAILED);
            }
            String imageUrl = environment.getProperty("com.privateboat.forum.backend.image-base-url") + imageFolderName + newName;
            comment.getImageUrl().add(imageUrl);
        }

        return comment;
    }

    @Override
    public Post getPost(Long postId, Long userId) throws PostException {
        Optional<Post> optionalPost = postRepository.findByPostId(postId);
        if (optionalPost.isEmpty()) {
            throw new PostException(PostException.PostExceptionType.POST_NOT_EXIST);
        }
        Optional<UserInfo> userInfo = userInfoRepository.findByUserId(userId);
        if (userInfo.isEmpty()) {
            throw new PostException(PostException.PostExceptionType.VIEWER_NOT_EXIST);
        }

        Post post = optionalPost.get();
        post.setIsStarred(starRecordRepository.checkIfHaveStarred(userInfo.get(), optionalPost.get()));
        return post;
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
        for (Comment comment: comments.getContent()) {
            if (comment.getFloor() == 0) host = comment;
            comment.setApprovalStatus(approvalRecordRepository.checkIfHaveApproved(userInfo.get(), comment));
            if (comment.getIsDeleted()) {
                comment.setContent("");
                comment.setImageUrl(new ArrayList<>());
                comment.setQuoteId(0L);
                continue;
            }
            if (comment.getQuoteId() != 0) {
                Comment quoteComment = commentRepository.getById(comment.getQuoteId());
                QuoteDTO quoteDTO = new QuoteDTO(quoteComment);
                if (quoteComment.getIsDeleted()) {
                    quoteDTO.setContent("内容已被删除");
                }
                comment.setQuoteDTO(quoteDTO);
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

    @Transactional
    @Override
    public void deletePost(Long postId, Long userId) throws PostException {
        Optional<UserInfo> userInfo = userInfoRepository.findByUserId(userId);
        if (userInfo.isEmpty()) {
            throw new PostException(PostException.PostExceptionType.VIEWER_NOT_EXIST);
        }
        Optional<Post> post = postRepository.findByPostId(postId);
        if (post.isEmpty()) {
            throw new PostException(PostException.PostExceptionType.POST_NOT_EXIST);
        }
        if ((post.get().getUserInfo() != userInfo.get() || post.get().getIsFrozen()) &&
                userInfo.get().getUserAuth().getUserType() != UserType.ADMIN) {
            throw new PostException(PostException.PostExceptionType.PERMISSION_DENIED);
        }
        post.get().getUserInfo().getUserStatistic().subPost();
        userStatisticRepository.save(post.get().getUserInfo().getUserStatistic());
        postRepository.delete(post.get());
    }

    @Transactional
    @Override
    public void deleteComment(Long commentId, Long userId) throws PostException {
        Optional<UserInfo> userInfo = userInfoRepository.findByUserId(userId);
        if (userInfo.isEmpty()) {
            throw new PostException(PostException.PostExceptionType.VIEWER_NOT_EXIST);
        }
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (comment.isEmpty()) {
            throw new PostException(PostException.PostExceptionType.COMMENT_NOT_EXIST);
        }
        Post post = comment.get().getPost();
        if ((comment.get().getUserInfo() != userInfo.get() || post.getIsFrozen()) &&
                userInfo.get().getUserAuth().getUserType() != UserType.ADMIN) {
            throw new PostException(PostException.PostExceptionType.PERMISSION_DENIED);
        }
        post.deleteComment(comment.get());
        comment.get().getUserInfo().getUserStatistic().subComment();
        userStatisticRepository.save(comment.get().getUserInfo().getUserStatistic());
        postRepository.save(post);
        commentRepository.delete(comment.get());
    }

    @Override
    public Post getPostByComment(Long commentId, Long userId) throws PostException {
        Optional<UserInfo> userInfo = userInfoRepository.findByUserId(userId);
        if (userInfo.isEmpty()) {
            throw new PostException(PostException.PostExceptionType.VIEWER_NOT_EXIST);
        }
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (comment.isEmpty()) {
            throw new PostException(PostException.PostExceptionType.COMMENT_NOT_EXIST);
        }
        Post post = comment.get().getPost();
        post.setIsStarred(starRecordRepository.checkIfHaveStarred(userInfo.get(), post));
        return post;
    }

    @Override
    public void setPostTransientField(Post post, UserInfo userInfo) {
        Comment hostComment = post.getComments().get(0);
        hostComment.setApprovalStatus(approvalRecordRepository.checkIfHaveApproved(userInfo, hostComment));
        post.setIsStarred(starRecordRepository.checkIfHaveStarred(userInfo, post));
    }
}
