package org.meveo.model.worldline.sips;

public class BaseRequest {
    private String merchantId;
    private String interfaceVersion;
    private String seal;
    private String keyVersion;
    private String sealAlgorithm;

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getInterfaceVersion() {
        return interfaceVersion;
    }

    public void setInterfaceVersion(String interfaceVersion) {
        this.interfaceVersion = interfaceVersion;
    }

    public String getSeal() {
        return seal;
    }

    public void setSeal(String seal) {
        this.seal = seal;
    }

    public String getKeyVersion() {
        return keyVersion;
    }

    public void setKeyVersion(String keyVersion) {
        this.keyVersion = keyVersion;
    }

    public String getSealAlgorithm() {
        return sealAlgorithm;
    }

    public void setSealAlgorithm(String sealAlgorithm) {
        this.sealAlgorithm = sealAlgorithm;
    }
}