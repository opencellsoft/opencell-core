package org.meveo.api.account;

/**
 * @author Edward P. Legaspi
 **/
public enum AccountHierarchyTypeEnum {

	// UA=0, BA=1, CA=2, C=3, S=4

	S(4, 4), 
	S_C(3, 4),    
	C(3, 3),
	S_CA(2, 4),
	C_CA(2, 3),
	CA(2, 2),
	S_BA(1, 4),  
	C_BA(1, 3),
	CA_BA(1, 2),
	BA(1, 1),
	S_UA(0, 4), 
	C_UA(0, 3),
	CA_UA(0, 2),
	BA_UA(0, 1),
	UA(0, 0);

	private int lowLevel;
	private int highLevel;

	private AccountHierarchyTypeEnum(int lowLevel, int highLevel) {
		this.lowLevel = lowLevel;
		this.highLevel = highLevel;
	}

	public int getLowLevel() {
		return lowLevel;
	}

	public void setLowLevel(int lowLevel) {
		this.lowLevel = lowLevel;
	}

	public int getHighLevel() {
		return highLevel;
	}

	public void setHighLevel(int highLevel) {
		this.highLevel = highLevel;
	}

}
