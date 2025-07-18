package bulletcarpet.commands.scoreboardStats;

import bulletcarpet.BulletCarpetSettings;
import bulletcarpet.helpers.suggestionProviders.ScoreboardSlotSuggestionProvider;
import bulletcarpet.helpers.StatsHelper;
import bulletcarpet.mixins.ScoreboardCriteriaMixin;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.registry.Registries;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardCriterion.RenderType;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ScoreboardStatsCommand {

    private static final String NAME = "sb";
    private static final String SCORE_PREFIX = "st.";


    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {

        LiteralArgumentBuilder<ServerCommandSource> commandBuilder = literal(NAME).requires(source -> BulletCarpetSettings.scoreboardStats && source.isExecutedByPlayer());


        Registries.STAT_TYPE.getIds().forEach((type) -> {
            String filteredType = type.toTranslationKey().replace("minecraft.", "");

            if (!filteredType.equals("custom")) {
                for (Identifier itemIdentifier : Registries.ITEM.getIds()) {
                    String itemName = itemIdentifier.getPath();
                    commandBuilder.then(literal(filteredType).then(literal(itemName).
                            executes(c -> executeStats(c, itemIdentifier.getNamespace(), filteredType + ":", itemName, "sidebar")).
                            then(argument("displaySlot", StringArgumentType.word()).
                                    suggests(new ScoreboardSlotSuggestionProvider()).
                                    executes(c -> executeStats(c, itemIdentifier.getNamespace(), filteredType + ":", itemName, StringArgumentType.getString(c, "displaySlot")))))
                    );
                }
            }
            //killed ||custom || mined || picked_up || used || dropped ||crafted || killed_by || broken
        });


        Set<String> customCriterias = new TreeSet<>(ScoreboardCriteriaMixin.getCriteria().keySet());
        customCriterias.remove("air");
        customCriterias.remove("armor");
        customCriterias.remove("dummy");
        customCriterias.remove("food");
        customCriterias.remove("health"); //Maybe dejo health?
        customCriterias.remove("trigger");


        for (String key : customCriterias) {
            key = key.replace("minecraft.", "");
            String filteredKey;
            String prefix;

            if (key.contains("custom:")) {
                filteredKey = key.replace("custom:", "");
                prefix = "custom:";
            }
            else {
                filteredKey = key;
                prefix = "";
            }

            if (!key.contains("teamkill.")) { //Maybe si hay team de bots, añadir excepción
                commandBuilder.then(literal(filteredKey).executes(c -> executeSpecialStats(c, prefix, filteredKey, "sidebar")).
                        then(argument("displaySlot", StringArgumentType.word()).
                                suggests(new ScoreboardSlotSuggestionProvider()).
                                executes(c -> executeSpecialStats(c, prefix, filteredKey, StringArgumentType.getString(c, "displaySlot")))));
            }
        }

        commandBuilder.then(
                literal("clear").executes(c -> alterScoreboardSlot(c, "sidebar", ScoreboardAction.CLEAR)).
                        then(argument("displaySlot", StringArgumentType.word()).
                                suggests(new ScoreboardSlotSuggestionProvider("all"))
                                .executes(c -> alterScoreboardSlot(c, StringArgumentType.getString(c, "displaySlot"), ScoreboardAction.CLEAR))));
        commandBuilder.then(
                literal("hide").executes(c -> alterScoreboardSlot(c, "sidebar", ScoreboardAction.HIDE)).
                        then(argument("displaySlot", StringArgumentType.word()).
                                suggests(new ScoreboardSlotSuggestionProvider("all"))
                                .executes(c -> alterScoreboardSlot(c, StringArgumentType.getString(c, "displaySlot"), ScoreboardAction.HIDE)))
        );
        //TODO: help message

        dispatcher.register(commandBuilder);
    }


    //Todo: Don't like two almost identical functions
    private static int executeStats(CommandContext<ServerCommandSource> c, String namespace, String prefix, String itemName, String displaySlot) throws CommandSyntaxException {
        createAndShowObjective(c, prefix + namespace + "." + itemName, displaySlot);
        return notifyModification(c, displaySlot);
    }

    private static int executeSpecialStats(CommandContext<ServerCommandSource> c, String prefix, String statType, String displaySlot) throws CommandSyntaxException {
        executeStats(c, "", prefix, statType, displaySlot);
        return notifyModification(c, displaySlot);
    }

    private static void createAndShowObjective(CommandContext<ServerCommandSource> c, String criterionName, String displayName, String displaySlot) throws CommandSyntaxException {
        int displaySlotId = Scoreboard.getDisplaySlotId(displaySlot);
        if (displaySlotId == -1) {
            throw new SimpleCommandExceptionType(Text.literal(Formatting.RED + "You can only display it on list, sidebar or belowName")).create();
        }

        Scoreboard scoreboard = c.getSource().getWorld().getScoreboard();

        String scoreboardName = SCORE_PREFIX + criterionName.replace(":", ".");//Después vemos más cambios

        ScoreboardObjective displayObjective = scoreboard.getObjective(scoreboardName);

        if (displayObjective == null) {
            final ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();

            ScoreboardCriterion statCriterion = ScoreboardCriterion.getOrCreateStatCriterion(criterionName).get();//Should not throw error

            MutableText styledTitle = Text.literal(displayName).styled(style -> style.withColor(Formatting.GOLD));

            displayObjective = scoreboard.addObjective(scoreboardName, statCriterion, styledTitle, RenderType.INTEGER);
            //TODO:Async?
            singleThreadExecutor.execute(() -> {
                StatsHelper.initializeScoreboard(c.getSource().getServer(), scoreboardName);
            });

            singleThreadExecutor.shutdown();
        }

        scoreboard.setObjectiveSlot(displaySlotId, displayObjective);
    }

    private static void createAndShowObjective(CommandContext<ServerCommandSource> c, String criterionName, String displaySlot) throws CommandSyntaxException {
        //Maybe string builder?
        String displayName = criterionName.substring(criterionName.indexOf(":") + 1);
        displayName = displayName.substring(displayName.indexOf(".") + 1);

        String typeName = criterionName.substring(0, criterionName.indexOf(":"));

        if (typeName.equals("custom")) {
            typeName = "";
        }

        displayName = displayName.substring(0, 1).toUpperCase() +
                displayName.replace('_', ' ').substring(1)
                + " " + typeName;

        createAndShowObjective(c, criterionName, displayName, displaySlot);
    }

    private static void deleteAllScores(Scoreboard scoreboard) {
        Collection<ScoreboardObjective> allObjectives = scoreboard.getObjectives();
        Iterator<ScoreboardObjective> scoresIterator = allObjectives.iterator();
        while (scoresIterator.hasNext()) {
            ScoreboardObjective scoreObjective = scoresIterator.next();
            if (scoreObjective.getName().startsWith("st.")) {
                scoresIterator.remove();
            }
        }
        for (int i = 0; i <= 2; i++) {
            scoreboard.setObjectiveSlot(i, null);
        }
    }

    private enum ScoreboardAction {
        HIDE, CLEAR
    }

    private static int alterScoreboardSlot(CommandContext<ServerCommandSource> c, String displaySlot, ScoreboardAction action) throws CommandSyntaxException {
        Scoreboard scoreboard = c.getSource().getWorld().getScoreboard();
        int displayIdentifier = Scoreboard.getDisplaySlotId(displaySlot);

        if (displayIdentifier == -1) {
            if (displaySlot.equals("all")) {
                int failedScoreboards = 0;
                switch (action) {
                    case HIDE -> {
                        try {
                            alterScoreboardSlot(c, "sidebar", ScoreboardAction.HIDE);
                        } catch (Exception e) {
                            failedScoreboards++;
                        }
                        try {
                            alterScoreboardSlot(c, "list", ScoreboardAction.HIDE);
                        } catch (Exception e) {
                            failedScoreboards++;
                        }
                        try {
                            alterScoreboardSlot(c, "belowName", ScoreboardAction.HIDE);
                        } catch (Exception e) {
                            failedScoreboards++;
                        }
                        if (failedScoreboards == 3) {
                            c.getSource().sendFeedback(Text.literal("Nothing to hide"), false);
                        }
                        //Ew..... so ugly
                    }
                    case CLEAR -> {
                        deleteAllScores(scoreboard);
                        c.getSource().getServer().getPlayerManager().broadcast(Text.literal(Formatting.GRAY + "Cleared all scores"), false);
                    }
                }
                return 0;
            }
            else {
                throw new SimpleCommandExceptionType(Text.literal(Formatting.GRAY + "You can only " + action.name().toLowerCase() + " it on list, sidebar or belowName")).create();
            }
        }

        ScoreboardObjective objective = scoreboard.getObjectiveForSlot(displayIdentifier);
        if (objective != null) {
            if (objective.getName().startsWith("st.")) {
                switch (action) {
                    case HIDE -> scoreboard.setObjectiveSlot(displayIdentifier, null);
                    case CLEAR -> scoreboard.removeObjective(objective);
                }
            }
            else {
                throw new SimpleCommandExceptionType(Text.literal(Formatting.RED + "You can't " + action.name().toLowerCase() + " a handmade scoreboard")).create();
            }
        }
        else {
            throw new SimpleCommandExceptionType(Text.literal(Formatting.WHITE + "There is no scoreboard to " + action.name().toLowerCase() + " on " + displaySlot)).create();
        }
        return notifyModification(c, displaySlot);
    }

    private static int notifyModification(CommandContext<ServerCommandSource> c, String displaySlot) {
        String playerName = c.getSource().getPlayer() != null ? c.getSource().getPlayer().getName().getString() : "Console";

        String message = displaySlot.substring(0, 1).toUpperCase() + displaySlot.substring(1) +
                " changed by " + playerName;

        c.getSource().getServer().getPlayerManager().broadcast(Text.literal(Formatting.GRAY + message), false);
//        c.getSource().sendFeedback(Text.literal(Formatting.GRAY + message), false);

        return 0;
    }
}
