package com.pratikmane.wechat.dto.mapper;

import java.util.ArrayList;
import java.util.List;

import com.pratikmane.wechat.dto.ReelsDto;
import com.pratikmane.wechat.dto.UserDto;
import com.pratikmane.wechat.model.Reels;

public class ReelsDtoMapper {
	
	
	public static ReelsDto toReelsDto(Reels reel) {
		ReelsDto reelsDto=new ReelsDto();
		
		UserDto user=UserDtoMapper.userDTO(reel.getUser());
		
		reelsDto.setTitle(reel.getTitle());
		reelsDto.setUser(user);
		reelsDto.setVideo(reel.getVideo());
		reelsDto.setId(reel.getId());
		
		return reelsDto;
		
	}
	
	public static List<ReelsDto> toReelsDtos(List<Reels> reels){
		
		List<ReelsDto> reelsDtos=new ArrayList<>();
		
		for(Reels reel : reels ) {
			 ReelsDto reelsDto=toReelsDto(reel);
			 reelsDtos.add(reelsDto);
		}
		
		return reelsDtos;
		
	}

}
