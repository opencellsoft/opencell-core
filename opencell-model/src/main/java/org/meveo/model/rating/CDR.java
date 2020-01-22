/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.model.rating;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BaseEntity;

/**
 * Charging Data Record - CDR - information
 * 
 * @author anasseh
 * @since 9.1
 */
@Entity
@Table(name = "rating_cdr")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
		@Parameter(name = "sequence_name", value = "rating_cdr_seq"), })
public class CDR extends BaseEntity {

	private static final long serialVersionUID = 1278336601263933734L;

	/**
	 * the origin batch the CDR comes from (like a CDR file)
	 */
	@Column(name = "origin_batch", length = 255)
	@Size(max = 255)
	private String originBatch;

	/**
	 * The origin record the CDR comes from (like a CDR unique identifier)
	 */
	@Column(name = "origin_record", length = 255)
	@Size(max = 255)
	private String originRecord;

	/**
	 * Event date
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "event_date")
	private Date timestamp;

	/**
	 * Quantity
	 */
	@Column(name = "quantity")
	private BigDecimal quantity;

	/**
	 * Parameter
	 */
	@Column(name = "parameter_1", length = 255)
	@Size(max = 255)
	private String param1;

	/**
	 * Parameter
	 */
	@Column(name = "parameter_2", length = 255)
	@Size(max = 255)
	private String param2;

	/**
	 * Parameter
	 */
	@Column(name = "parameter_3", length = 255)
	@Size(max = 255)
	private String param3;

	/**
	 * Parameter
	 */
	@Column(name = "parameter_4", length = 255)
	@Size(max = 255)
	private String param4;

	/**
	 * Parameter
	 */
	@Column(name = "parameter_5", length = 255)
	@Size(max = 255)
	private String param5;

	/**
	 * Parameter
	 */
	@Column(name = "parameter_6", length = 255)
	@Size(max = 255)
	private String param6;

	/**
	 * Parameter
	 */
	@Column(name = "parameter_7", length = 255)
	@Size(max = 255)
	private String param7;

	@Column(name = "parameter_8", length = 255)
	@Size(max = 255)
	private String param8;

	/**
	 * Parameter
	 */
	@Column(name = "parameter_9", length = 255)
	@Size(max = 255)
	private String param9;

	/**
	 * Date type parameter
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "date_parameter_1")
	private Date dateParam1;

	/**
	 * Date type parameter
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "date_parameter_2")
	private Date dateParam2;

	/**
	 * Date type parameter
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "date_parameter_3")
	private Date dateParam3;

	/**
	 * Date type parameter
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "date_parameter_4")
	private Date dateParam4;

	/**
	 * Date type parameter
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "date_parameter_5")
	private Date dateParam5;

	/**
	 * Decimal type parameter
	 */
	@Column(name = "decimal_parameter_1", precision = NB_PRECISION, scale = NB_DECIMALS)
	private BigDecimal decimalParam1;

	/**
	 * Decimal type parameter
	 */
	@Column(name = "decimal_parameter_2", precision = NB_PRECISION, scale = NB_DECIMALS)
	private BigDecimal decimalParam2;

	/**
	 * Decimal type parameter
	 */
	@Column(name = "decimal_parameter_3", precision = NB_PRECISION, scale = NB_DECIMALS)
	private BigDecimal decimalParam3;

	/**
	 * Decimal type parameter
	 */
	@Column(name = "decimal_parameter_4", precision = NB_PRECISION, scale = NB_DECIMALS)
	private BigDecimal decimalParam4;

	/**
	 * Decimal type parameter
	 */
	@Column(name = "decimal_parameter_5", precision = NB_PRECISION, scale = NB_DECIMALS)
	private BigDecimal decimalParam5;

	/**
	 * Record creation timestamp
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created")
	private Date created = new Date();

	/**
	 * Last update timestamp
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_updated")
	private Date updated;

	/**
	 * Access code
	 */
	@Column(name = "access_code", length = 255)
	@Size(max = 255)
	private String access_id;

	/**
	 * the main Edr created by mediation from CDR
	 */
	@JoinColumn(name = "header_edr_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private EDR headerEDR;

	/**
	 * Parameter
	 */
	@Column(name = "EXTRA_PARAMETER", columnDefinition = "TEXT")
	private String extraParam;

	/**
	 * Processing status
	 */
	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private CDRStatusEnum status = CDRStatusEnum.OPEN;

	/**
	 * Last status date
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "status_date")
	private Date statusDate;

	/**
	 * Rejection reason
	 */
	@Column(name = "reject_reason", columnDefinition = "text")
	private String rejectReason;

	@Transient
	private String line;

	@Transient
	private Exception rejectReasonException = null;

	public String getLine() {
		return line;
	}

	public void setLine(String line) {
		this.line = line;
	}

	public Exception getRejectReasonException() {
		return rejectReasonException;
	}

	public void setRejectReasonException(Exception rejectReasonException) {
		this.rejectReasonException = rejectReasonException;
	}

	public String getOriginBatch() {
		return originBatch;
	}

	public void setOriginBatch(String originBatch) {
		this.originBatch = originBatch;
	}

	public String getOriginRecord() {
		return originRecord;
	}

	public void setOriginRecord(String originRecord) {
		this.originRecord = originRecord;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date eventDate) {
		this.timestamp = eventDate;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public String getParam1() {
		return param1;
	}

	public void setParam1(String param1) {
		this.param1 = param1;
	}

	public String getParam2() {
		return param2;
	}

	public void setParam2(String param2) {
		this.param2 = param2;
	}

	public String getParam3() {
		return param3;
	}

	public void setParam3(String param3) {
		this.param3 = param3;
	}

	public String getParam4() {
		return param4;
	}

	public void setParam4(String param4) {
		this.param4 = param4;
	}

	public String getParam5() {
		return param5;
	}

	public void setParam5(String param5) {
		this.param5 = param5;
	}

	public String getParam6() {
		return param6;
	}

	public void setParam6(String param6) {
		this.param6 = param6;
	}

	public String getParam7() {
		return param7;
	}

	public void setParam7(String param7) {
		this.param7 = param7;
	}

	public String getParam8() {
		return param8;
	}

	public void setParam8(String param8) {
		this.param8 = param8;
	}

	public String getParam9() {
		return param9;
	}

	public void setParam9(String param9) {
		this.param9 = param9;
	}

	public Date getStatusDate() {
		return statusDate;
	}

	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	/**
	 * @return Last status change date
	 */
	public Date getUpdated() {
		return updated;
	}

	/**
	 * @param updated Last status change date
	 */
	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public Date getDateParam1() {
		return dateParam1;
	}

	public void setDateParam1(Date dateParam1) {
		this.dateParam1 = dateParam1;
	}

	public Date getDateParam2() {
		return dateParam2;
	}

	public void setDateParam2(Date dateParam2) {
		this.dateParam2 = dateParam2;
	}

	public Date getDateParam3() {
		return dateParam3;
	}

	public void setDateParam3(Date dateParam3) {
		this.dateParam3 = dateParam3;
	}

	public Date getDateParam4() {
		return dateParam4;
	}

	public void setDateParam4(Date dateParam4) {
		this.dateParam4 = dateParam4;
	}

	public Date getDateParam5() {
		return dateParam5;
	}

	public void setDateParam5(Date dateParam5) {
		this.dateParam5 = dateParam5;
	}

	public BigDecimal getDecimalParam1() {
		return decimalParam1;
	}

	public void setDecimalParam1(BigDecimal decimalParam1) {
		this.decimalParam1 = decimalParam1;
	}

	public BigDecimal getDecimalParam2() {
		return decimalParam2;
	}

	public void setDecimalParam2(BigDecimal decimalParam2) {
		this.decimalParam2 = decimalParam2;
	}

	public BigDecimal getDecimalParam3() {
		return decimalParam3;
	}

	public void setDecimalParam3(BigDecimal decimalParam3) {
		this.decimalParam3 = decimalParam3;
	}

	public BigDecimal getDecimalParam4() {
		return decimalParam4;
	}

	public void setDecimalParam4(BigDecimal decimalParam4) {
		this.decimalParam4 = decimalParam4;
	}

	public BigDecimal getDecimalParam5() {
		return decimalParam5;
	}

	public void setDecimalParam5(BigDecimal decimalParam5) {
		this.decimalParam5 = decimalParam5;
	}

	public String getAccess_id() {
		return access_id;
	}

	public void setAccess_id(String accessCode) {
		this.access_id = accessCode;
	}

	public EDR getHeaderEDR() {
		return headerEDR;
	}

	public void setHeaderEDR(EDR headerEDR) {
		this.headerEDR = headerEDR;
	}

	public String getExtraParam() {
		return extraParam;
	}

	public void setExtraParameter(String extraParameter) {
		this.extraParam = extraParameter;
	}

	@Override
	public String toString() {
		return "CDR [id=" + id + ", originBatch=" + originBatch + ", originRecord=" + originRecord + ", timestamp=" + timestamp + ", quantity=" + quantity + ", access="
				+ access_id + ", parameter1=" + param1 + ", parameter2=" + param2 + ", parameter3=" + param3 + ", parameter4=" + param4 + ", parameter5=" + param5
				+ ", parameter6=" + param6 + ", parameter7=" + param7 + ", parameter8=" + param8 + ", parameter9=" + param9 + ", dateParam1=" + dateParam1 + ", dateParam2="
				+ dateParam2 + ", dateParam3=" + dateParam3 + ", dateParam4=" + dateParam4 + ", dateParam5=" + dateParam5 + ", decimalParam1=" + decimalParam1 + ", dateParam2="
				+ dateParam2 + ", decimalParam3=" + decimalParam3 + ", dateParam4=" + dateParam4 + ", decimalParam5=" + decimalParam5 + ", extraParam=" + extraParam
				+ ", headerEDR=" + ((headerEDR == null) ? "null" : headerEDR.getId()) + ", created=" + created + ", lastUpdate=" + updated + "]";
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		} else if (obj == null) {
			return false;
		} else if (!(obj instanceof CDR)) {
			return false;
		}

		CDR other = (CDR) obj;

		if (id != null && other.getId() != null && id.equals(other.getId())) {
			return true;
		}

		return this.toString().equals(other.toString());
	}

	/**
	 * @return Processing status
	 */
	public CDRStatusEnum getStatus() {
		return status;
	}

	/**
	 * @param status Processing status
	 */
	public void setStatus(CDRStatusEnum status) {
		this.status = status;
		this.statusDate = new Date();
	}
	
	/**
	 * @return Rejection reason
	 */
	public String getRejectReason() {
		return rejectReason;
	}

	/**
	 * @param rejectReason Rejection reason
	 */
	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}

	/**
	 * Convert to a CSV-like line with ";" as field separator
	 * 
	 * @return CSV-line line string
	 */
	public String toCsv() {
		return getCsvValue(timestamp) + ";" + getCsvValue(quantity) + ";" + getCsvValue(access_id) + ";" + getCsvValue(param1) + ";" + getCsvValue(param2) + ";"
				+ getCsvValue(param3) + ";" + getCsvValue(param4) + ";" + getCsvValue(param5) + ";" + getCsvValue(param6) + ";" + getCsvValue(param7) + ";" + getCsvValue(param8)
				+ ";" + getCsvValue(param9) + ";" + getCsvValue(dateParam1) + ";" + getCsvValue(dateParam2) + ";" + getCsvValue(dateParam3) + ";" + getCsvValue(dateParam4) + ";"
				+ getCsvValue(dateParam5) + ";" + getCsvValue(decimalParam1) + ";" + getCsvValue(decimalParam2) + ";" + getCsvValue(decimalParam3) + ";"
				+ getCsvValue(decimalParam4) + ";" + getCsvValue(decimalParam5) + ";" + getCsvValue(extraParam);
	}

	private String getCsvValue(Object o) {
		String result = "";
		if (o == null) {
			result = "";
		} else if (o instanceof Date) {
			result = sdf.format((Date) o);
		} else {
			result = o.toString();
		}
		return result;
	}
}