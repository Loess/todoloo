package com.loess.todoloo.model.db.repository;

import com.loess.todoloo.model.db.entity.Invite;
import com.loess.todoloo.model.db.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InviteRepo extends JpaRepository<Invite, Long> {

    @Query("select i from Invite i where i.user = :userId and i.familyId = :familyId")
    Optional<Invite> findMatch(@Param("userId") User userId, @Param("familyId") Long familyId);

    Optional<Invite> findFirstByUserAndFamilyId(User user, Long familyId);

}
