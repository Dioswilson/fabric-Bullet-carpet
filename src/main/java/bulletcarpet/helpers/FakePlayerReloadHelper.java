package bulletcarpet.helpers;

import bulletcarpet.BulletCarpetSettings;
import bulletcarpet.mixins.EntityPlayerActionPackAccessor;
import bulletcarpet.utils.ModUtils;
import carpet.fakes.ServerPlayerInterface;
import carpet.helpers.EntityPlayerActionPack;
import carpet.helpers.EntityPlayerActionPack.Action;
import carpet.helpers.EntityPlayerActionPack.ActionType;
import carpet.patches.EntityPlayerMPFake;
import com.google.gson.reflect.TypeToken;
import com.mojang.logging.LogUtils;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

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
import java.util.Map;

import static carpet.script.api.Auxiliary.GSON;

public class FakePlayerReloadHelper {

    private static final List<PlayerData> playersData = Collections.synchronizedList(new ArrayList<>());

    private static final String CONFIG_PATH = ModUtils.getConfigPath().resolve("fake_players.json").toString();

    private static final Logger LOGGER = LogUtils.getLogger();

    public static void registerFakePlayerInfo(ServerPlayerEntity player) {
        final String playerName = player.getName().getString();
        final Vec3d position = player.getPos();
        final float yaw = player.getYaw();
        final float pitch = player.getPitch();
        final String dimension = player.getWorld().getRegistryKey().getValue().getPath();
        final String gamemode = player.interactionManager.getGameMode().getName();
        final boolean isFlying = player.getAbilities().flying;

        boolean isSprinting = false;
        boolean isSneaking = false;
        float forward = 0.0f;
        float strafing = 0.0f;

        if (BulletCarpetSettings.saveFakePlayersActions) {
            EntityPlayerActionPackAccessor actionPackAccessor = (EntityPlayerActionPackAccessor) ((ServerPlayerInterface) player).getActionPack();
            isSneaking = actionPackAccessor.isSneaking();
            isSprinting = actionPackAccessor.isSprinting();
            forward = actionPackAccessor.getForward();
            strafing = actionPackAccessor.getStrafing();
        }

        List<ActionData> actions = getActionDataIfNeeded((ServerPlayerInterface) player, BulletCarpetSettings.saveFakePlayersActions);

        playersData.add(new PlayerData(playerName, position, yaw, pitch, dimension, gamemode, isFlying, isSneaking, isSprinting, forward, strafing, actions));
    }

    private static @NotNull List<ActionData> getActionDataIfNeeded(ServerPlayerInterface player, boolean needed) {
        List<ActionData> actions = new ArrayList<>();

        if (needed) {
            EntityPlayerActionPackAccessor actionPackAccessor = (EntityPlayerActionPackAccessor) ((ServerPlayerInterface) player).getActionPack();
            Map<ActionType, Action> playerActions = actionPackAccessor.getActions();

            for (Map.Entry<ActionType, Action> entry : playerActions.entrySet()) {
                ActionType actionType = entry.getKey();
                Action action = entry.getValue();

                actions.add(new ActionData(actionType.name(), action));
            }
        }
        return actions;
    }

    public static void saveFakePlayersInfo() {
        if (!playersData.isEmpty()) {
            File file = new File(CONFIG_PATH);

            try {
                ModUtils.ensureConfigFileExists(file.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            try (FileWriter writer = new FileWriter(CONFIG_PATH)) {
                LOGGER.info("Saving {} fake players data to {}", playersData.size(), CONFIG_PATH);
                GSON.toJson(playersData, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
            clearFakePlayers();
        }
    }

    public static void loadFakePlayers(MinecraftServer server) {
        List<PlayerData> players = getPlayerDataFromFile(CONFIG_PATH);

        LOGGER.info("Respawning fake players");

        for (PlayerData playerData : players) {
            Identifier dimensionId = new Identifier("minecraft", playerData.dimension);

            RegistryKey<World> dimensionKey = RegistryKey.of(RegistryKeys.WORLD, dimensionId);
            GameMode gameMode = GameMode.byName(playerData.gamemode);

            EntityPlayerMPFake fakePlayer = EntityPlayerMPFake.createFake(playerData.name, server, playerData.position.x, playerData.position.y, playerData.position.z, playerData.yaw, playerData.pitch, dimensionKey, gameMode, playerData.isFlying);

            EntityPlayerActionPack actionPack = ((ServerPlayerInterface) fakePlayer).getActionPack();

            actionPack.setSneaking(playerData.sneaking);
            actionPack.setSprinting(playerData.sprinting);
            actionPack.setForward(playerData.forward);
            actionPack.setStrafing(playerData.strafing);

            for (ActionData actionData : playerData.actions) {
                ActionType actionType = ActionType.valueOf(actionData.actionName);
                Action action = actionData.action;

                ((EntityPlayerActionPackAccessor) actionPack).getActions().put(actionType, action);
            }
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
                              boolean isFlying, boolean sneaking, boolean sprinting, float forward, float strafing,
                              List<ActionData> actions) {

    }

    private record ActionData(String actionName, Action action) {
    }


}
