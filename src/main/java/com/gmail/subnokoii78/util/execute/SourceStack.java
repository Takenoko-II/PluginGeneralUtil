package com.gmail.subnokoii78.util.execute;

import com.gmail.subnokoii78.util.file.json.JSONArray;
import com.gmail.subnokoii78.util.file.json.JSONObject;
import com.gmail.subnokoii78.util.file.json.JSONSerializer;
import com.gmail.subnokoii78.util.vector.DualAxisRotationBuilder;
import com.gmail.subnokoii78.util.vector.Vector3Builder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ScoreComponent;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    private EntityAnchor anchor = new EntityAnchor(EntityAnchorType.FEET, this);

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
    }

    /**
     * コマンドの送信者(実行者)を取得します。
     * @return 送信者
     */
    public SourceOrigin<?> getSender() {
        return sender;
    }

    /**
     * コマンドの実行者を取得します。
     * @return 実行者がいればそのまま返し、いなければnull
     */
    public @Nullable Entity getExecutor() {
        return executor;
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
     * 実行アンカーを返します。
     * @return 実行アンカー(デフォルトでfeet)
     */
    public @NotNull EntityAnchor getEntityAnchor() {
        return anchor;
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

    protected void write(@NotNull EntityAnchorType anchor) {
        this.anchor = new EntityAnchor(anchor, this);
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
     * このソーススタックをコピーします。
     * @return コピーされたオブジェクト
     */
    public @NotNull SourceStack copy() {
        final SourceStack stack = new SourceStack(sender);
        stack.write(dimension);
        stack.write(executor);
        stack.write(location.copy());
        stack.write(rotation.copy());
        stack.anchor = anchor;
        stack.resultCallback = resultCallback;
        return stack;
    }

    /**
     * {@link JSONObject}としてソーススタックに格納されている情報を取得します。
     * @return シリアライズされたオブジェクト
     */
    public @NotNull JSONObject getAsJSONObject() {
        final JSONObject jsonObject = new JSONObject();

        jsonObject.set("sender", JSONComponentSerializer.json().serialize(sender.getName()));
        jsonObject.set("executor", hasExecutor() ? executor.getName() : "null");
        final JSONArray loc = new JSONArray();
        loc.add(location.x());
        loc.add(location.y());
        loc.add(location.z());
        jsonObject.set("location", loc);
        final JSONArray rot = new JSONArray();
        rot.add(rotation.yaw());
        rot.add(rotation.pitch());
        jsonObject.set("rotation", rot);
        jsonObject.set("anchor", anchor.getType().getId());
        jsonObject.set("dimension", VanillaDimensionProvider.get(dimension).getId());

        return jsonObject;
    }

    /**
     * コマンドを実行します。
     * @param command 実行するコマンド
     * @return 成功したときtrue、失敗すればfalse
     */
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
                anchor.getType().getId(),
                command
            );
        }
        else {
            commandString = String.format(
                "execute %s anchored %s run %s",
                common,
                anchor.getType().getId(),
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
     * 実行者が存在するかどうかをテストします。
     * @return 実行者が存在すればtrue、しなければfalse
     */
    public boolean hasExecutor() {
        return executor != null;
    }

    /**
     * 実行者が存在しないときに例外を投げることによって必ずnullでない実行者を返します。
     * @return 実行者
     * @throws IllegalStateException 実行者が存在しないとき
     */
    public @NotNull Entity requireExecutor() throws IllegalStateException {
        if (hasExecutor()) return executor;
        else {
            throw new IllegalStateException("実行者がいません");
        }
    }

    /**
     * 実行座標、実行方向、実行ディメンションの三つを一つの{@link Location}オブジェクトとして取得します。
     * @return 実行座標・実行方向・実行ディメンション
     */
    public @NotNull Location getAsBukkitLocation() {
        return location.withRotationAndWorld(rotation, dimension);
    }

    /**
     * この{@link SourceStack}オブジェクトを文字列として視覚化します。
     * @return 変換された文字列
     */
    @Override
    public @NotNull String toString() {
        return new JSONSerializer(getAsJSONObject()).serialize();
    }

    /**
     * 架空のコマンド送信者のオブジェクト
     */
    public static final CommandSender COMMAND_SENDER = Bukkit.createCommandSender(component -> {});
}
