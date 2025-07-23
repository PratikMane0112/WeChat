package com.pratikmane.wechat.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pratikmane.wechat.exception.UserException;
import com.pratikmane.wechat.model.Reels;
import com.pratikmane.wechat.model.User;
import com.pratikmane.wechat.repository.ReelsRepository;

@Service
public class ReelsServiceImplementation implements ReelsService {
	
	@Autowired
	private ReelsRepository reelsRepositoy;
	
	@Autowired
	private UserService userSerive;

	@Override
	public Reels createReel(Reels reel,User user) {
		Reels createdReel = new Reels();
		
		createdReel.setTitle(reel.getTitle());
		createdReel.setUser(user);
		createdReel.setVideo(reel.getVideo());
		
		return reelsRepositoy.save(createdReel);
	}

	@Override
	public List<Reels> findAllReels() {
		
		return reelsRepositoy.findAll();
	}

	@Override
	public List<Reels> findUsersReel(Integer userId) throws UserException {
		userSerive.findUserById(userId);
		return reelsRepositoy.findByUserId(userId);
	}

}
