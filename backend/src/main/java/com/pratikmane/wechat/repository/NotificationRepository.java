package com.pratikmane.wechat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pratikmane.wechat.model.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId ORDER BY n.timestamp DESC")
    List<Notification> findByUserIdOrderByTimestampDesc(@Param("userId") Integer userId);
    
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.is_seen = false ORDER BY n.timestamp DESC")
    List<Notification> findUnreadByUserId(@Param("userId") Integer userId);
}
