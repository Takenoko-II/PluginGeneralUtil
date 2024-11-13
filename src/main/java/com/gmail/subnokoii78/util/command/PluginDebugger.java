package com.gmail.subnokoii78.util.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class PluginDebugger {
    private PluginDebugger() {}

    private final Map<String, Function<CommandSourceStack, Integer>> functions = new HashMap<>();

    private int call(@NotNull CommandContext<CommandSourceStack> context) {
        final String id = context.getArgument("function_id", String.class);
        if (functions.containsKey(id)) return functions.get(id).apply(context.getSource());
        else return 0;
    }

    public void init(@NotNull String name, @NotNull Commands dispatcher) {
        dispatcher.register(Commands.literal(name).then(functionId().executes(this::call)).build());
    }

    public void register(@NotNull String id, Function<CommandSourceStack, Integer> function) throws IllegalArgumentException {
        functions.put(id, function);
    }

    private @NotNull RequiredArgumentBuilder<CommandSourceStack, String> functionId() {
        return Commands
            .argument("function_id", StringArgumentType.string())
            .suggests((context, builder) -> {
                for (final String id : functions.keySet()) {
                    builder.suggest(id);
                }
                return builder.buildFuture();
            });
    }

    public static final PluginDebugger DEFAULT_DEBUGGER = new PluginDebugger();
}
