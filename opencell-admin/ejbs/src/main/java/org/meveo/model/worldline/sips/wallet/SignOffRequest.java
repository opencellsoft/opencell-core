package org.meveo.model.worldline.sips.wallet;

import org.meveo.model.worldline.sips.BaseRequest;

public class SignOffRequest extends BaseRequest {
    private String merchantWalletId;

    public String getMerchantWalletId() {
        return merchantWalletId;
    }

    public void setMerchantWalletId(String merchantWalletId) {
        this.merchantWalletId = merchantWalletId;
    }
}