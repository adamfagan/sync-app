package com.playersync.service;

import com.playersync.dto.MissionDTO;
import com.playersync.dto.PlayerSyncDTO;
import com.playersync.dto.MissionRewardDTO;
import com.playersync.entity.Mission;
import com.playersync.entity.Player;
import com.playersync.repository.PlayerRepository;
import com.playersync.redis.RedisCacheService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlayerSyncService {

    private final PlayerRepository playerRepository;
    private final RedisCacheService redisCacheService;

    public PlayerSyncService(PlayerRepository playerRepository, RedisCacheService redisCacheService) {
        this.playerRepository = playerRepository;
        this.redisCacheService = redisCacheService;
    }

    @Transactional(readOnly = true)
    public PlayerSyncDTO syncPlayer(Long playerId, int page, int size) {
        // 1. Load Data
        Player player = playerRepository.findByIdWithMissions(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found"));

        // Bonus 1: Pagination Logic
        List<Mission> missionsToMap;
        if (size > 0) {
            missionsToMap = playerRepository.findMissionsByPlayerId(playerId, PageRequest.of(page, size)).getContent();
        } else {
            // Convert Set to List
            missionsToMap = new ArrayList<>(player.getMissions());
        }

        // 2. Map to DTO
        PlayerSyncDTO dto = new PlayerSyncDTO();
        dto.setId(player.getId());
        dto.setUsername(player.getUsername());
        dto.setStatus(player.getStatus());

        if (player.getProfile() != null) {
            dto.setCountry(player.getProfile().getCountry());
        } else {
            dto.setCountry("Unknown");
        }

        List<MissionDTO> missionDTOs = missionsToMap.stream().map(m -> {
            MissionDTO md = new MissionDTO();
            md.setId(m.getId());
            md.setName(m.getName());
            md.setCompleted(m.getCompleted() != null ? m.getCompleted() : false);
            md.setProgress(m.getProgress() != null ? m.getProgress() : 0);

            // Map Rewards
            if (m.getRewards() != null) {
                md.setRewards(
                        m.getRewards().stream()
                                .map(r -> {
                                    MissionRewardDTO mrd = new MissionRewardDTO();
                                    mrd.setId(r.getId());
                                    mrd.setMissionId(r.getMission().getId());
                                    mrd.setRewardType(r.getRewardType());
                                    mrd.setAmount(r.getAmount());
                                    return mrd;
                                })
                                .collect(Collectors.toList())
                );
            } else {
                md.setRewards(new ArrayList<>());
            }

            // Map Tags
            if (m.getTags() != null) {
                md.setTags(m.getTags().stream()
                        .map(t -> t.getName())
                        .collect(Collectors.toList()));
            } else {
                md.setTags(new ArrayList<>());
            }

            return md;
        }).collect(Collectors.toList());

        dto.setMissions(missionDTOs);

        // 3. Sync to Redis
        // Convert LocalDateTime to Instant
        Instant lastUpdate = Instant.now();
        if (player.getUpdatedAt() != null) {
            lastUpdate = player.getUpdatedAt().toInstant(ZoneOffset.UTC);
        }

        redisCacheService.cachePlayerMissions(playerId, dto, lastUpdate);

        return dto;
    }

    @Transactional
    public void updatePlayer(Long playerId, String newStatus) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found"));

        // Update SQL
        player.setStatus(newStatus);
        player.setUpdatedAt(java.time.LocalDateTime.now());
        playerRepository.save(player);

        // Bonus 2: Invalidate Cache
        redisCacheService.invalidateCache(playerId);
    }
}