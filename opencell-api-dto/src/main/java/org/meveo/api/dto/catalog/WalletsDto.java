package org.meveo.api.dto.catalog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Edward P. Legaspi
 **/
@XmlType(name = "Wallets")
@XmlAccessorType(XmlAccessType.FIELD)
public class WalletsDto implements Serializable {

	private static final long serialVersionUID = 2202950461799059524L;

	private List<String> wallet = new ArrayList<String>();

	public List<String> getWallet() {
		return wallet;
	}

	public void setWallet(List<String> wallet) {
		this.wallet = wallet;
	}

	@Override
	public String toString() {
		return "WalletsDto [wallet=" + wallet + "]";
	}

}
