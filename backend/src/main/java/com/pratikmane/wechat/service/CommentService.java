package com.pratikmane.wechat.service;

import com.pratikmane.wechat.exception.CommentException;
import com.pratikmane.wechat.exception.PostException;
import com.pratikmane.wechat.exception.UserException;
import com.pratikmane.wechat.model.Comments;

public interface CommentService {
	
	public Comments createComment(Comments comment,Integer postId,Integer userId) throws PostException, UserException;

	public Comments findCommentById(Integer commentId) throws CommentException;
	public Comments likeComment(Integer CommentId,Integer userId) 
			throws UserException, CommentException;
}
