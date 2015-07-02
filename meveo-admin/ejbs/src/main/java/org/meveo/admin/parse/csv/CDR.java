package org.meveo.admin.parse.csv;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.meveo.model.IProvider;
import org.meveo.model.crm.Provider;

public class CDR  implements Serializable,IProvider{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Date timestamp;
	public String access_id;
	public BigDecimal quantity;
	public String param1;
	public String param2;
	public String param3;
	public String param4;
	private String param5;
	private String param6;
	private String param7;
	private String param8;
	private String param9;
	private long dateParam1;
	private long dateParam2;
	private long dateParam3; 
	private long dateParam4; 
	private long dateParam5; 
	private BigDecimal decimalParam1; 
	private BigDecimal decimalParam2; 
	private BigDecimal decimalParam3; 
	private BigDecimal decimalParam4; 
	private BigDecimal decimalParam5;
	private Provider provider;
	
	public CDR(){
		
	}

	/**
	 * @return the timestamp
	 */
	public Date getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @return the access_id
	 */
	public String getAccess_id() {
		return access_id;
	}

	/**
	 * @param access_id the access_id to set
	 */
	public void setAccess_id(String access_id) {
		this.access_id = access_id;
	}

	/**
	 * @return the quantity
	 */
	public BigDecimal getQuantity() {
		return quantity;
	}

	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	/**
	 * @return the param1
	 */
	public String getParam1() {
		return param1;
	}

	/**
	 * @param param1 the param1 to set
	 */
	public void setParam1(String param1) {
		this.param1 = param1;
	}

	/**
	 * @return the param2
	 */
	public String getParam2() {
		return param2;
	}

	/**
	 * @param param2 the param2 to set
	 */
	public void setParam2(String param2) {
		this.param2 = param2;
	}

	/**
	 * @return the param3
	 */
	public String getParam3() {
		return param3;
	}

	/**
	 * @param param3 the param3 to set
	 */
	public void setParam3(String param3) {
		this.param3 = param3;
	}

	/**
	 * @return the param4
	 */
	public String getParam4() {
		return param4;
	}

	/**
	 * @param param4 the param4 to set
	 */
	public void setParam4(String param4) {
		this.param4 = param4;
	}

	/**
	 * @return the param5
	 */
	public String getParam5() {
		return param5;
	}

	/**
	 * @param param5 the param5 to set
	 */
	public void setParam5(String param5) {
		this.param5 = param5;
	}

	/**
	 * @return the param6
	 */
	public String getParam6() {
		return param6;
	}

	/**
	 * @param param6 the param6 to set
	 */
	public void setParam6(String param6) {
		this.param6 = param6;
	}

	/**
	 * @return the param7
	 */
	public String getParam7() {
		return param7;
	}

	/**
	 * @param param7 the param7 to set
	 */
	public void setParam7(String param7) {
		this.param7 = param7;
	}

	/**
	 * @return the param8
	 */
	public String getParam8() {
		return param8;
	}

	/**
	 * @param param8 the param8 to set
	 */
	public void setParam8(String param8) {
		this.param8 = param8;
	}

	/**
	 * @return the param9
	 */
	public String getParam9() {
		return param9;
	}

	/**
	 * @param param9 the param9 to set
	 */
	public void setParam9(String param9) {
		this.param9 = param9;
	}

	/**
	 * @return the dateParam1
	 */
	public long getDateParam1() {
		return dateParam1;
	}

	/**
	 * @param dateParam1 the dateParam1 to set
	 */
	public void setDateParam1(long dateParam1) {
		this.dateParam1 = dateParam1;
	}

	/**
	 * @return the dateParam2
	 */
	public long getDateParam2() {
		return dateParam2;
	}

	/**
	 * @param dateParam2 the dateParam2 to set
	 */
	public void setDateParam2(long dateParam2) {
		this.dateParam2 = dateParam2;
	}

	/**
	 * @return the dateParam3
	 */
	public long getDateParam3() {
		return dateParam3;
	}

	/**
	 * @param dateParam3 the dateParam3 to set
	 */
	public void setDateParam3(long dateParam3) {
		this.dateParam3 = dateParam3;
	}

	/**
	 * @return the dateParam4
	 */
	public long getDateParam4() {
		return dateParam4;
	}

	/**
	 * @param dateParam4 the dateParam4 to set
	 */
	public void setDateParam4(long dateParam4) {
		this.dateParam4 = dateParam4;
	}

	/**
	 * @return the dateParam5
	 */
	public long getDateParam5() {
		return dateParam5;
	}

	/**
	 * @param dateParam5 the dateParam5 to set
	 */
	public void setDateParam5(long dateParam5) {
		this.dateParam5 = dateParam5;
	}

	/**
	 * @return the decimalParam1
	 */
	public BigDecimal getDecimalParam1() {
		return decimalParam1;
	}

	/**
	 * @param decimalParam1 the decimalParam1 to set
	 */
	public void setDecimalParam1(BigDecimal decimalParam1) {
		this.decimalParam1 = decimalParam1;
	}

	/**
	 * @return the decimalParam2
	 */
	public BigDecimal getDecimalParam2() {
		return decimalParam2;
	}

	/**
	 * @param decimalParam2 the decimalParam2 to set
	 */
	public void setDecimalParam2(BigDecimal decimalParam2) {
		this.decimalParam2 = decimalParam2;
	}

	/**
	 * @return the decimalParam3
	 */
	public BigDecimal getDecimalParam3() {
		return decimalParam3;
	}

	/**
	 * @param decimalParam3 the decimalParam3 to set
	 */
	public void setDecimalParam3(BigDecimal decimalParam3) {
		this.decimalParam3 = decimalParam3;
	}

	/**
	 * @return the decimalParam4
	 */
	public BigDecimal getDecimalParam4() {
		return decimalParam4;
	}

	/**
	 * @param decimalParam4 the decimalParam4 to set
	 */
	public void setDecimalParam4(BigDecimal decimalParam4) {
		this.decimalParam4 = decimalParam4;
	}

	/**
	 * @return the decimalParam5
	 */
	public BigDecimal getDecimalParam5() {
		return decimalParam5;
	}

	/**
	 * @param decimalParam5 the decimalParam5 to set
	 */
	public void setDecimalParam5(BigDecimal decimalParam5) {
		this.decimalParam5 = decimalParam5;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CDR [timestamp=" + timestamp + ", access_id=" + access_id + ", quantity=" + quantity + ", param1=" + param1 + ", param2=" + param2 + ", param3=" + param3 + ", param4=" + param4 + ", param5=" + param5 + ", param6=" + param6 + ", param7=" + param7 + ", param8=" + param8 + ", param9=" + param9 + ", dateParam1=" + dateParam1 + ", dateParam2=" + dateParam2 + ", dateParam3=" + dateParam3 + ", dateParam4=" + dateParam4 + ", dateParam5=" + dateParam5
				+ ", decimalParam1=" + decimalParam1 + ", decimalParam2=" + decimalParam2 + ", decimalParam3=" + decimalParam3 + ", decimalParam4=" + decimalParam4 + ", decimalParam5=" + decimalParam5 + "]";
	}

	@Override
	public Provider getProvider() {
		return provider;
	}

	@Override
	public void setProvider(Provider provider) {
		this.provider=provider;
		
	}

}
