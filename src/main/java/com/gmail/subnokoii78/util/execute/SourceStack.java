package com.gmail.subnokoii78.util.execute;

import com.gmail.subnokoii78.util.vector.DualAxisRotationBuilder;
import com.gmail.subnokoii78.util.vector.Vector3Builder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * executeコマンドにおける単一の実行文脈を表現するクラス
 */
public class SourceStack {
    private final SourceOrigin<?> sender;

    private Entity executor = null;

    private World dimension = VanillaDimensionProvider.OVERWORLD.getWorld();

    private final Vector3Builder location = new Vector3Builder();

    private final DualAxisRotationBuilder rotation = new DualAxisRotationBuilder();

    private final EntityAnchor entityAnchor = new EntityAnchor(this);

    private ResultCallback resultCallback = ResultCallback.EMPTY;

    /**
     * 初期状態のソーススタックを生成します。
     */
    public SourceStack() {
        this(SourceOrigin.of(Bukkit.getServer()));
    }

    /**
     * 引数に渡されたオブジェクトをコマンドの送信者としてソーススタックを生成します。
     * @param sender 送信者
     */
    public SourceStack(@NotNull SourceOrigin<?> sender) {
        this.sender = sender;
        sender.callOrigin(Entity.class, entity -> {
            write(entity);
            write(entity.getWorld());
            write(Vector3Builder.from(entity));
            write(DualAxisRotationBuilder.from(entity));
            return null;
        });

        sender.callOrigin(Block.class, block -> {
            write(block.getWorld());
            write(Vector3Builder.from(block.getLocation()));
            write(DualAxisRotationBuilder.from(block.getLocation()));
            return null;
        });
    }

    /**
     * コマンドの送信者(実行者)を取得します。
     * @return 送信者
     */
    public @NotNull SourceOrigin<?> getSender() {
        return sender;
    }

    /**
     * 実行者が存在するかどうかをテストします。
     * @return 実行者が存在すればtrue、しなければfalse
     */
    public boolean hasExecutor() {
        if (executor == null) return false;
        else return executor.isValid();
    }

    /**
     * コマンドの実行者を取得します。
     * <br>先に{@link SourceStack#hasExecutor()}を呼び出してください
     * @return 実行者
     */
    public @NotNull Entity getExecutor() throws IllegalStateException {
        if (hasExecutor()) return executor;
        else throw new IllegalStateException("実行者が存在しません");
    }

    /**
     * 実行ディメンションを返します。
     * @return 実行ディメンション
     */
    public @NotNull World getDimension() {
        return dimension;
    }

    /**
     * 実行座標を返します。
     * @return 実行座標(3次元ベクトル)
     */
    public @NotNull Vector3Builder getPosition() {
        return location.copy();
    }

    /**
     * 実行方向を返します。
     * @return 実行方向(2次元ベクトル)
     */
    public @NotNull DualAxisRotationBuilder getRotation() {
        return rotation.copy();
    }

    /**
     * 実行座標、実行方向、実行ディメンションの三つを一つの{@link Location}オブジェクトとして取得します。
     * @return 実行座標・実行方向・実行ディメンション
     */
    public @NotNull Location getLocation(@NotNull LocationGetOption... options) {
        final Set<LocationGetOption> set = Set.of(options);

        if (set.isEmpty()) {
            return location.withRotationAndWorld(rotation, dimension);
        }

        final Location loc = new Location(VanillaDimensionProvider.OVERWORLD.getWorld(), 0d, 0d, 0d, 0f, 0f);

        if (set.contains(LocationGetOption.DIMENSION)) {
            loc.setWorld(dimension);
        }
        if (set.contains(LocationGetOption.POSITION)) {
            loc.setX(location.x());
            loc.setY(location.y());
            loc.setZ(location.z());
        }
        if (set.contains(LocationGetOption.ROTATION)) {
            loc.setYaw(rotation.yaw());
            loc.setPitch(rotation.pitch());
        }

        return loc;
    }

    /**
     * 実行アンカーを返します。
     * @return 実行アンカー(デフォルトでfeet)
     */
    public @NotNull EntityAnchor getEntityAnchor() {
        return entityAnchor;
    }

    public @NotNull ResultCallback getCallback() {
        return resultCallback;
    }

    protected void write(@NotNull Entity executor) {
        this.executor = executor;
    }

    protected void write(@NotNull World dimension) {
        this.dimension = dimension;
    }

    protected void write(@NotNull Vector3Builder location) {
        this.location.x(location.x()).y(location.y()).z(location.z());
    }

    protected void write(@NotNull DualAxisRotationBuilder rotation) {
        this.rotation.yaw(rotation.yaw()).pitch(rotation.pitch());
    }

    protected void write(@NotNull EntityAnchor.Type type) {
        this.entityAnchor.setType(type);
    }

    protected void write(@NotNull StoreTarget storeTarget, @NotNull ResultConsumer resultConsumer) {
        this.resultCallback = this.resultCallback.chain(storeTarget, (successful, returnValue) -> {
            resultConsumer.accept(copy(), returnValue);
        });
    }

    private double parseAbsolutePos(@NotNull String input) {
        if (input.matches("^[+-]?\\d$")) {
            return Double.parseDouble(input) + 0.5d;
        }
        else if (input.matches("^[+-]?\\d+(?:\\.\\d+)?$")) {
            return Double.parseDouble(input);
        }
        else {
            throw new IllegalArgumentException("絶対座標が期待されています");
        }
    }

    private double parseRelativePos(@NotNull String input, int axis) {
        if (input.matches("^~(?:[+-]?\\d+(?:\\.\\d+)?)?$")) {
            final String number = input.substring(1);
            final double offset = number.isEmpty() ? 0 : Double.parseDouble(number);

            return switch (axis) {
                case 0 -> offset + location.x();
                case 1 -> offset + location.y();
                case 2 -> offset + location.z();
                default ->
                    throw new IllegalArgumentException("NEVER HAPPENS");
            };
        }
        else {
            throw new IllegalArgumentException("相対座標が期待されています");
        }
    }

    private @NotNull Vector3Builder parseLocalPos(@NotNull List<String> components) {
        final Vector3Builder out = location.copy();

        int i = 0;
        for (final String input : components) {
            if (input.matches("^\\^(?:[+-]?\\d+(?:\\.\\d+)?)?$")) {
                final Vector3Builder.LocalAxisProvider localAxis = rotation.getDirection3d().getLocalAxisProvider();
                final String number = input.substring(1);
                final double length = number.isEmpty() ? 0 : Double.parseDouble(number);

                final Vector3Builder offset = switch (i) {
                    case 0 -> localAxis.getX().length(length);
                    case 1 -> localAxis.getY().length(length);
                    case 2 -> localAxis.getZ().length(length);
                    default ->
                        throw new IllegalArgumentException("NEVER HAPPENS");
                };

                out.add(offset);
            }
            else {
                throw new IllegalArgumentException("ローカル座標が期待されています");
            }

            i++;
        }

        return out.add(getEntityAnchor().getOffset());
    }

    /**
     * コマンドの引数としての形式で記述された座標を読み取ります。
     * @param coordinates 解析する文字列
     * @return 絶対座標
     */
    public @NotNull Vector3Builder readCoordinates(@NotNull String coordinates) {
        final List<String> componentInputs = List.of(coordinates.split("\\s"));
        final List<Double> componentOutputs = new ArrayList<>();

        if (componentInputs.size() != 3) throw new IllegalArgumentException("座標は三軸です");

        for (int i = 0; i < componentInputs.size(); i++) {
            final String value = componentInputs.get(i);

            if (value.startsWith("~")) {
                componentOutputs.add(parseRelativePos(value, i));
            }
            else if (value.startsWith("^")) {
                if (i == 0) {
                    final Vector3Builder v = parseLocalPos(componentInputs);
                    componentOutputs.addAll(List.of(v.x(), v.y(), v.z()));
                    break;
                }
                else throw new IllegalArgumentException("ローカル座標とほかの記述形式を混ぜることはできません");
            }
            else {
                componentOutputs.add(parseAbsolutePos(value));
            }
        }

        return new Vector3Builder(componentOutputs.get(0), componentOutputs.get(1), componentOutputs.get(2));
    }

    private float parseAbsoluteRot(@NotNull String input) {
        if (input.matches("^[+-]?\\d+(?:\\.\\d+)?$")) {
            return Float.parseFloat(input);
        }
        else {
            throw new IllegalArgumentException("絶対角度が期待されています");
        }
    }

    private float parseRelativeRot(@NotNull String input, int axis) {
        if (input.matches("^~(?:[+-]?\\d+(?:\\.\\d+)?)?$")) {
            final String number = input.substring(1);
            final float offset = number.isEmpty() ? 0 : Float.parseFloat(number);

            return switch (axis) {
                case 0 -> offset + rotation.yaw();
                case 1 -> offset + rotation.pitch();
                default ->
                    throw new IllegalArgumentException("NEVER HAPPENS");
            };
        }
        else {
            throw new IllegalArgumentException("相対座標が期待されています");
        }
    }

    /**
     * コマンドの引数としての形式で記述された角度を読み取ります。
     * @param angles 解析する文字列
     * @return 絶対回転
     */
    public @NotNull DualAxisRotationBuilder readAngles(@NotNull String angles) {
        final List<String> componentInputs = List.of(angles.split("\\s"));
        final List<Float> componentOutputs = new ArrayList<>();

        if (componentInputs.size() != 2) throw new IllegalArgumentException("座標は二軸です");

        for (int i = 0; i < componentInputs.size(); i++) {
            final String value = componentInputs.get(i);

            if (value.startsWith("~")) {
                componentOutputs.add(parseRelativeRot(value, i));
            }
            else {
                componentOutputs.add(parseAbsoluteRot(value));
            }
        }

        return new DualAxisRotationBuilder(componentOutputs.get(0), componentOutputs.get(1));
    }

    /**
     * コマンドの引数としての形式で記述された軸を読み取ります。
     * @param axes 解析する文字列
     * @return 軸の文字のSet
     */
    public static @NotNull Set<Character> readAxes(@NotNull String axes) {
        final Set<String> chars = Set.of(axes.split(""));

        if (axes.length() > 3) throw new IllegalArgumentException("軸は3つまで指定可能です");
        else if (axes.length() != chars.size()) throw new IllegalArgumentException("軸が重複しています");
        else if (!Set.of("x", "y", "z").containsAll(chars)) {
            throw new IllegalArgumentException("x, y, zの文字が有効です");
        }

        return chars.stream()
            .map(c -> c.charAt(0))
            .collect(Collectors.toSet());
    }

    /**
     * 渡されたセレクターから処理順に従ってエンティティを取得します。
     * @param selector セレクター
     * @return エンティティのリスト
     */
    public <T extends Entity> @NotNull List<T> getEntities(@NotNull EntitySelector<T> selector) {
        return selector.getEntities(this);
    }

    /**
     * 渡されたセレクターから処理順に従ってエンティティを取得します。
     * @param selector セレクター
     * @return エンティティのリスト
     */
    public <T extends Entity> @NotNull List<T> getEntities(@NotNull EntitySelector.Builder<T> selector) {
        return getEntities(selector.build());
    }

    /**
     * このソーススタックをコピーします。
     * @return コピーされたオブジェクト
     */
    public @NotNull SourceStack copy() {
        final SourceStack stack = new SourceStack(sender);
        stack.write(dimension);
        stack.write(executor);
        stack.write(location.copy());
        stack.write(rotation.copy());
        stack.write(entityAnchor.getType());
        stack.resultCallback = resultCallback;
        return stack;
    }

    /**
     * コマンドを実行します。
     * @param command 実行するコマンド
     * @return 成功したときtrue、失敗すればfalse
     * @apiNote 整数を返さないため、使用を推奨しません。
     */
    @ApiStatus.Obsolete
    public boolean runCommand(@NotNull String command) {
        final String common = String.format(
            "in %s positioned %s rotated %s",
            VanillaDimensionProvider.get(dimension).getId(),
            location.format("$c $c $c", 5),
            rotation.format("$c $c", 5)
        );

        final String commandString;

        if (hasExecutor()) {
            commandString = String.format(
                "execute %s as %s anchored %s run %s",
                common,
                executor.getUniqueId(),
                entityAnchor.getType().getId(),
                command
            );
        }
        else {
            commandString = String.format(
                "execute %s anchored %s run %s",
                common,
                entityAnchor.getType().getId(),
                command
            );
        }

        try {
            return Bukkit.getServer().dispatchCommand(COMMAND_SENDER, commandString);
        }
        catch (CommandException e) {
            return false;
        }
    }

    /**
     * 架空のコマンド送信者のオブジェクト
     */
    public static final CommandSender COMMAND_SENDER = Bukkit.createCommandSender(component -> {});
}
