package bulletcarpet.helpers;

import bulletcarpet.BulletCarpetSettings;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.stat.StatFormatter;
import net.minecraft.util.Identifier;

import static net.minecraft.stat.Stats.CUSTOM;

public class CustomStats {
    public static final Identifier HOURS_PLAYED = register("hours_played", StatFormatter.TIME);

    private static Identifier register(String id, StatFormatter formatter) {
        Identifier identifier = new Identifier(BulletCarpetSettings.NAMESPACE, id);
        Registry.register(Registries.CUSTOM_STAT, (String) id, identifier);
        CUSTOM.getOrCreateStat(identifier, formatter);
        return identifier;
    }
    public static void initialize() {
        // This method is used to ensure the class is loaded and the stats are registered.
        // No additional initialization logic is needed at this time.
    }
}
