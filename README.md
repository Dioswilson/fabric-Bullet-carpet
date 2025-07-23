<img src="./src/main/resources/assets/bulletcarpet/bulletLogo.png" align="right" width="128px"/>

# Bullet Carpet

This mod is porting some of the features of the original [Bullet Carpet](https://github.com/Dioswilson/Bullet-carpet)
to work with fabric and [Fabric Carpet](https://github.com/gnembon/fabric-carpet).<br>
It brings back some of 1.12's carpet features and adds some new ones we need.


# Bullet Carpet Features

## scoreboardStats

Enables scoreboard stats for players
It tracks all gamestats and adds the sum of `pickaxes`, `axes`, `shovels`, `hoes` and `all_tools`

* Type: `Boolean`
* Default value: `false`
* Allowed options: `true`, `false`
* Categories: `SURVIVAL`, `COMMAND`, `BULLET`

## addBotsToTeam

Adds bot to Bots team with dark green color

* Type: `Boolean`
* Default value: `true`
* Allowed options: `true`, `false`
* Categories:  `BULLET`

## botsNoStats

Bots stats don't get incremented except if bot is a shadow

* Type: `Boolean`
* Default value: `false`
* Allowed options: `true`, `false`
* Categories:  `BULLET`

## tickWarpNoTime

Tick warps with no time argument starts an indefinite time warp
(Except if it is already running)

* Type: `Boolean`
* Default value: `true`
* Allowed options: `true`, `false`
* Categories: `CREATIVE`, `COMMAND`, `BULLET`

## removeStatsCommand

Adds command to list or manually remove stats<br>
Players whose stats are being removed shouldn't be online<br>
You might need to restart the server for this to take full effect<br>
If you are using scoreboardStats, remember reinitializing the scoreboard

* Type: `Boolean`
* Default value: `false`
* Allowed options: `true`, `false`
* Categories: `CREATIVE`, `EXPERIMENTAL`, `COMMAND`, `BULLET`

## commandCameraMode

Enables /c and /s commands to quickly switch between spectator and server modes

* Type: `Boolean`
* Default value: `false`
* Allowed options: `true`, `false`
* Categories:  `COMMAND`, `BULLET`

## cameraModeRestoreLocation

Places players back to the original location when using camera mode by using /c then /s

* Type: `Boolean`
* Default value: `false`
* Allowed options: `true`, `false`
* Categories:  `COMMAND`, `BULLET`

## reloadFakePlayers

Reloads fake players on server startup that were loaded before server shutdown

* Type: `Boolean`
* Default value: `false`
* Allowed options: `true`, `false`
* Categories: `FEATURE` , `COMMAND`, `BULLET`

## saveFakePlayersActions

Saves fake players actions and restores them on server startup if reloadFakePlayers is enabled

* Type: `Boolean`
* Default value: `false`
* Allowed options: `true`, `false`
* Categories: `FEATURE` , `COMMAND`, `BULLET`

## instamineDeepslate

Allows instamining deepslate blocks with netherite pickaxe + haste 2
Consider using `silenceMismatchDestroyBlock` to avoid warnings thrown on console

* Type: `Boolean`
* Default value: `false`
* Allowed options: `true`, `false`
* Categories: `SURVIVAL`, `FEATURE` , `BULLET`

## silenceMismatchDestroyBlock

Silences warning about mismatched destroy block

* Type: `Boolean`
* Default value: `false`
* Allowed options: `true`, `false`
* Categories: `SURVIVAL`, `EXPERIMENTAL` , `BULLET`

## extremeBehaviours

Edge cases are as frequent as common cases, for testing only!!

The following things are affected:

1. Velocities of items from dispensers, blaze projectiles, fireworks
2. Directions of fireballs, wither skulls, fishing bobbers,
3. Items dropped from blocks and inventories, llamas spit, triggered trap horses
4. Damage dealt with projectiles
5. Blaze aggro sensitivity
6. Mobs spawned follow range
7. Campfire entities spawning
8. Items inside chiseled bookshelf

* Type: `Boolean`
* Default value: `false`
* Allowed options: `true`, `false`
* Categories: `CREATIVE` , `BULLET`

## stackableShulkersPlayerInventory
Allows empty shulkerboxes to stack in the player inventory
You also need client side mod for it to work on creative inventory

* Type: `Boolean`
* Default value: `false`
* Allowed options: `true`, `false`
* Categories: `SURVIVAL`, `FEATURE` , `BULLET`

