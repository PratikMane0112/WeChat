package com.pratikmane.wechat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pratikmane.wechat.exception.CommentException;
import com.pratikmane.wechat.exception.PostException;
import com.pratikmane.wechat.exception.UserException;
import com.pratikmane.wechat.model.Comments;
import com.pratikmane.wechat.model.User;
import com.pratikmane.wechat.service.CommentService;
import com.pratikmane.wechat.service.UserService;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
	
	@Autowired
	private CommentService commentService;
	
	@Autowired
	private UserService userService;
	
	@PostMapping("/{postId}")
	public ResponseEntity<Comments> createCommentHandler(@RequestBody Comments comment, @PathVariable("postId") Integer postId,@RequestHeader("Authorization")String token) throws PostException, UserException{
		User user = userService.findUserProfileByJwt(token);
		
		Comments createdComment = commentService.createComment(comment, postId, user.getId());
		return new ResponseEntity<Comments>(createdComment,HttpStatus.CREATED);
		
	}
	
	
	@PutMapping("/like/{commentId}")
	public ResponseEntity<Comments> likeCommentHandler(@PathVariable Integer commentId, @RequestHeader("Authorization")String token) throws UserException, CommentException{
		System.out.println("----------- like comment id ---------- ");
		User user = userService.findUserProfileByJwt(token);
		Comments likedComment=commentService.likeComment(commentId, user.getId());
		System.out.println("liked comment - : "+likedComment);
		return new ResponseEntity<Comments>(likedComment,HttpStatus.OK);
	}
	
	


}
//
