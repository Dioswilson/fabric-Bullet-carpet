package bulletcarpet.helpers;

import bulletcarpet.BulletCarpetSettings;
import carpet.patches.EntityPlayerMPFake;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static carpet.script.api.Auxiliary.GSON;

public class FakePlayerReloadHelper {

    private static List<PlayerData> playersData = Collections.synchronizedList(new ArrayList<>());

    private static final String CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve(BulletCarpetSettings.NAMESPACE).
            resolve("fake_players.json").toString();//Todo: wrong path for singleplayer

    public static void registerFakePlayerInfo(ServerPlayerEntity player) {
        final String name = player.getName().getString();
        final Vec3d position = player.getPos();
        final float yaw = player.getYaw();
        final float pitch = player.getPitch();
        final String dimension = player.getWorld().getRegistryKey().getValue().getPath();
        final String gamemode = player.interactionManager.getGameMode().getName();
        final boolean isFlying = player.getAbilities().flying;

        playersData.add(new PlayerData(name, position, yaw, pitch, dimension, gamemode, isFlying));
    }

    public static void saveFakePlayersInfo() {
        if (!playersData.isEmpty()) {
            File file = new File(CONFIG_PATH);

            try {
                ensureConfigFileExists(file.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            try (FileWriter writer = new FileWriter(CONFIG_PATH)) {
                GSON.toJson(playersData, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
            clearFakePlayers();
        }
    }

    public static void loadFakePlayers(MinecraftServer server) {
        List<PlayerData> players = getPlayerDataFromFile(CONFIG_PATH);

        for (PlayerData playerData : players) {
            Identifier dimensionId = new Identifier("minecraft", playerData.dimension);

            RegistryKey<World> dimensionKey = RegistryKey.of(RegistryKeys.WORLD, dimensionId);
            GameMode gameMode = GameMode.byName(playerData.gamemode);

            EntityPlayerMPFake.createFake(playerData.name, server, playerData.position.x, playerData.position.y, playerData.position.z,
                    playerData.yaw, playerData.pitch, dimensionKey, gameMode, playerData.isFlying);
        }

    }

    //Todo: Utils class?
    private static void ensureConfigFileExists(Path configPath) throws IOException {
        Files.createDirectories(configPath.getParent());
        if (!Files.exists(configPath)) {
            Files.createFile(configPath);
        }
    }

    private static List<PlayerData> getPlayerDataFromFile(String filePath) {
        try (Reader reader = Files.newBufferedReader(Path.of(filePath))) {
            Type listType = new TypeToken<List<PlayerData>>() {
            }.getType();

            return GSON.fromJson(reader, listType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return List.of();
    }

    private static void clearFakePlayers() {
        playersData.clear();
    }


    private record PlayerData(String name, Vec3d position, float yaw, float pitch, String dimension, String gamemode,
                              boolean isFlying) {
        //TODO: Save current action

    }


}
