package bulletcarpet;

import bulletcarpet.commands.cameramode.CameraSpectatorCommand;
import bulletcarpet.commands.cameramode.CameraSurvivalCommand;
import bulletcarpet.commands.removeStats.RemoveStatsCommand;
import bulletcarpet.commands.scoreboardStats.ScoreboardStatsCommand;
import bulletcarpet.helpers.FakePlayerReloadHelper;
import bulletcarpet.helpers.StatsHelper;
import bulletcarpet.utils.CustomStats;
import bulletcarpet.utils.ToolItems;
import carpet.CarpetExtension;
import carpet.CarpetServer;
import carpet.script.external.Carpet;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.WorldSavePath;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class BulletCarpetServer implements CarpetExtension, ModInitializer {
    @Override
    public String version() {
        return "BulletCarpet";
    }

    public static void loadExtension() {
        CarpetServer.manageExtension(new BulletCarpetServer());
    }

    @Override
    public void onInitialize() {
        BulletCarpetServer.loadExtension();
        ToolItems.initialize();
        CustomStats.initialize();
    }

    @Override
    public void onServerLoaded(MinecraftServer server) {
        CarpetExtension.super.onServerLoaded(server);

    }

    @Override
    public void onServerLoadedWorlds(MinecraftServer server) {
        CarpetExtension.super.onServerLoadedWorlds(server);

        if (BulletCarpetSettings.scoreboardStats) {
            File rootDir = server.getSavePath(WorldSavePath.ROOT).toFile();
            File falgFile = new File(rootDir, "bulletcarpetStatsInit"); //TODO: Maybe move into config directory
            if (!falgFile.exists()) {
                StatsHelper.initToolItemStats(server);
                StatsHelper.initializeHoursPlayed(server);

                try {
                    Files.createFile(falgFile.toPath());
                } catch (IOException e) {
                    System.err.println("Failed to create file: " + falgFile.getAbsolutePath());
                }
            }
        }
        if (BulletCarpetSettings.reloadFakePlayers) {
            FakePlayerReloadHelper.loadFakePlayers(server);
        }
    }

    @Override
    public void onServerClosed(MinecraftServer server) {
        CarpetExtension.super.onServerClosed(server);

        if (BulletCarpetSettings.reloadFakePlayers) {
            List<ServerPlayerEntity> playerList = server.getPlayerManager().getPlayerList();

            for (ServerPlayerEntity player : playerList) {
                String playerType = Carpet.isModdedPlayer(player);
                if (playerType != null) {
                    FakePlayerReloadHelper.registerFakePlayerInfo(player);
                }
            }
            FakePlayerReloadHelper.saveFakePlayersInfo();

        }
    }

    @Override
    public void onGameStarted() {
        // let's /carpet handle our few simple settings
        CarpetServer.settingsManager.parseSettingsClass(BulletCarpetSettings.class);
        //Más cosas
    }

    @Override
    public void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
        // here goes extra stuff
        ScoreboardStatsCommand.register(dispatcher);
        RemoveStatsCommand.register(dispatcher);
        CameraSpectatorCommand.register(dispatcher);
        CameraSurvivalCommand.register(dispatcher);
    }

    @Override
    public Map<String, String> canHasTranslations(String lang) {
        InputStream langFile = BulletCarpetServer.class.getClassLoader().getResourceAsStream("assets/bulletcarpet/lang/%s.json".formatted(lang));
        if (langFile == null) {
            return Collections.emptyMap();
        }
        String jsonData;
        try {
            jsonData = IOUtils.toString(langFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return Collections.emptyMap();
        }
        return new GsonBuilder().create().fromJson(jsonData, new TypeToken<Map<String, String>>() {
        }.getType());
    }
}
