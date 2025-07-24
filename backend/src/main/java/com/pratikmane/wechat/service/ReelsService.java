package com.pratikmane.wechat.service;

import java.util.List;

import com.pratikmane.wechat.exception.UserException;
import com.pratikmane.wechat.model.Reels;
import com.pratikmane.wechat.model.User;

public interface ReelsService {
	
	public Reels createReel(Reels reel,User user);
	public List<Reels> findAllReels();
	public List<Reels> findUsersReel(Integer userId) throws UserException;

}
