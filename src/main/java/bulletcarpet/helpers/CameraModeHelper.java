package bulletcarpet.helpers;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.UUID;

public class CameraModeHelper {

    private static final HashMap<UUID, PlayerPositionData> originalCameraPositions = new HashMap<>();
    private static CameraModeHelper INSTANCE;

    private CameraModeHelper() {
    }

    public static CameraModeHelper getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CameraModeHelper();
        }
        return CameraModeHelper.INSTANCE;
    }

    public void storePlayerPos(ServerPlayerEntity player) {
        PlayerPositionData playerPositionData = new PlayerPositionData(player.getPos(), player.getYaw(), player.getPitch());
        originalCameraPositions.put(player.getUuid(), playerPositionData);
    }

    public boolean restorePlayerPos(ServerPlayerEntity player) {
        boolean positionRestored = false;
        if (originalCameraPositions.containsKey(player.getUuid())) {
            PlayerPositionData originalPos = originalCameraPositions.get(player.getUuid());

            player.teleport(originalPos.position.x, originalPos.position.y, originalPos.position.z);
            player.refreshPositionAndAngles(originalPos.position.x, originalPos.position.y, originalPos.position.z,
                    originalPos.yaw, originalPos.pitch);

            originalCameraPositions.remove(player.getUuid());
            positionRestored = true;
        }
        return positionRestored;
    }

    private class PlayerPositionData {
        public final Vec3d position;
        public final float yaw;
        public final float pitch;

        public PlayerPositionData(Vec3d originalPos, float yaw, float pitch) {
            this.position = originalPos;
            this.yaw = yaw;
            this.pitch = pitch;
        }
    }


}
