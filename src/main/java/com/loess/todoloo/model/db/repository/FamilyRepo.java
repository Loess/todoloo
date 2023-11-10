package com.loess.todoloo.model.db.repository;

import com.loess.todoloo.model.db.entity.Family;
import com.loess.todoloo.model.db.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FamilyRepo extends JpaRepository<Family, Long> {

//    Optional<Family> findByEmail(String email);
}
