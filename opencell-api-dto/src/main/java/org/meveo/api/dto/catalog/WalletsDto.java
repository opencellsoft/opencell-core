package org.meveo.api.dto.catalog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * The Class WalletsDto.
 *
 * @author Edward P. Legaspi
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class WalletsDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 2202950461799059524L;

    /** The wallet. */
    private List<String> wallet = new ArrayList<String>();

    /**
     * Gets the wallet.
     *
     * @return the wallet
     */
    public List<String> getWallet() {
        return wallet;
    }

    /**
     * Sets the wallet.
     *
     * @param wallet the new wallet
     */
    public void setWallet(List<String> wallet) {
        this.wallet = wallet;
    }


    @Override
    public String toString() {
        return "WalletsDto [wallet=" + wallet + "]";
    }
}