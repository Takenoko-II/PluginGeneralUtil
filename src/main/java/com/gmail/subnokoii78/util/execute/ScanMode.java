package com.gmail.subnokoii78.util.execute;

import org.jetbrains.annotations.NotNull;

/**
 * {@link Execute.GuardSubCommandIfUnless#blocks(String, String, String, ScanMode)}の引数に用いられる、範囲内のブロックの比較に際してのオプション
 */
public enum ScanMode {
    /**
     * 比較の際に条件として範囲内のブロックの完全一致を要求するオプション
     */
    ALL("all"),

    /**
     * 比較の際に条件として比較元範囲における空気以外の部分のブロックの完全一致を要求するオプション
     */
    MASKED("masked");

    private final String id;

    ScanMode(@NotNull String id) {
        this.id = id;
    }

    /**
     * IDを取得します。
     * @return ID
     */
    public String getId() {
        return id;
    }
}
