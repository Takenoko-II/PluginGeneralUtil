package com.gmail.subnokoii78.util.execute;

import net.kyori.adventure.text.TextComponent;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

/**
 * executeコマンドの送信者(実行者ではない)を表現するクラス
 * @param <T> 制限なし
 */
public class ExecuteSender<T> {
    final T sender;

    private ExecuteSender(@NotNull T sender) {
        this.sender = sender;
    }

    /**
     * 名前を取得します。
     * <br>コマンドブロック以外のブロックに限りIDを返します。
     * @return 送信者名
     */
    public @NotNull String getName() {
        return switch (this) {
            case EntityExecuteSender entity -> entity.sender.getName();
            case BlockExecuteSender block -> {
                if (block.sender.getState() instanceof CommandBlock commandBlock) {
                    if (commandBlock.name() instanceof TextComponent textComponent) {
                        yield textComponent.content();
                    }
                    else {
                        yield commandBlock.getBlock().getType().getKey().toString();
                    }
                }
                else {
                    yield block.sender.getType().getKey().toString();
                }
            }
            case ConsoleExecuteSender console -> console.sender.getName();
            case ServerExecuteSender server -> server.sender.getName();
            default -> throw new IllegalArgumentException("無効な送信者です");
        };
    }

    /**
     * メッセージを送信します。
     * <br>ブロック・サーバーが送信者の場合メッセージは送信できません。
     * @param message メッセージ
     */
    public void sendMessage(@NotNull TextComponent message) {
        switch (this) {
            case EntityExecuteSender entity:
                entity.sender.sendMessage(message);
                break;
            case ConsoleExecuteSender console:
                console.sender.sendMessage(message);
                break;
            default:
                break;
        }
    }

    private static final class EntityExecuteSender extends ExecuteSender<Entity> {
        private EntityExecuteSender(@NotNull Entity sender) {
            super(sender);
        }
    }

    private static final class BlockExecuteSender extends ExecuteSender<Block> {
        private BlockExecuteSender(@NotNull Block sender) {
            super(sender);
        }
    }

    private static final class ConsoleExecuteSender extends ExecuteSender<ConsoleCommandSender> {
        private ConsoleExecuteSender(@NotNull ConsoleCommandSender sender) {
            super(sender);
        }
    }

    private static final class ServerExecuteSender extends ExecuteSender<Server> {
        private ServerExecuteSender(@NotNull Server server) {
            super(server);
        }
    }

    /**
     * エンティティからコマンド送信者を作成します。
     * @param entity 送信者
     * @return {@link ExecuteSender}
     */
    public static @NotNull ExecuteSender<Entity> of(@NotNull Entity entity) {
        return new EntityExecuteSender(entity);
    }

    /**
     * ブロックからコマンド送信者を作成します。
     * @param block 送信者
     * @return {@link ExecuteSender}
     */
    public static @NotNull ExecuteSender<Block> of(@NotNull Block block) {
        return new BlockExecuteSender(block);
    }

    /**
     * コンソールからコマンド送信者を作成します。
     * @param consoleCommandSender 送信者
     * @return {@link ExecuteSender}
     */
    public static @NotNull ExecuteSender<ConsoleCommandSender> of(@NotNull ConsoleCommandSender consoleCommandSender) {
        return new ConsoleExecuteSender(consoleCommandSender);
    }

    /**
     * サーバーからコマンド送信者を作成します。
     * @param server 送信者
     * @return {@link ExecuteSender}
     */
    public static @NotNull ExecuteSender<Server> of(@NotNull Server server) {
        return new ServerExecuteSender(server);
    }
}
