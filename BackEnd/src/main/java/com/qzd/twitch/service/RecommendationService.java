package com.qzd.twitch.service;

import com.qzd.twitch.dao.FavoriteDao;
import com.qzd.twitch.entity.db.Item;
import com.qzd.twitch.entity.db.ItemType;
import com.qzd.twitch.entity.response.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RecommendationService {
    private static final int DEFAULT_GAME_LIMIT = 3;
    private static final int DEFAULT_PER_GAME_RECOMMENDATION_LIMIT = 10;
    private static final int DEFAULT_TOTAL_RECOMMENDATION_LIMIT = 20;
    @Autowired
    private GameService gameService;

    @Autowired
    private FavoriteDao favoriteDao;

    // Return a list of Item objects for the given type. Types are one of [Stream, Video, Clip]. Add items are related to the top games provided in the argument
    private List<Item> recommendByTopGames(ItemType type, List<Game> topGames) throws RecommendationException {
        List<Item> recommendedItems = new ArrayList<>();
        for (Game game : topGames) {
            List<Item> items;
            try {
                items = gameService.searchByType(game.getId(), type, DEFAULT_PER_GAME_RECOMMENDATION_LIMIT);
            } catch (TwitchException e) {
                throw new RecommendationException("Failed to get recommendation result");
            }
            for (Item item : items) {
                if (recommendedItems.size() == DEFAULT_TOTAL_RECOMMENDATION_LIMIT) {
                    return recommendedItems;
                }
                recommendedItems.add(item);
            }
        }
        return recommendedItems;
    }

    // Return a list of Item objects for the given type. Types are one of [Stream, Video, Clip]. All items are related to the items previously favorited by the user. E.g., if a user favorited some videos about game "Just Chatting", then it will return some other videos about the same game.
    private List<Item> recommendByFavoriteHistory(
            Set<String> favoritedItemIds, List<String> favoritedGameIds, ItemType type) throws RecommendationException {
        // Count the favorite game IDs from the database for the given user. E.g. if the favorited game ID list is ["1234", "2345", "2345", "3456"], the returned Map is {"1234": 1, "2345": 2, "3456": 1}
        Map<String, Long> favoriteGameIdByCount = new HashMap<>();
        for(String gameId : favoritedGameIds) {
            favoriteGameIdByCount.put(gameId, favoriteGameIdByCount.getOrDefault(gameId, 0L) + 1);
        }
        // Sort the game Id by count. E.g. if the input is {"1234": 1, "2345": 2, "3456": 1}, the returned Map is {"2345": 2, "1234": 1, "3456": 1}
        List<Map.Entry<String, Long>> sortedFavoriteGameIdListByCount = new ArrayList<>(
                favoriteGameIdByCount.entrySet());
        sortedFavoriteGameIdListByCount.sort((Map.Entry<String, Long> e1, Map.Entry<String, Long> e2) -> Long
                .compare(e2.getValue(), e1.getValue()));
        // See also: https://stackoverflow.com/questions/109383/sort-a-mapkey-value-by-values


        if (sortedFavoriteGameIdListByCount.size() > DEFAULT_GAME_LIMIT) {
            sortedFavoriteGameIdListByCount = sortedFavoriteGameIdListByCount.subList(0, DEFAULT_GAME_LIMIT);
        }


        List<Item> recommendedItems = new ArrayList<>();


        // Search Twitch based on the favorite game IDs returned in the last step.

        for (Map.Entry<String, Long> favoriteGame : sortedFavoriteGameIdListByCount) {
            List<Item> items;
            try {
                items = gameService.searchByType(favoriteGame.getKey(), type, DEFAULT_PER_GAME_RECOMMENDATION_LIMIT);
            } catch (TwitchException e) {
                throw new RecommendationException("Failed to get recommendation result");
            }


            for (Item item : items) {
                if (recommendedItems.size() == DEFAULT_TOTAL_RECOMMENDATION_LIMIT) {
                    return recommendedItems;
                }
                if (!favoritedItemIds.contains(item.getId())) {
                    recommendedItems.add(item);
                }
            }
        }
        return recommendedItems;
    }

    // Return a map of Item objects as the recommendation result. Keys of the may are [Stream, Video, Clip]. Each key is corresponding to a list of Items objects, each item object is a recommended item based on the previous favorite records by the user.
    public Map<String, List<Item>> recommendItemsByUser(String userId) throws RecommendationException {
        Map<String, List<Item>> recommendedItemMap = new HashMap<>();
        Set<String> favoriteItemIds;
        Map<String, List<String>> favoriteGameIds;

        favoriteItemIds = favoriteDao.getFavoriteItemIds(userId);
        favoriteGameIds = favoriteDao.getFavoriteGameIds(favoriteItemIds);

        for (Map.Entry<String, List<String>> entry : favoriteGameIds.entrySet()) {
            if (entry.getValue().size() == 0) {
                List<Game> topGames;
                try {
                    topGames = gameService.topGames(DEFAULT_GAME_LIMIT);
                } catch (TwitchException e) {
                    throw new RecommendationException("Failed to get game data for recommendation");
                }
                recommendedItemMap.put(entry.getKey(), recommendByTopGames(ItemType.valueOf(entry.getKey()), topGames));
            } else {
                recommendedItemMap.put(entry.getKey(), recommendByFavoriteHistory(favoriteItemIds, entry.getValue(), ItemType.valueOf(entry.getKey())));
            }
        }
        return recommendedItemMap;
    }

    // Return a map of Item objects as the recommendation result. Keys of the may are [Stream, Video, Clip]. Each key is corresponding to a list of Items objects, each item object is a recommended item based on the top games currently on Twitch.
    public Map<String, List<Item>> recommendItemsByDefault() throws RecommendationException {
        Map<String, List<Item>> recommendedItemMap = new HashMap<>();
        List<Game> topGames;
        try {
            topGames = gameService.topGames(DEFAULT_GAME_LIMIT);
        } catch (TwitchException e) {
            throw new RecommendationException("Failed to get game data for recommendation");
        }


        for (ItemType type : ItemType.values()) {
            recommendedItemMap.put(type.toString(), recommendByTopGames(type, topGames));
        }
        return recommendedItemMap;
    }


}
