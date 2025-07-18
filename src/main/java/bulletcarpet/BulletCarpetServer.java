package bulletcarpet;

import bulletcarpet.commands.removeStats.RemoveStatsCommand;
import bulletcarpet.commands.scoreboardStats.ScoreboardStatsCommand;
import bulletcarpet.helpers.CustomStats;
import bulletcarpet.helpers.StatsHelper;
import bulletcarpet.utils.ToolItems;
import carpet.CarpetExtension;
import carpet.CarpetServer;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.WorldSavePath;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
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
        ToolItems.initialize();
        CustomStats.initialize();
        BulletCarpetServer.loadExtension();
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
            File falgFile = new File(rootDir, "bulletcarpetStatsInit");
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
