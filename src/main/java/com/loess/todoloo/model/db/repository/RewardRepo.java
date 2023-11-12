package com.loess.todoloo.model.db.repository;

import com.loess.todoloo.model.db.entity.Reward;
import com.loess.todoloo.model.db.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RewardRepo extends JpaRepository<Reward, Long> {

    List<Reward> findByAssigneeAndFinished(User user, boolean finished);

    List<Reward> findByAssignee (User user);

}
