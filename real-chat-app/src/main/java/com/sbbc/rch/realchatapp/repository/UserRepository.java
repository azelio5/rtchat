package com.sbbc.rch.realchatapp.repository;

import com.sbbc.rch.realchatapp.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.isOnline = :isOnline WHERE u.username =:username")
    void updateUserOnlineStatus(@Param("username") String username,
                                @Param("isOnline") boolean isOnline);

    Optional<User> findByUsername(String username);
}
