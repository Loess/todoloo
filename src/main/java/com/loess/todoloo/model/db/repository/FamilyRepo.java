package com.loess.todoloo.model.db.repository;

import com.loess.todoloo.model.db.entity.Family;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FamilyRepo extends JpaRepository<Family, Long> {

}
