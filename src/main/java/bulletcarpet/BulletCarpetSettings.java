package bulletcarpet;

import carpet.api.settings.Rule;

import static carpet.api.settings.RuleCategory.*;

/**
 * Here is your example Settings class you can plug to use carpetmod /carpet settings command
 */
public class BulletCarpetSettings {
    public static String NAMESPACE = "bullet-carpet";

    public static final String BULLET = "Bullet";

    @Rule(
            categories = {SURVIVAL, COMMAND, BULLET}
    )
    public static boolean scoreboardStats;

    @Rule(
            categories = {BULLET}
    )
    public static boolean addBotsToTeam = true;

    @Rule(
            categories = {BULLET}
    )
    public static boolean botsNoStats;

    @Rule(
            categories = {CREATIVE, COMMAND, BULLET}
    )
    public static boolean tickWarpNoTime = true;

    @Rule(
            categories = {CREATIVE, EXPERIMENTAL, COMMAND, BULLET}
    )
    public static boolean removeStatsCommand;

    @Rule(
            categories = {COMMAND, BULLET}
    )
    public static boolean commandCameraMode;

    @Rule(
            categories = {COMMAND, BULLET}
    )
    public static boolean cameraModeRestoreLocation;

    @Rule(
            categories = {FEATURE, COMMAND, BULLET}
    )
    public static boolean reloadFakePlayers;

    @Rule(
            categories = {FEATURE, COMMAND, BULLET}
    )
    public static boolean saveFakePlayersActions;

    @Rule(
            categories = {SURVIVAL, FEATURE, BULLET}
    )
    public static boolean instamineDeepslate;

    @Rule(
            categories = {CREATIVE, BULLET}
    )
    public static boolean extremeBehaviours;

    @Rule(
            categories = {SURVIVAL, FEATURE, BULLET}
    )
    public static boolean stackableShulkersPlayerInventory;
}
