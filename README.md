# PluginGeneralUtil
プラグインのための汎用的なユーティリティたち

## vector パッケージ
### [Vector3Builder](src/main/java/com/gmail/subnokoii78/util/vector/Vector3Builder.java)
三次元ベクトルを操作するためのクラス
<br>bukkit用の機能もあるよ

### [DualAxisRotationBuilder](src/main/java/com/gmail/subnokoii78/util/vector/DualAxisRotationBuilder.java)
エンティティの向きなどの二軸で表される向きを操作するためのクラス
<br>bukkit用の機能も以下略

### [TripleAxisRotationBuilder](src/main/java/com/gmail/subnokoii78/util/vector/TripleAxisRotationBuilder.java)
ディスプレイエンティティの向きなどの三軸で表される向きを操作するためのクラス
<br>bukkit以下略

### [TiltedBoundingBox](src/main/java/com/gmail/subnokoii78/util/vector/TiltedBoundingBox.java)
向きにおいてディスプレイエンティティに匹敵する自由度を持つ当たり判定のクラス

## ui パッケージ
### [ContainerUI](src/main/java/com/gmail/subnokoii78/util/ui/ContainerUI.java)
コンテナ(チェスト型)のUIを作成するためのクラス

### [ItemButton](src/main/java/com/gmail/subnokoii78/util/ui/ItemButton.java)
ContainerUIクラスで使用されるボタンを表現するクラス
<br>その他のファイルはItemButtonを継承したクラス

## shape パッケージ (Shape API)
### [ShapeTemplate](src/main/java/com/gmail/subnokoii78/util/shape/ShapeTemplate.java)
図形のテンプレートを作成するためのクラス

### [ShapeBase](src/main/java/com/gmail/subnokoii78/util/shape/ShapeBase.java)
図形を表現する抽象クラス
<br>その他のファイルはShapeBaseを継承したクラス

## itemstack パッケージ
### [ItemStackBuilder](src/main/java/com/gmail/subnokoii78/util/itemstack/ItemStackBuilder.java)
ItemStackオブジェクトの作成を単純化するクラス

### [ComponentItemStackBuilder](src/main/java/com/gmail/subnokoii78/util/itemstack/components/ComponentItemStackBuilder.java)
コンポーネントベースのItemStackBuilderクラス
<br>コマンドに近い感覚で操作が可能

### [PotionContent](src/main/java/com/gmail/subnokoii78/util/itemstack/PotionContent.java)
ポーションコンポーネントで使用されるクラス

### [TypedAttributeModifier](src/main/java/com/gmail/subnokoii78/util/itemstack/TypedAttributeModifier.java)
アトリビュートコンポーネントで使用されるクラス

### [ItemStackComponentType](src/main/java/com/gmail/subnokoii78/util/itemstack/components/ItemStackComponentType.java)
アイテムから取得するコンポーネントの指定に使用されるクラス
<br>コンポーネントの種類を表現するクラス

### [ItemStackComponent](src/main/java/com/gmail/subnokoii78/util/itemstack/components/ItemStackComponent.java)
コンポーネントそのものを表現するクラス
<br>その他のファイルはItemStackComponentを継承したクラス

## file パッケージ
### [ServerDirectoryManager](src/main/java/com/gmail/subnokoii78/util/file/ServerDirectoryFileManager.java)
サーバーフォルダ内の主なファイルへの書き込みと読み取りをサポートするクラス

### [ServerDirectoryPaths](src/main/java/com/gmail/subnokoii78/util/file/ServerDirectoryPaths.java)
サーバーフォルダ内の主なファイルのパスの列挙型
<br><br>その他のファイル使用非推奨

### json パッケージ
json文字列のシリアライズ・デシリアライズ、jsonファイルの読み取り・書き込みなどjsonに対する操作を提供するクラス

## scoreboard パッケージ
### [ScoreboardUtils](src/main/java/com/gmail/subnokoii78/util/scoreboard/ScoreboardUtils.java)
スコアボードの操作を容易にするクラス

## random パッケージ
### [Xorshift128](src/main/java/com/gmail/subnokoii78/util/random/Xorshift128.java)
Xorshift128を実装したクラス
<br>高性能な乱数を生成するときに使う

## function パッケージ
### [TriFunction](src/main/java/com/gmail/subnokoii78/util/function/TriFunction.java)
引数を3つ取る関数型インターフェース

## schedule パッケージ
### [Scheduler](src/main/java/com/gmail/subnokoii78/util/schedule/Scheduler.java)
スケジューラを表現するインターフェース

### [RealTimeScheduler](src/main/java/com/gmail/subnokoii78/util/schedule/RealTimeScheduler.java)
現実時間を使用するスケジューラ

### [AbstractGameTickScheduler](src/main/java/com/gmail/subnokoii78/util/schedule/AbstractGameTickScheduler.java)
ゲームティックを使用するスケジューラ

## other パッケージ
### [TupleT](src/main/java/com/gmail/subnokoii78/util/other/TupleT.java)
長さ2の配列のクラス

### [TupleLR](src/main/java/com/gmail/subnokoii78/util/other/TupleLR.java)
大きさ2のタプルを表現するクラス

### [ServerMessageBuilder](src/main/java/com/gmail/subnokoii78/util/other/ServerMessageBuilder.java)
サーバーに流すチャットメッセージを作るためのクラス
<br>これはBoA用

### [PaperVelocityManager](src/main/java/com/gmail/subnokoii78/util/other/PaperVelocityManager.java)
Velocityとの通信を行うクラス
<br>これはBoA用

### [CalcExpEvaluator](src/main/java/com/gmail/subnokoii78/util/other/CalcExpEvaluator.java)
計算式を解析し評価するクラス
<br>関数や定数の定義が可能なため簡易的なゲーム内スクリプトとして使用予定

## datacontainer パッケージ
### [DataContainerCompound](src/main/java/com/gmail/subnokoii78/util/datacontainer/DataContainerCompound.java)
コンパウンドタグを表現するクラス

### [DataContainerManager](src/main/java/com/gmail/subnokoii78/util/datacontainer/DataContainerManager.java)
コンパウンドタグを管理するクラス
<br>NMSを使用せずカスタムデータの保存を行うためのクラス
<br>その他のファイルはDataContainerManagerを継承したクラス

## execute パッケージ (Execute API)

### [Execute](src/main/java/com/gmail/subnokoii78/util/execute/Execute.java)
Execute APIの心臓部
<br>executeコマンドに似た操作で処理の記述が可能になるクラス

### [IfUnless](src/main/java/com/gmail/subnokoii78/util/execute/IfUnless.java)
If/Unlessを区別するための列挙型

### [EntitySelector](src/main/java/com/gmail/subnokoii78/util/execute/EntitySelector.java)
エンティティセレクターを表現するクラス

### [SelectorArgument](src/main/java/com/gmail/subnokoii78/util/execute/SelectorArgument.java)
エンティティセレクター引数を表現するクラス

### [EntityAnchor](src/main/java/com/gmail/subnokoii78/util/execute/EntityAnchor.java)
エンティティアンカーを表現する列挙型

### [ScanMode](src/main/java/com/gmail/subnokoii78/util/execute/ScanMode.java)
all/maskedを区別する列挙型

### [ScoreHolder](src/main/java/com/gmail/subnokoii78/util/execute/ScoreHolder.java)
スコアホルダーを表現するクラス

### [VanillaDimensionProvider](src/main/java/com/gmail/subnokoii78/util/execute/VanillaDimensionProvider.java)
ディメンションの絶対的な値による指定を可能にするための列挙型

### [SelectorSortOrder](src/main/java/com/gmail/subnokoii78/util/execute/SelectorSortOrder.java)
セレクターの処理順を決定するためのクラス

### [ScoreComparator](src/main/java/com/gmail/subnokoii78/util/execute/ScoreComparator.java)
スコアの比較演算子を表現するインターフェース

### [ItemSlotsGroup](src/main/java/com/gmail/subnokoii78/util/execute/ItemSlotsGroup.java)
アイテムのパス指定を容易にするクラス

### [ScoreRange](src/main/java/com/gmail/subnokoii78/util/execute/ScoreRange.java)
整数の範囲

### [LevelRange](src/main/java/com/gmail/subnokoii78/util/execute/LevelRange.java)
正の整数の範囲

### [RotationRange](src/main/java/com/gmail/subnokoii78/util/execute/RotationRange.java)
正の浮動小数点数の範囲

### [DistanceRange](src/main/java/com/gmail/subnokoii78/util/execute/DistanceRange.java)
正の倍精度浮動小数点数の範囲

### [SourceOrigin](src/main/java/com/gmail/subnokoii78/util/execute/SourceOrigin.java)
executeコマンドの送信者を表すクラス

### [SourceStack](src/main/java/com/gmail/subnokoii78/util/execute/SourceStack.java)
Executeオブジェクト内部で操作される、コマンドの実行文脈1つ分を表現するクラス

## event パッケージ

### [CustomEvent](src/main/java/com/gmail/subnokoii78/util/event/CustomEvent.java)
カスタムイベントのインターフェース

### [CancellableCustomEvent](src/main/java/com/gmail/subnokoii78/util/event/CancellableCustomEvent.java)
キャンセル可能なカスタムイベントの抽象クラス

### [CustomEventType](src/main/java/com/gmail/subnokoii78/util/event/CustomEventType.java)
カスタムイベントの種類

### [CustomEventHandlerRegistry](src/main/java/com/gmail/subnokoii78/util/event/CustomEventHandlerRegistry.java)
カスタムイベントのリスナーを登録するためのクラス

その他のファイルはCustomEventを継承したクラス