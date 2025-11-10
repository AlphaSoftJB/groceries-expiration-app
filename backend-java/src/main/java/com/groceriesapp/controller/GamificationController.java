package com.groceriesapp.controller;

import com.groceriesapp.service.GamificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class GamificationController {
    
    private final GamificationService gamificationService;
    
    @QueryMapping
    public Map<String, Object> userStats(@Argument Long userId) {
        return gamificationService.getUserStats(userId);
    }
    
    @QueryMapping
    public List<Map<String, Object>> leaderboard(@Argument Integer limit) {
        return gamificationService.getLeaderboard(limit);
    }
    
    @MutationMapping
    public Map<String, Object> awardExperience(@Argument Long userId, @Argument Integer xp, @Argument String reason) {
        return gamificationService.awardExperience(userId, xp, reason);
    }
}
