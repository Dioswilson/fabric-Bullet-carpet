package bulletcarpet.utils;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ModUtils {
    public static String NAMESPACE = "bullet-carpet";

    public static boolean playerInventoryShulkerStacking = false;

    private static MinecraftServer server;

    public static void setMinecraftServer(MinecraftServer minecraftServer) {
        server = minecraftServer;
    }

    public static Path getConfigPath() {
        FabricLoader fabricInstance = FabricLoader.getInstance();

        Path path = fabricInstance.getConfigDir();
        ;

        if (fabricInstance.getEnvironmentType() != EnvType.SERVER) {
            if (server != null) {
                path = server.getSavePath(WorldSavePath.ROOT);
            }
        }

        return path.resolve(NAMESPACE);
    }

    public static String getStatInitFileName() {
        return getConfigPath().resolve("bulletcarpetStatsInit").toAbsolutePath().toString();

    }

   public static void ensureConfigFileExists(Path configPath) throws IOException {
        Files.createDirectories(configPath.getParent());
        if (!Files.exists(configPath)) {
            Files.createFile(configPath);
        }
    }

}
