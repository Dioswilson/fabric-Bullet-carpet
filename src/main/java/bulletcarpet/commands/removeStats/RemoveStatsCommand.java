package bulletcarpet.commands.removeStats;

import bulletcarpet.BulletCarpetSettings;
import bulletcarpet.helpers.StatsHelper;
import bulletcarpet.utils.ModUtils;
import bulletcarpet.utils.ToolItems;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.logging.LogUtils;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.stat.StatHandler;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.UserCache;
import net.minecraft.util.WorldSavePath;
import org.slf4j.Logger;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class RemoveStatsCommand {
    private static final String NAME = "removeStats";

    private static final Logger LOGGER = LogUtils.getLogger();

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {


        LiteralArgumentBuilder<ServerCommandSource> commandBuilder = literal(NAME).requires(source -> BulletCarpetSettings.removeStatsCommand && source.hasPermissionLevel(2));

        commandBuilder.then(literal("lowPickUses").
                executes(c -> {
                    MinecraftServer server = c.getSource().getServer();
                    PlayerManager playerManager = server.getPlayerManager();

                    playerManager.broadcast(Text.literal(Formatting.GRAY + "Starting to remove stats files"), false);
                    removeStatsFromPlayersWithCriteria(c.getSource(), server, RemoveStatsCriteria.LOW_PICK_USES, 1);
                    playerManager.broadcast(Text.literal(Formatting.GRAY + "Finished removing stat files"), false);
                    return 0;
                }).
                then(argument("minUses", IntegerArgumentType.integer(0, 100)).
                        executes(c -> {
                            MinecraftServer server = c.getSource().getServer();
                            PlayerManager playerManager = server.getPlayerManager();
                            int minUsesAllowed = IntegerArgumentType.getInteger(c, "minUses");

                            playerManager.broadcast(Text.literal(Formatting.GRAY + "Starting to remove stats files"), false);
                            removeStatsFromPlayersWithCriteria(c.getSource(), server, RemoveStatsCriteria.LOW_PICK_USES, minUsesAllowed);
                            playerManager.broadcast(Text.literal(Formatting.GRAY + "Finished removing stat files"), false);
                            return 0;
                        })));

        commandBuilder.then(literal("user").then(
                argument("users", StringArgumentType.word()).
                        executes(c -> {
                            removeStatsFromPlayer(c.getSource(), c.getSource().getServer(), StringArgumentType.getString(c, "users"));

                            return 0;
                        })));

        commandBuilder.then(literal("listPlayers").
                executes(c -> {
                    final ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();

                    singleThreadExecutor.submit(() -> {
                        listAllPlayersWithStats(c.getSource(), c.getSource().getServer());
                    });

                    singleThreadExecutor.shutdown();
                    return 0;
                }));

        dispatcher.register(commandBuilder);

    }

    private static void listAllPlayersWithStats(ServerCommandSource source, MinecraftServer server) {
        StatsHelper.getAllStatistics(server).forEach(((uuid, statHandler) -> {
            String playerName = StatsHelper.getUsername(server, uuid);
            if (playerName != null) {
                source.sendFeedback(Text.literal(Formatting.DARK_AQUA + playerName), false);
            }
        }));

        source.sendFeedback(Text.literal(Formatting.GRAY + "Finished listing players"), false);

    }

    private static void removeStatsFromPlayer(ServerCommandSource source, MinecraftServer server, String username) {
        PlayerManager playerManager = server.getPlayerManager();
        UserCache userCache = playerManager.getServer().getUserCache();
        GameProfile profile = userCache.findByName(username).get();
        UUID uuid = profile.getId();
        if (uuid != null) {
            File statsFile = server.getSavePath(WorldSavePath.STATS).resolve(uuid.toString() + ".json").toFile();
            if (statsFile.delete()) {
                source.sendFeedback(Text.literal(Formatting.GREEN + "Removed stats for player: " + username), false);
                LOGGER.info("Removed stats for player: {}", username);
            }
            else {
                source.sendFeedback(Text.literal(Formatting.RED + "Failed to remove stats file for player: " + username), false);
            }
        }

    }

    private static void removeStatsFromPlayersWithCriteria(ServerCommandSource source, MinecraftServer server, RemoveStatsCriteria removeCriteria, int minUsesAllowed) {
        Map<UUID, StatHandler> stats = StatsHelper.getAllStatistics(server);
        if (removeCriteria != RemoveStatsCriteria.LOW_PICK_USES) {
            System.out.println("RemoveStatsCommand: Unsupported criteria: " + removeCriteria);
            return;
        }

        stats.forEach((uuid, statHandler) -> {
            int statValue = 0;

            File falgFile = new File(ModUtils.getStatInitFileName());
            if (falgFile.exists()) {
                statValue = statHandler.getStat(Stats.USED.getOrCreateStat(ToolItems.PICKAXE));//only pickaxe uses yet
            }
            else {
                statValue = StatsHelper.sumStats(statHandler, Stats.USED, List.of(
                        Items.WOODEN_PICKAXE, Items.STONE_PICKAXE, Items.IRON_PICKAXE,
                        Items.GOLDEN_PICKAXE, Items.DIAMOND_PICKAXE, Items.NETHERITE_PICKAXE
                ));
            }

            if (statValue < minUsesAllowed) {
                if (uuid != null) {
                    File statsFile = server.getSavePath(WorldSavePath.STATS).resolve(uuid.toString() + ".json").toFile();
                    if (statsFile.exists()) {
                        if (statsFile.delete()) {
                            source.sendFeedback(Text.literal(Formatting.GREEN + "Removed stats for player: " + uuid + " with " + statValue + " uses"), false);
                            LOGGER.info("Removed stats for player: {}", uuid);
                        }
                        else {
                            source.sendFeedback(Text.literal(Formatting.RED + "Failed to remove stats file for player: " + uuid), false);
                        }
                    }
                    else {
                        source.sendFeedback(Text.literal(Formatting.YELLOW + "No stats file found for player: " + uuid), false);
                    }
                }

            }

        });
    }

    private enum RemoveStatsCriteria {
        LOW_PICK_USES,
    }
}
