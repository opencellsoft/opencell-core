package org.meveo.api.dto.catalog;

import java.io.Serializable;

/**
 * The Class BaseServiceChargeTemplateDto.
 *
 * @author Edward P. Legaspi
 */
public abstract class BaseServiceChargeTemplateDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 3771281513359110575L;

    /** The code. */
    private String code;

    /** The wallets. */
    private WalletsDto wallets;

    /**
     * Gets the code.
     *
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the code.
     *
     * @param code the new code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Gets the wallets.
     *
     * @return the wallets
     */
    public WalletsDto getWallets() {
        if (wallets == null)
            wallets = new WalletsDto();
        return wallets;
    }

    /**
     * Sets the wallets.
     *
     * @param wallets the new wallets
     */
    public void setWallets(WalletsDto wallets) {
        this.wallets = wallets;
    }
    
    @Override
    public String toString() {
        return "BaseServiceChargeTemplateDto [code=" + code + ", wallets=" + wallets + "]";
    }   
}