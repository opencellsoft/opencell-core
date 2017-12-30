package org.meveo.model.crm;

import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.payments.CustomerAccount;

/**
 * @author Edward P. Legaspi
 **/
public enum AccountHierarchyTypeEnum {

	// UA=0, BA=1, CA=2, C=3, S=4

	/**
	 * Seller only
	 */
	S(4, 4),

	/**
	 * Seller and Customer
	 */
	S_C(3, 4),

	/**
	 * Customer only
	 */
	C(3, 3),

	/**
	 * Seller, Customer and Customer account
	 */
	S_CA(2, 4),

	/**
	 * Customer and customer account
	 */
	C_CA(2, 3),

	/**
	 * Customer account only
	 */
	CA(2, 2),

	/**
	 * Seller, Customer, Customer account and Billing account
	 */
	S_BA(1, 4),

	/**
	 * Customer, Customer account and Billing account
	 */
	C_BA(1, 3),

	/**
	 * Customer account and Billing account
	 */
	CA_BA(1, 2),

	/**
	 * Billing account
	 */
	BA(1, 1),

	/**
	 * Seller, Customer, Customer account, Billing account and User account
	 */
	S_UA(0, 4),

	/**
	 * Customer, Customer account, Billing account and User account
	 */
	C_UA(0, 3),

	/**
	 * Customer account, Billing account and User account
	 */
	CA_UA(0, 2),

	/**
	 * Billing account and User account
	 */
	BA_UA(0, 1),

	/**
	 * User account
	 */
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

	/**
	 * @return label
	 */
	public String getLabel() {
		return "enum.AccountHierarchyTypeEnum." + name();
	}

	/**
	 * This will return the class that corresponds to the top entity of the
	 * hierarchy.
	 * 
	 * @return top class.
	 */
	public Class<? extends BusinessEntity> topClass() {
		switch (highLevel) {
		case 0:
			return UserAccount.class;
		case 1:
			return BillingAccount.class;
		case 2:
			return CustomerAccount.class;
		case 3:
			return Customer.class;
		case 4:
			return Seller.class;
		default:
			return null;
		}
	}

	/**
	 * This will return the class that corresponds to the parent of the top
	 * entity.
	 * 
	 * @return top class
	 */
	public Class<? extends BusinessEntity> parentClass() {
		switch (highLevel) {
		case 0:
			return BillingAccount.class;
		case 1:
			return CustomerAccount.class;
		case 2:
			return Customer.class;
		case 3:
			return Seller.class;
		case 4:
			return Seller.class;
		default:
			return null;
		}
	}

}