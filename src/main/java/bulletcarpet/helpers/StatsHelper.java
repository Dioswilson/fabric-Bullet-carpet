package bulletcarpet.helpers;

import bulletcarpet.utils.CustomStats;
import bulletcarpet.utils.ToolItems;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.logging.LogUtils;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.File;
import java.util.*;

public class StatsHelper {

    private static Map<UUID, StatHandler> cache;
    private static int cacheTime;

    private static final Logger LOGGER = LogUtils.getLogger();

    public static String TOTAL_USER_NAME = Formatting.BOLD + " Total";

    public static File[] getStatFiles(MinecraftServer server) {
        File statsDir = server.getSavePath(WorldSavePath.STATS).toFile();
        return statsDir.listFiles((dir, name) -> name.endsWith(".json"));
    }

    public static Map<UUID, StatHandler> getAllStatistics(MinecraftServer server) {
        if (cache != null && server.getTicks() - cacheTime < 100) {
            LOGGER.info("Using cached statistics data");
            return cache;
        }
        File[] statFiles = getStatFiles(server);

        HashMap<UUID, StatHandler> stats = new HashMap<>();

        if (statFiles == null) {
            LOGGER.warn("No stat files found in the stats directory.");
            return stats;
        }
        LOGGER.info("Retrieving statistics from {} files", statFiles.length);
        for (File statFile : statFiles) {
            String filename = statFile.getName();
            String uuidString = filename.substring(0, filename.lastIndexOf(".json"));
            try {
                UUID uuid = UUID.fromString(uuidString);

                ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);

                if (player != null) {
                    stats.put(uuid, player.getStatHandler());
                }
                else {
                    ServerStatHandler manager = new ServerStatHandler(server, statFile);
                    manager.updateStatSet();
                    stats.put(uuid, manager);
                }
            } catch (IllegalArgumentException ignored) {
            }
        }
        LOGGER.info("Finished retrieving statistics");

        cache = stats;
        cacheTime = server.getTicks();

        return stats;
    }

    @Nullable
    public static String getUsername(MinecraftServer server, UUID uuid) {
        if (uuid == null) {
            return null;
        }

        Optional<GameProfile> userProfile = server.getUserCache().getByUuid(uuid);
        if (userProfile.isPresent()) {
            return userProfile.get().getName();
        }

        MinecraftSessionService sessionService = server.getSessionService();
        GameProfile profile = null;
        int attempts = 5;
        while (profile == null && attempts > 0) {
            attempts--;
            try {
                profile = sessionService.fillProfileProperties(new GameProfile(uuid, null), false);
            } catch (Exception e) {
                LOGGER.info("Retrying profile retrieving for UUID: {} ", uuid.toString());
            }
        }
        if (profile == null) {
            LOGGER.warn("Failed to retrieve profile for UUID: {}", uuid);
            return null;
        }
        if (profile.isComplete()) {
            return profile.getName();
        }
        return null;
    }


    public static Optional<Stat<?>> resolveStat(String criterionName, int typeSeparationIndex) {
        Identifier typeId = Identifier.splitOn(criterionName.substring(0, typeSeparationIndex), '.');
        Identifier statId = Identifier.splitOn(criterionName.substring(typeSeparationIndex + 1), '.');

        // Get raw Optional<StatType<?>>
        Optional<? extends StatType<?>> optType = Registries.STAT_TYPE.getOrEmpty(typeId);

        return optType.flatMap(type -> resolveStatFromType(type, statId));
    }

    private static <T> Optional<Stat<?>> resolveStatFromType(StatType<?> typeRaw, Identifier statId) {
        StatType<T> type = (StatType<T>) typeRaw;
        return type.getRegistry().getOrEmpty(statId)
                .map(obj -> (Stat<?>) type.getOrCreateStat(obj));

    }

    public static void initializeScoreboard(MinecraftServer server, String objectiveName) {
        LOGGER.info("Initializing {} scoreboard ", objectiveName);
        Scoreboard scoreboard = server.getScoreboard();
        ScoreboardObjective scoreObjective = scoreboard.getObjective(objectiveName);
        String criterionName = scoreObjective.getCriterion().getName();

        int typeSeparationIndex = criterionName.indexOf(':');

        Optional<Stat<?>> stat = resolveStat(criterionName, typeSeparationIndex);

        if (stat.isPresent()) { //Else log error
            int totalScorePoints = 0;//Note: Try to use long?

            for (Map.Entry<UUID, StatHandler> statEntry : getAllStatistics(server).entrySet()) {
                int scorePoints = statEntry.getValue().getStat(stat.get());
                String username = getUsername(server, statEntry.getKey());
                if (username == null) {
                    continue;
                }
                ScoreboardPlayerScore playerScore = scoreboard.getPlayerScore(username, scoreObjective);
                playerScore.setScore(scorePoints);
                totalScorePoints += scorePoints;
            }
            ScoreboardPlayerScore totalPlayerScore = scoreboard.getPlayerScore(TOTAL_USER_NAME, scoreObjective);
            totalPlayerScore.setScore(totalScorePoints);

            LOGGER.info("Finished initializing {} scoreboard ", objectiveName);
        }
    }

    public static void initToolItemStats(MinecraftServer server) {
        Map<String, List<Item>> toolCategories = Map.of(
                "pickaxe", List.of(
                        Items.WOODEN_PICKAXE, Items.STONE_PICKAXE, Items.IRON_PICKAXE,
                        Items.GOLDEN_PICKAXE, Items.DIAMOND_PICKAXE, Items.NETHERITE_PICKAXE
                ),
                "hoe", List.of(
                        Items.WOODEN_HOE, Items.STONE_HOE, Items.IRON_HOE,
                        Items.GOLDEN_HOE, Items.DIAMOND_HOE, Items.NETHERITE_HOE
                ),
                "shovel", List.of(
                        Items.WOODEN_SHOVEL, Items.STONE_SHOVEL, Items.IRON_SHOVEL,
                        Items.GOLDEN_SHOVEL, Items.DIAMOND_SHOVEL, Items.NETHERITE_SHOVEL
                ),
                "axe", List.of(
                        Items.WOODEN_AXE, Items.STONE_AXE, Items.IRON_AXE,
                        Items.GOLDEN_AXE, Items.DIAMOND_AXE, Items.NETHERITE_AXE
                )
        );
        List<StatType<Item>> statTypes = List.of(
                Stats.USED, Stats.CRAFTED, Stats.BROKEN, Stats.PICKED_UP, Stats.DROPPED
        );

        LOGGER.info("Initializing tool item stats ");

        getAllStatistics(server).forEach((uuid, statHandler) -> {
            String username = getUsername(server, uuid);
            if (username != null) {
                ServerPlayerEntity player = new ServerPlayerEntity(server, Objects.requireNonNull(server.getWorld(World.OVERWORLD)), new GameProfile(uuid, username));

                //Sounds stupid ignoring statHandler, but don't know how else to fix it
                ServerStatHandler serverStatHandler = player.getStatHandler();

                for (StatType<Item> statType : statTypes) {
                    int allToolsValue = 0;

                    for (var entry : toolCategories.entrySet()) {
                        String toolType = entry.getKey();
                        List<Item> items = entry.getValue();

                        int value = sumStats(serverStatHandler, statType, items);

                        Item customItem = getItemFromToolType(toolType);
                        if (customItem != null) {
                            serverStatHandler.setStat(player, statType.getOrCreateStat(customItem), value);
                        }

                        allToolsValue += value;
                    }
                    serverStatHandler.setStat(player, statType.getOrCreateStat(ToolItems.ALL_TOOLS), allToolsValue);
                }
                serverStatHandler.save();
            }
        });
        LOGGER.info("Finished initializing tool item stats ");
    }

    public static void initializeHoursPlayed(MinecraftServer server) {
        LOGGER.info("Initializing hours played stats ");

        getAllStatistics(server).forEach((uuid, statHandler) -> {
            String username = getUsername(server, uuid);
            if (username != null) {
                ServerPlayerEntity player = new ServerPlayerEntity(server, Objects.requireNonNull(server.getWorld(World.OVERWORLD)), new GameProfile(uuid, username));
                //Sounds stupid ignoring statHandler, but don't know how else to fix it
                ServerStatHandler serverStatHandler = player.getStatHandler();

                int timePlayed = serverStatHandler.getStat(Stats.CUSTOM.getOrCreateStat(Stats.PLAY_TIME));
                serverStatHandler.setStat(player, Stats.CUSTOM.getOrCreateStat(CustomStats.HOURS_PLAYED), timePlayed / 72000);

                serverStatHandler.save();
            }
        });
        LOGGER.info("Finished initializing hours played stats ");
    }

    public static Item getItemFromToolType(String toolType) {//Note: Maybe this shold be on ToolItems.java?
        return switch (toolType) {
            case "pickaxe" -> ToolItems.PICKAXE;
            case "hoe" -> ToolItems.HOE;
            case "shovel" -> ToolItems.SHOVEL;
            case "axe" -> ToolItems.AXE;
            default -> null;
        };
    }

    public static int sumStats(StatHandler handler, StatType<Item> type, List<Item> items) {
        return items.stream()
                .mapToInt(item -> handler.getStat(type.getOrCreateStat(item)))
                .sum();
    }

}
