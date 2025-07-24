package com.pratikmane.wechat.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pratikmane.wechat.dto.ChatDto;
import com.pratikmane.wechat.dto.mapper.ChatDtoMapper;
import com.pratikmane.wechat.exception.ChatException;
import com.pratikmane.wechat.exception.UserException;
import com.pratikmane.wechat.model.Chat;
import com.pratikmane.wechat.model.User;
import com.pratikmane.wechat.request.GroupChatRequest;
import com.pratikmane.wechat.request.RenameGroupRequest;
import com.pratikmane.wechat.request.SingleChatRequest;
import com.pratikmane.wechat.service.ChatService;
import com.pratikmane.wechat.service.UserService;

@RestController
@RequestMapping("/api/chats")
public class ChatController {
	
	@Autowired
	private ChatService chatService;
	
	@Autowired
	private UserService userService;
	
	@PostMapping("/single")
	public ResponseEntity<ChatDto> creatChatHandler(
			@RequestBody SingleChatRequest singleChatRequest, 
			@RequestHeader("Authorization")  String jwt) throws UserException{
		
		System.out.println("single chat --------");
		User reqUser=userService.findUserProfileByJwt(jwt);
		
		Chat chat=chatService.createChat(reqUser.getId(),singleChatRequest.getUserId(),false);
		ChatDto chatDto=ChatDtoMapper.toChatDto(chat,reqUser);
		
		return new ResponseEntity<ChatDto>(chatDto,HttpStatus.OK);
	}
	
	
	
	@GetMapping("/{chatId}")
	public ResponseEntity<ChatDto> findChatByIdHandler(
			@PathVariable Integer chatId,
			@RequestHeader("Authorization")  String jwt
			) throws ChatException, UserException{
		
		Chat chat =chatService.findChatById(chatId);
		User reqUser=userService.findUserProfileByJwt(jwt);
		ChatDto chatDto=ChatDtoMapper.toChatDto(chat,reqUser);
		
		return new ResponseEntity<ChatDto>(chatDto,HttpStatus.OK);
		
	}
	
	@GetMapping("/user")
	public ResponseEntity<List<ChatDto>> findAllChatByUserIdHandler(
			@RequestHeader("Authorization")String jwt) throws UserException{
		
		User user=userService.findUserProfileByJwt(jwt);
		
		List<Chat> chats=chatService.findAllChatByUserId(user.getId());
		
		List<ChatDto> chatDtos=ChatDtoMapper.toChatDtos(chats,user);
		
		return new ResponseEntity<>(chatDtos,HttpStatus.ACCEPTED);
	}
	

	
	
	

	
}
