package com.pratikmane.wechat.request;

import lombok.Data;

@Data
public class SendMessageRequest {
	
	private Integer chatId;
	private Integer userId;
	private String content;
	private String image;
	


}
