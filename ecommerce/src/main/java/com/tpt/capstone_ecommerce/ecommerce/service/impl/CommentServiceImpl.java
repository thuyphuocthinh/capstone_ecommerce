package com.tpt.capstone_ecommerce.ecommerce.service.impl;

import com.tpt.capstone_ecommerce.ecommerce.constant.*;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.CreateCommentRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.UpdateCommentRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.APISuccessResponseWithMetadata;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.CommentDetailResponse;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.PaginationMetadata;
import com.tpt.capstone_ecommerce.ecommerce.entity.Comment;
import com.tpt.capstone_ecommerce.ecommerce.entity.Spu;
import com.tpt.capstone_ecommerce.ecommerce.entity.User;
import com.tpt.capstone_ecommerce.ecommerce.enums.NOTIFICATION_TYPE;
import com.tpt.capstone_ecommerce.ecommerce.exception.NotFoundException;
import com.tpt.capstone_ecommerce.ecommerce.repository.CommentRepository;
import com.tpt.capstone_ecommerce.ecommerce.repository.SpuRepository;
import com.tpt.capstone_ecommerce.ecommerce.repository.UserRepository;
import com.tpt.capstone_ecommerce.ecommerce.service.CommentService;
import com.tpt.capstone_ecommerce.ecommerce.service.NotificationService;
import com.tpt.capstone_ecommerce.ecommerce.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final SpuRepository spuRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Override
    public String createComment(String spuId, CreateCommentRequest request) throws NotFoundException, BadRequestException {
        Spu findSpu = this.spuRepository.findById(spuId)
                .orElseThrow(() -> new NotFoundException(SpuErrorConstant.SPU_NOT_FOUND));

        User findUser = this.userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException(UserErrorConstant.USER_NOT_FOUND));

        Comment comment = Comment.builder()
                .spu(findSpu)
                .content(request.getContent())
                .user(findUser)
                .build();

        Comment savedComment = this.commentRepository.save(comment);

        this.notificationService.addNewNotificationForShop(findSpu.getShop().getId(), spuId, NOTIFICATION_TYPE.NEW_REVIEW, request.getContent());
        this.notificationService.addNewNotificationForUser(findUser.getEmail(), spuId, NOTIFICATION_TYPE.NEW_REVIEW, request.getContent());

        return savedComment.getId();
    }

    @Override
    public String updateComment(String commentId, UpdateCommentRequest request) throws NotFoundException, BadRequestException {
        Comment findComment = this.commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(CommentErrorConstant.COMMENT_NOT_FOUND));

        String currentUserId = SecurityUtils.getCurrentUserId();
        if(!currentUserId.equals(findComment.getUser().getId())) {
            throw new BadRequestException(AuthConstantError.NOT_ALLOWED_TO_ACCESS_RESOURCE);
        }

        String content = request.getContent();
        findComment.setContent(content);
        Comment updatedComment = this.commentRepository.save(findComment);
        return updatedComment.getId();
    }

    @Override
    public String deleteComment(String commentId) throws NotFoundException, BadRequestException {
        Comment findComment = this.commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(CommentErrorConstant.COMMENT_NOT_FOUND));

        String currentUserId = SecurityUtils.getCurrentUserId();
        if(!currentUserId.equals(findComment.getUser().getId())) {
            throw new BadRequestException(AuthConstantError.NOT_ALLOWED_TO_ACCESS_RESOURCE);
        }

        this.commentRepository.delete(findComment);
        return "Success";
    }

    @Override
    public String replyComment(String commentParentId, CreateCommentRequest request) throws NotFoundException, BadRequestException {
        Comment findComment = this.commentRepository.findById(commentParentId)
                .orElseThrow(() -> new NotFoundException(CommentErrorConstant.COMMENT_NOT_FOUND));

        User findUser = this.userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException(UserErrorConstant.USER_NOT_FOUND));

        Comment comment = Comment.builder()
                .spu(findComment.getSpu())
                .content(request.getContent())
                .user(findUser)
                .parentId(findComment.getId())
                .build();

        Comment savedComment = this.commentRepository.save(comment);

        Spu spu = findComment.getSpu();
        this.notificationService.addNewNotificationForShop(spu.getShop().getId(), spu.getId(), NOTIFICATION_TYPE.NEW_REVIEW, request.getContent());
        this.notificationService.addNewNotificationForUser(findUser.getEmail(), spu.getId(), NOTIFICATION_TYPE.NEW_REVIEW, request.getContent());

        return savedComment.getId();
    }

    @Override
    public APISuccessResponseWithMetadata<?> getListOfCommentsBySpu(String spuId, Integer pageNumber, Integer pageSize) throws NotFoundException {
        Pageable page = PageRequest.of(Math.max(0, pageNumber - 1), pageSize);
        Page<Comment> pageOfComments = this.commentRepository.findParentComment(spuId, page);
        List<Comment> comments = pageOfComments.getContent();
        List<CommentDetailResponse> commentDetailResponses = comments.stream()
                .map(comment -> CommentDetailResponse.builder()
                        .id(comment.getId())
                        .content(comment.getContent())
                        .spuId(comment.getSpu().getId())
                        .userId(comment.getUser().getId())
                        .parentId(comment.getParentId())
                        .createdAt(comment.getCreatedAt())
                        .updatedAt(comment.getUpdatedAt())
                        .build()).toList();

        PaginationMetadata paginationMetadata = PaginationMetadata.builder()
                .currentPage(pageOfComments.getNumber() + 1)
                .pageSize(pageOfComments.getSize())
                .totalPages(pageOfComments.getTotalPages())
                .totalItems((int)pageOfComments.getTotalElements())
                .build();

        return APISuccessResponseWithMetadata.builder()
                .message("Success")
                .data(commentDetailResponses)
                .metadata(paginationMetadata)
                .build();
    }

    @Override
    public APISuccessResponseWithMetadata<?> getRepliesOfComment(String commentParentId, Integer pageNumber, Integer pageSize) throws NotFoundException {
        this.commentRepository.findById(commentParentId)
                .orElseThrow(() -> new NotFoundException(CommentErrorConstant.COMMENT_NOT_FOUND));

        Pageable page = PageRequest.of(Math.max(0, pageNumber - 1), pageSize);
        Page<Comment> pageOfComments = this.commentRepository.findAllByParentId(commentParentId, page);
        List<Comment> comments = pageOfComments.getContent();
        List<CommentDetailResponse> commentDetailResponses = comments.stream()
                .map(comment -> CommentDetailResponse.builder()
                        .id(comment.getId())
                        .content(comment.getContent())
                        .spuId(comment.getSpu().getId())
                        .userId(comment.getUser().getId())
                        .parentId(commentParentId)
                        .createdAt(comment.getCreatedAt())
                        .updatedAt(comment.getUpdatedAt())
                        .build()).toList();

        PaginationMetadata paginationMetadata = PaginationMetadata.builder()
                .currentPage(pageOfComments.getNumber() + 1)
                .pageSize(pageOfComments.getSize())
                .totalPages(pageOfComments.getTotalPages())
                .totalItems((int)pageOfComments.getTotalElements())
                .build();

        return APISuccessResponseWithMetadata.builder()
                .message("Success")
                .data(commentDetailResponses)
                .metadata(paginationMetadata)
                .build();
    }
}
