package com.tpt.capstone_ecommerce.ecommerce.service;

import com.tpt.capstone_ecommerce.ecommerce.dto.request.CreateCommentRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.UpdateCommentRequest;
import com.tpt.capstone_ecommerce.ecommerce.dto.response.APISuccessResponseWithMetadata;
import com.tpt.capstone_ecommerce.ecommerce.exception.NotFoundException;
import org.apache.coyote.BadRequestException;

public interface CommentService {
    String createComment(String spuId, CreateCommentRequest request) throws NotFoundException, BadRequestException;
    String updateComment(String commentId, UpdateCommentRequest request) throws NotFoundException, BadRequestException;
    String deleteComment(String commentId) throws NotFoundException, BadRequestException;
    String replyComment(String commentParentId, CreateCommentRequest request) throws NotFoundException, BadRequestException;
    APISuccessResponseWithMetadata<?> getListOfCommentsBySpu(String spuId, Integer pageNumber, Integer pageSize) throws NotFoundException;
    APISuccessResponseWithMetadata<?> getRepliesOfComment(String commentParentId, Integer pageNumber, Integer pageSize) throws NotFoundException;
}
