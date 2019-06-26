package org.meveo.api.dto.catalog;

import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

@XmlType(name = "baseServiceChargeTemplateDto", propOrder = {
        "code",
        "wallets",
        "counterTemplate"
})

/**
 * The Class BaseServiceChargeTemplateDto.
 *
 * @author Edward P. Legaspi
 * @author Abdellatif BARI
 * @lastModifiedVersion 5.3
 */
public abstract class BaseServiceChargeTemplateDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 3771281513359110575L;

    /** The code. */
    private String code;

    /** The wallets. */
    private WalletsDto wallets;
    
    /** The counter template. */
    private String counterTemplate;

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
    
    /**
     * Gets the counter template.
     *
     * @return the counter template
     */
    public String getCounterTemplate() {
        return counterTemplate;
    }

    /**
     * Sets the counter template.
     *
     * @param counterTemplate the new counter template
     */
    public void setCounterTemplate(String counterTemplate) {
        this.counterTemplate = counterTemplate;
    }

    
    @Override
    public String toString() {
        return "BaseServiceChargeTemplateDto [code=" + code + ", wallets=" + wallets + ", counterTemplate=" + counterTemplate + "]";
    }   
}