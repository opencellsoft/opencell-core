package org.meveo.model.worldline.sips.wallet;

public enum WalletAction {
    ADDPM,
    UPDATEPM,
    DELETEPM,
    SIGNOFF;

    public static WalletAction get(String value) {
        for (WalletAction wa : values()) {
            if (wa.name().equalsIgnoreCase(value)) {
                return wa;
            }
        }

        return null;
    }
}