package bulletcarpet.helpers.suggestionProviders;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.ServerCommandSource;

import java.util.concurrent.CompletableFuture;

//Maybe a bit overkill, lol
public class ScoreboardSlotSuggestionProvider implements SuggestionProvider<ServerCommandSource> {

    private String[] customOptions = null;

    private static final String[] slots = new String[]{
            "list", "sidebar", "belowName"
    };

    public ScoreboardSlotSuggestionProvider() {
    }

    public ScoreboardSlotSuggestionProvider(String... customOptions) {
        this.customOptions = customOptions;
    }


    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {

        for (String slot : slots) {
            builder.suggest(slot);
        }
        if (customOptions != null) {
            for (String extra : customOptions) {
                builder.suggest(extra);
            }
        }

        return builder.buildFuture();
    }
}
