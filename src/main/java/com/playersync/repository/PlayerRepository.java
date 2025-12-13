package com.playersync.repository;

import com.playersync.entity.Player;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    // Optimized query to fetch Player + Profile + Missions + Rewards + Tags in one go
    // Used JOIN FETCH to solve N+1.
    @Query("SELECT p FROM Player p " +
            "LEFT JOIN FETCH p.profile " +
            "LEFT JOIN FETCH p.missions m " +
            "LEFT JOIN FETCH m.rewards " +
            "LEFT JOIN FETCH m.tags " +
            "WHERE p.id = :id")
    Optional<Player> findByIdWithMissions(@Param("id") Long id);

    // Bonus 1: Pagination support
    @Query("SELECT m FROM Mission m WHERE m.player.id = :playerId")
    Page<com.playersync.entity.Mission> findMissionsByPlayerId(@Param("playerId") Long playerId, Pageable pageable);
}