package com.pratikmane.wechat.dto;

import java.time.LocalDateTime;

import com.pratikmane.wechat.model.User;

import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StoryDto {

	private Integer id;
	private User user;
	private String image;
	private String captions;
	private LocalDateTime timestamp;
	
}
