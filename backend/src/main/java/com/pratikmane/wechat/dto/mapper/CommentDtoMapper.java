package com.pratikmane.wechat.dto.mapper;

import java.util.ArrayList;
import java.util.List;

import com.pratikmane.wechat.dto.CommentDto;
import com.pratikmane.wechat.dto.UserDto;
import com.pratikmane.wechat.model.Comments;
import com.pratikmane.wechat.model.User;

public class CommentDtoMapper {
	
	
	public static CommentDto toCommentDTO(Comments comment) {
		UserDto userDto=UserDtoMapper.userDTO(comment.getUser());
		List<User> likedUsers=new ArrayList<>(comment.getLiked());
		List<UserDto> userDtos = UserDtoMapper.userDTOS(likedUsers);
		
		CommentDto commentDto=new CommentDto();
		commentDto.setContent(comment.getContent());
		commentDto.setCreatedAt(comment.getCreatedAt());
		commentDto.setId(comment.getId());
		commentDto.setLiked(userDtos);
		commentDto.setUser(userDto);
		
		return commentDto;
	}
	
	public static List<CommentDto> toCommentDtos(List<Comments> comments){
		List<CommentDto> commentDtos=new ArrayList<>();
		
		for(Comments comment: comments) {
			
			CommentDto commentDto=toCommentDTO(comment);
			commentDtos.add(commentDto);
			
		}
		
		return commentDtos;
	}

}
