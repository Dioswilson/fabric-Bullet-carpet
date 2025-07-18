package bulletcarpet.helpers.suggestionProviders;

import bulletcarpet.helpers.StatsHelper;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AllPlayersSuggestionProvider implements SuggestionProvider<ServerCommandSource> {

    public AllPlayersSuggestionProvider() {
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {

        final MinecraftServer server = context.getSource().getServer();



        return builder.buildFuture();
    }
}
