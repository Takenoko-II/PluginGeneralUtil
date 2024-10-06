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
    public static final PluginDebugger INSTANCE = new PluginDebugger();

    private PluginDebugger() {}

    private final Map<String, Function<CommandContext<CommandSourceStack>, Integer>> functionMap = new HashMap<>();

    public void init(@NotNull String name, @NotNull Commands dispatcher) {
        dispatcher.register(
            Commands.literal(name)
                .then(
                    functionId()
                        .executes(ctx -> {
                            return functionMap.get(ctx.getArgument("function_id", String.class)).apply(ctx);
                        })
                        .then(
                            Commands.argument("args", StringArgumentType.string())
                                .executes(ctx -> {
                                    return functionMap.get(ctx.getArgument("function_id", String.class)).apply(ctx);
                                })
                        )
                )
                .build()
        );
    }

    public void register(@NotNull String id, Function<CommandContext<CommandSourceStack>, Integer>function) {
        functionMap.put(id, function);
    }

    private @NotNull RequiredArgumentBuilder<CommandSourceStack, String> functionId() {
        return Commands
            .argument("function_id", StringArgumentType.string())
            .suggests((context, builder) -> {
                for (final String id : functionMap.keySet()) {
                    builder.suggest(id);
                }
                return builder.buildFuture();
            });
    }
}
