package com.pratikmane.wechat.utils;

import com.pratikmane.wechat.model.Chat;
import com.pratikmane.wechat.model.User;

public class ChatUtil {
	
	public static String chatName(Chat chat,User reqUser) {
		
		for(User user:chat.getUsers()) {
			if(user.getId()!=reqUser.getId()) {
				return user.getFirstName()+" "+user.getLastName();
				
			}
			
		}
		return null;
		
	}

}
