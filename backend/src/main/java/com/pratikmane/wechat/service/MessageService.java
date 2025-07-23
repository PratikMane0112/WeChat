package com.pratikmane.wechat.service;

import java.util.List;

import com.pratikmane.wechat.exception.ChatException;
import com.pratikmane.wechat.exception.MessageException;
import com.pratikmane.wechat.exception.UserException;
import com.pratikmane.wechat.model.Message;
import com.pratikmane.wechat.request.SendMessageRequest;

public interface MessageService  {
	
	public Message sendMessage(SendMessageRequest req) throws UserException, ChatException;
	
	public List<Message> getChatsMessages(Integer chatId) throws ChatException;
	
	public Message findMessageById(Integer messageId) throws MessageException;
	
	public String deleteMessage(Integer messageId) throws MessageException;

}
