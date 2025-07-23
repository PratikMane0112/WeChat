package com.pratikmane.wechat.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pratikmane.wechat.model.Comments;


public interface CommentRepository extends JpaRepository<Comments, Integer> {

}
