package com.gmail.subnokoii78.util.execute;

public enum IfUnless {
    /**
     * ガードサブコマンドの条件をテストの成功にするオプション
     */
    IF(true),

    /**
     * ガードサブコマンドの条件をテストの失敗にするオプション
     */
    UNLESS(false);

    private final boolean bool;

    IfUnless(boolean bool) {
        this.bool = bool;
    }

    /**
     * unlessによる条件の反転を適用し、ifならばそのまま返します。
     * @param condition 条件
     * @return ifまたはunlessが適用された条件
     */
    public boolean apply(boolean condition) {
        if (bool) return condition;
        else return !condition;
    }
}
