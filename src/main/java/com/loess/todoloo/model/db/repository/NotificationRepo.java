package com.loess.todoloo.model.db.repository;

import com.loess.todoloo.model.db.entity.Notification;
import com.loess.todoloo.model.db.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepo extends JpaRepository<Notification, Long> {

    List<Notification> findAllByUserOrderByCreationTimeDesc(User user); //latest first

    @Query("select n from Notification n where n.user = :user and deliveredTime = NULL ORDER BY creationTime DESC")
    List<Notification> findUnreadByUser(User user);


}
