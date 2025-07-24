package com.pratikmane.wechat.service;

import java.util.List;

import com.pratikmane.wechat.exception.StoryException;
import com.pratikmane.wechat.exception.UserException;
import com.pratikmane.wechat.model.Story;

public interface StoryService {

	public Story createStory(Story story,Integer userId) throws UserException;
	
	public List<Story> findStoryByUserId(Integer userId) throws UserException, StoryException;
	
	
}
