package com.privateboat.forum.backend.serviceimpl;

import com.privateboat.forum.backend.dto.QuoteDTO;
import com.privateboat.forum.backend.dto.request.NewCommentDTO;
import com.privateboat.forum.backend.dto.request.NewPostDTO;
import com.privateboat.forum.backend.dto.request.ReplyRecordReceiveDTO;
import com.privateboat.forum.backend.dto.response.HotPostDTO;
import com.privateboat.forum.backend.dto.response.PageDTO;
import com.privateboat.forum.backend.dto.response.SearchedCommentDTO;
import com.privateboat.forum.backend.entity.*;
import com.privateboat.forum.backend.enumerate.PostTag;
import com.privateboat.forum.backend.enumerate.PreferDegree;
import com.privateboat.forum.backend.enumerate.SortPolicy;
import com.privateboat.forum.backend.enumerate.UserType;
import com.privateboat.forum.backend.exception.PostException;
import com.privateboat.forum.backend.rabbitmq.MQSender;
import com.privateboat.forum.backend.repository.*;
import com.privateboat.forum.backend.service.PostService;
import com.privateboat.forum.backend.service.RecommendService;
import com.privateboat.forum.backend.util.audit.TextAuditResult;
import com.privateboat.forum.backend.util.audit.TextAuditResultType;
import com.privateboat.forum.backend.util.audit.TextAuditUtil;
import com.privateboat.forum.backend.util.image.ImageAuditException;
import com.privateboat.forum.backend.util.image.ImageUploadException;
import com.privateboat.forum.backend.util.image.ImageUtil;
import com.privateboat.forum.backend.util.RedisUtil;
import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.LinkedList;
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
    private final UserStatisticRepository userStatisticRepository;
    private final RecommendService recommendService;

    private final RedisUtil redisUtil;
    private final MQSender mqSender;

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

    @Override
    public Page<Post> findOnesPosts(Long userId, Integer pageNum, Integer pageSize, Long myUserId) {
        UserInfo userInfo = userInfoRepository.getById(myUserId);
        Page<Post> postPage = postRepository.findByUserId(userId, PageRequest.of(pageNum, pageSize));
        for(Post post: postPage.getContent()) {
            setPostTransientField(post, userInfo);
        }
        return postPage;
    }

    @Override
    public Page<Post> findStarredPosts(Long userId, Integer pageNum, Integer pageSize) {
        UserInfo userInfo = userInfoRepository.getById(userId);
        Page<StarRecord> starRecordPage = starRecordRepository.getMyStarRecords(userId, PageRequest.of(pageNum, pageSize));
        List<Post> postList = new LinkedList<>();
        for (StarRecord starRecord : starRecordPage.getContent()) {
            postList.add(starRecord.getPost());
        }
        for (Post post : postList) {
            setPostTransientField(post, userInfo);
        }
        return new PageImpl<>(postList);
    }

    @Transactional
    @Override
    public Post postPost(Long userId, NewPostDTO newPostDTO) throws PostException {
        Optional<UserInfo> optionalSenderInfo = userInfoRepository.findByUserId(userId);
        if (optionalSenderInfo.isEmpty()) {
            throw new PostException(PostException.PostExceptionType.POSTER_NOT_EXIST);
        }
        UserInfo senderInfo = optionalSenderInfo.get();
        checkSilence(senderInfo);

        String newTitle = newPostDTO.getTitle();
        // Audit post title.
        if (newTitle == null || newTitle.isEmpty()) {
            throw new PostException(PostException.PostExceptionType.EMPTY_TITLE);
        }
        auditPostContent(newPostDTO.getTitle());
        // Audit post content.
        if (!newPostDTO.getContent().isEmpty()) {
            auditPostContent(newPostDTO.getContent());
        }

        Post newPost = new Post(newTitle, newPostDTO.getTag());
        Comment hostComment = new Comment(newPost, senderInfo, 0L, newPostDTO.getContent());
        newPost.setHostComment(hostComment);
        newPost.addComment(hostComment);
        newPost.setKeyWordList(recommendService.addNewPost(newPostDTO.getTag(), newPost.getId(), newPostDTO.getTitle(), newPostDTO.getContent()));

        // Change user statistics.
        mqSender.addPostCount(userId);
//        UserStatistic senderStatistic = senderInfo.getUserStatistic();
//        senderStatistic.addPost();
//        userStatisticRepository.save(senderStatistic);


        addAndUploadImage(hostComment, newPostDTO.getUploadFiles());

        postRepository.save(newPost);
        redisUtil.addPostCounter();
        redisUtil.addActiveUserCounter(userId);
        commentRepository.save(hostComment);
        return newPost;
    }

    @Transactional
    @Override
    public Comment postComment(Long userId, NewCommentDTO commentDTO) throws PostException {
        Optional<UserInfo> optionalSenderInfo = userInfoRepository.findByUserId(userId);
        if (optionalSenderInfo.isEmpty()) {
            throw new PostException(PostException.PostExceptionType.POSTER_NOT_EXIST);
        }

        UserInfo senderInfo = optionalSenderInfo.get();
        checkSilence(senderInfo);

        Optional<Post> optionalPost = postRepository.findByPostId(commentDTO.getPostId());
        if (optionalPost.isEmpty()) {
            throw new PostException(PostException.PostExceptionType.POST_NOT_EXIST);
        }
        Post post = optionalPost.get();
        auditPostContent(commentDTO.getContent());
        if (post.getIsFrozen()) {
            throw new PostException(PostException.PostExceptionType.POST_FROZEN);
        }
        Comment newComment = new Comment(post, optionalSenderInfo.get(),
                commentDTO.getQuoteId(), commentDTO.getContent());
        post.addComment(newComment);
        post.setLastCommentTime(newComment.getTime());
//        optionalSenderInfo.get().getUserStatistic().addComment();
//        userStatisticRepository.save(optionalSenderInfo.get().getUserStatistic());
        mqSender.addCommentCount(userId);
        commentRepository.save(newComment);
        Long newCommentId = newComment.getId();
        postRepository.save(post);
        recommendService.updatePreferredWordList(userId, commentDTO.getPostId(), PreferDegree.REPLY);
        Long postUserId = post.getUserInfo().getId();
        if (!postUserId.equals(userId)) {
            ReplyRecordReceiveDTO reply = new ReplyRecordReceiveDTO(postUserId, commentDTO.getPostId(), newCommentId, commentDTO.getQuoteId());
            // replyRecordService.postReplyRecord(userId, reply);
            mqSender.sendReplyMessage(userId, reply);
        }
        if (newComment.getQuoteId() != 0) {
            List<Comment> finder =
                    post.getComments().stream().filter(
                            c -> c.getId().equals(newComment.getQuoteId())
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
                // replyRecordService.postReplyRecord(userId, reply);
                mqSender.sendReplyMessage(userId, reply);
            }
        }

        addAndUploadImage(newComment, commentDTO.getUploadFiles());
        updateCache(post.getId(), newComment.getFloor(), 8);

        redisUtil.addCommentCounter();
        redisUtil.addActiveUserCounter(userId);
        return newComment;
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


        Optional<Post> optionalPost = postRepository.findByPostId(postId);
        if (optionalPost.isEmpty()) {
            throw new PostException(PostException.PostExceptionType.POST_NOT_EXIST);
        }


        Sort.Direction direction = policy == SortPolicy.EARLIEST ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by(direction, "floor"));


        PageDTO<Comment> comments = commentRepository.findByPostId(postId, pageable);
        comments.setSize(optionalPost.get().getCommentCount().longValue());


        Comment host = null;
        for (Comment comment: comments.getContent()) {
            comment.setUserInfo(userInfoRepository.getById(comment.getUserInfo().getId()));
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

            if (comment.getFloor() == 0) host = comment;
        }

        if (host != null) {
            List<Comment> commentList = new ArrayList<>(comments.getContent());
            commentList.remove(host);
            commentList.add(0, host);
            recommendService.updatePreferredWordList(userId, postId, PreferDegree.BROWSE);
            redisUtil.addViewCounter(userId, postId);
            return new PageDTO<>(commentList, comments.getSize());
        }

        return comments;
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
//        post.get().getUserInfo().getUserStatistic().subPost();
//        userStatisticRepository.save(post.get().getUserInfo().getUserStatistic());
        mqSender.subPostCount(userId);
        postRepository.delete(post.get());
    }

    @Transactional
    @Override
    public void deleteComment(Long commentId, Long userId) throws PostException {
        Optional<UserInfo> optionalViewerInfo = userInfoRepository.findByUserId(userId);
        if (optionalViewerInfo.isEmpty()) {
            throw new PostException(PostException.PostExceptionType.VIEWER_NOT_EXIST);
        }
        UserInfo viewerInfo = optionalViewerInfo.get();

        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        if (optionalComment.isEmpty()) {
            throw new PostException(PostException.PostExceptionType.COMMENT_NOT_EXIST);
        }
        Comment comment = optionalComment.get();
        UserInfo senderInfo = comment.getUserInfo();

        Post post = comment.getPost();
        // Non-admin trying to delete a frozen post or deleting someone else's post.
        if ((!senderInfo.getId().equals(viewerInfo.getId()) || post.getIsFrozen()) &&
                viewerInfo.getUserAuth().getUserType() != UserType.ADMIN) {
            throw new PostException(PostException.PostExceptionType.PERMISSION_DENIED);
        }
        post.deleteComment(comment);
//        senderInfo.getUserStatistic().subComment();
//        userStatisticRepository.save(comment.getUserInfo().getUserStatistic());
        mqSender.subCommentCount(userId);
        postRepository.save(post);
        commentRepository.delete(comment);
        mqSender.sendCacheUpdateMessage(post.getId(), comment.getFloor(), 8);
    }

    @Override
    public List<SearchedCommentDTO> findMyComments(Long userId, Integer pageNum, Integer pageSize) {
        List<Comment> myComments = commentRepository.getMyComments(userId, PageRequest.of(pageNum, pageSize)).getContent();
        removeQuoteId(myComments);
        return wrapSearchedCommentsWithPost(myComments);
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
        post.getHostComment().setApprovalStatus(
                approvalRecordRepository.checkIfHaveApproved(userInfo, post.getHostComment()));
        post.setIsStarred(starRecordRepository.checkIfHaveStarred(userInfo, post));
    }

    @Override
    public List<HotPostDTO> getHotList(Integer pageNum, Integer pageSize) {
        List<Post> hottestPosts = postRepository.getHotPosts(PageRequest.of(pageNum, pageSize));
        return hottestPosts.stream().map(post -> {
            setPostTransientField(post, post.getUserInfo());
            return new HotPostDTO(post);
        }).collect(Collectors.toList());
    }

    public List<SearchedCommentDTO> wrapSearchedCommentsWithPost(List<Comment> comments) {
        return comments.stream().map(comment -> {
            Post parentPost = comment.getPost();
            setPostTransientField(parentPost, comment.getUserInfo());
            return new SearchedCommentDTO(parentPost, comment);
        }).collect(Collectors.toList());
    }

    public void removeQuoteId(List<Comment> comments) {
        for (Comment comment: comments) {
            comment.setQuoteId(0L);
        }
    }

    private void auditPostContent(String text) {
        TextAuditResult textAuditResult = TextAuditUtil.auditText(text);
        if (textAuditResult.getResultType() == TextAuditResultType.NOT_OK) {
            throw new PostException(PostException.PostExceptionType.ILLEGAL_TEXT);
        }
    }

    private void checkSilence(UserInfo senderInfo) {
        if (senderInfo.getUserAuth().getUserType() == UserType.SILENCED) {
            throw new PostException(PostException.PostExceptionType.USER_SILENCED);
        }
    }

    private void addAndUploadImage(Comment comment, List<MultipartFile> images) {
        List<String> imageUrlList = comment.getImageUrl();
        for (MultipartFile imageFile : images) {
            String newName = ImageUtil.getNewImageName(imageFile);
            try {
                ImageUtil.uploadImage(imageFile, newName, imageFolderName);
            } catch (ImageAuditException e) {
                if (e.getResult().isConfirmed()) {
                    throw new PostException(PostException.PostExceptionType.ILLEGAL_IMAGE);
                }
            } catch (ImageUploadException e) {
                throw new PostException(PostException.PostExceptionType.UPLOAD_IMAGE_FAILED);
            }
            String imageUrl = environment.getProperty("com.privateboat.forum.backend.image-base-url") + imageFolderName + newName;
            imageUrlList.add(imageUrl);
        }
    }

    private void updateCache(Long postId, Integer commentFloor, Integer pageSize) {
        int pageNum = commentFloor / pageSize;
        Pageable pageable = PageRequest.of(pageNum, pageSize,
                Sort.by(Sort.Direction.ASC, "floor"));
        // mqSender.sendCacheUpdateMessage(postId, pageNum, pageSize);
        commentRepository.updateCommentCache(postId, pageable);
    }
}
