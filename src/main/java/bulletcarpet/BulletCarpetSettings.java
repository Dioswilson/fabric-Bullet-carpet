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
    public static boolean scoreboardStats = true;

    @Rule(
            categories = {BULLET}
    )
    public static boolean addBotsToTeam = true;

    @Rule(
            categories = {BULLET}
    )
    public static boolean botsNoStats = true;

    @Rule(
            categories = {CREATIVE, COMMAND, BULLET}
    )
    public static boolean tickWarpNoTime = true;

    @Rule(
            categories = {CREATIVE, EXPERIMENTAL, COMMAND, BULLET}
    )
    public static boolean removeStatsCommand = true;

    @Rule(
            categories = {COMMAND, BULLET}
    )
    public static boolean commandCameraMode = true;

    @Rule(
            categories = {COMMAND, BULLET}
   )
    public static boolean cameraModeRestoreLocation = true;

    @Rule(
            categories = {FEATURE, COMMAND, BULLET}
    )
    public static boolean reloadFakePlayers = true;

    @Rule(
            categories = {FEATURE, COMMAND, BULLET}
    )
    public static boolean saveFakePlayersActions = true;

    @Rule(
            categories = {SURVIVAL, FEATURE, BULLET}
    )
    public static boolean instamineDeepslate = true;

    @Rule(
            categories = {CREATIVE, BULLET}
    )
    public static boolean extremeBehaviours = true;

    //Stolen from Carpet extra (https://github.com/gnembon/carpet-extra)
    @Rule(
            categories = {SURVIVAL, FEATURE, EXTRA, BULLET}
    )
    public static boolean accurateBlockPlacement = true;

    @Rule(
            categories = {BUGFIX, EXTRA, BULLET}
    )
    public static boolean blockStateSyncing = true;

}
