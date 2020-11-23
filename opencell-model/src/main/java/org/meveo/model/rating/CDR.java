/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BaseEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.crm.custom.CustomFieldValues;

/**
 * Charging Data Record - CDR - information
 * 
 * @author anasseh
 * @author Mohammed Amine TAZI
 * @since 9.1
 */
@Entity
@Table(name = "rating_cdr")
@CustomFieldEntity(cftCodePrefix = "CDR")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
		@Parameter(name = "sequence_name", value = "rating_cdr_seq") })
@NamedQueries({
    @NamedQuery(name = "CDR.checkFileNameExists", query = "SELECT originBatch FROM CDR where originBatch=:fileName"),
    @NamedQuery(name = "CDR.checkRTBilledExists", query = "from RatedTransaction rt where Status = 'BILLED' and rt.edr.originBatch=:fileName"),
    @NamedQuery(name = "CDR.deleteRTs", query = "delete from RatedTransaction rt where Status <> 'BILLED' and rt.edr in (select e from EDR e where e.originBatch=:fileName)"),
    @NamedQuery(name = "CDR.deleteWOs", query = "delete from WalletOperation wo where wo.edr in (select e from EDR e where e.originBatch=:fileName)"),
    @NamedQuery(name = "CDR.deleteEDRs", query = "delete from EDR where originBatch=:fileName"),
    @NamedQuery(name = "CDR.deleteCDRs", query = "delete from CDR where originBatch=:fileName"),
    @NamedQuery(name="CDR.listCDRsToReprocess", query = "from CDR where Status = 'TO_REPROCESS'"),
    @NamedQuery(name="CDR.cleanReprocessedCDR", query = "delete from CDR where Status = 'TO_REPROCESS' and originRecord =:originRecord"),
    @NamedQuery(name="CDR.updateReprocessedCDR", query = "update CDR set timesTried=:timesTried, status=:status where Status = 'TO_REPROCESS' and originRecord =:originRecord")

    
})
public class CDR extends BaseEntity implements ICustomFieldEntity {

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
	private Date eventDate;

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
	private String  parameter1;

	/**
	 * Parameter
	 */
	@Column(name = "parameter_2", length = 255)
	@Size(max = 255)
	private String parameter2;

	/**
	 * Parameter
	 */
	@Column(name = "parameter_3", length = 255)
	@Size(max = 255)
	private String parameter3;

	/**
	 * Parameter
	 */
	@Column(name = "parameter_4", length = 255)
	@Size(max = 255)
	private String parameter4;

	/**
	 * Parameter
	 */
	@Column(name = "parameter_5", length = 255)
	@Size(max = 255)
	private String parameter5;

	/**
	 * Parameter
	 */
	@Column(name = "parameter_6", length = 255)
	@Size(max = 255)
	private String parameter6;

	/**
	 * Parameter
	 */
	@Column(name = "parameter_7", length = 255)
	@Size(max = 255)
	private String parameter7;

	@Column(name = "parameter_8", length = 255)
	@Size(max = 255)
	private String parameter8;

	/**
	 * Parameter
	 */
	@Column(name = "parameter_9", length = 255)
	@Size(max = 255)
	private String parameter9;

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
	@Column(name = "decimal_parameter_1")
	private BigDecimal decimalParam1;

	/**
	 * Decimal type parameter
	 */
	@Column(name = "decimal_parameter_2")
	private BigDecimal decimalParam2;

	/**
	 * Decimal type parameter
	 */
	@Column(name = "decimal_parameter_3")
	private BigDecimal decimalParam3;

	/**
	 * Decimal type parameter
	 */
	@Column(name = "decimal_parameter_4")
	private BigDecimal decimalParam4;

	/**
	 * Decimal type parameter
	 */
	@Column(name = "decimal_parameter_5")
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
	private String accessCode;

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
	
	/** The times tried. */
	@Column(name = "times_tried")
	private Integer timesTried;
	
	/** The type. */
	@Column(name = "type", length = 255)
    @Size(max = 255)
    private String type;

    @Column(name = "line",  length = 2000)
    @Size(max = 2000)
	private String line;

    @Column(name = "updater",  length = 100)
    @Size(max = 100)
    private String updater;
    
    /** The serialized CDR dto. to be used while re-processing the CDR */
    @Column(name = "source", nullable = true, columnDefinition = "TEXT")
    private String source;
    

	@Transient
	private Exception rejectReasonException = null;

	@Transient
    private CustomFieldValues cfValues;

	@Transient
    private CustomFieldValues cfAccumulatedValues;

	@Transient
    private String uuid;

	public String getLine() {
		return line;
	}

	public void setLine(String line) {
		this.line = line;
	}

	public String getUpdater() {
        return updater;
    }
	
	public void setUpdater(String updater) {
        this.updater = updater;
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

	public Date getEventDate() {
		return eventDate;
	}

	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public String getParameter1() {
		return parameter1;
	}

	public void setParameter1(String param1) {
		this.parameter1 = param1;
	}

	public String getParameter2() {
		return parameter2;
	}

	public void setParameter2(String param2) {
		this.parameter2 = param2;
	}

	public String getParameter3() {
		return parameter3;
	}

	public void setParameter3(String param3) {
		this.parameter3 = param3;
	}

	public String getParameter4() {
		return parameter4;
	}

	public void setParameter4(String param4) {
		this.parameter4 = param4;
	}

	public String getParameter5() {
		return parameter5;
	}

	public void setParameter5(String param5) {
		this.parameter5 = param5;
	}

	public String getParameter6() {
		return parameter6;
	}

	public void setParameter6(String param6) {
		this.parameter6 = param6;
	}

	public String getParameter7() {
		return parameter7;
	}

	public void setParameter7(String param7) {
		this.parameter7 = param7;
	}

	public String getParameter8() {
		return parameter8;
	}

	public void setParameter8(String param8) {
		this.parameter8 = param8;
	}

	public String getParameter9() {
		return parameter9;
	}

	public void setParameter9(String param9) {
		this.parameter9 = param9;
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

	public String getAccessCode() {
		return accessCode;
	}

	public void setAccessCode(String accessCode) {
		this.accessCode = accessCode;
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
		return "CDR [id=" + id + ", originBatch=" + originBatch + ", originRecord=" + originRecord + ", timestamp=" + eventDate + ", quantity=" + quantity + ", access="
				+ accessCode + ", parameter1=" + parameter1 + ", parameter2=" + parameter2 + ", parameter3=" + parameter3 + ", parameter4=" + parameter4 + ", parameter5=" + parameter5
				+ ", parameter6=" + parameter6 + ", parameter7=" + parameter7 + ", parameter8=" + parameter8 + ", parameter9=" + parameter9 + ", dateParam1=" + dateParam1 + ", dateParam2="
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
	 * Gets the times tried.
	 *
	 * @return the times tried
	 */
	public Integer getTimesTried() {
        return timesTried;
    }

    /**
     * Sets the times tried.
     *
     * @param timesTried the new times tried
     */
    public void setTimesTried(Integer timesTried) {
        this.timesTried = timesTried;
    }
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
       
    

    /**
     * Gets the source.
     *
     * @return the source
     */
    public String getSource() {
        return source;
    }

    /**
     * Sets the source.
     *
     * @param source the new source
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
	 * Convert to a CSV-like line with ";" as field separator
	 * 
	 * @return CSV-line line string
	 */
	public String toCsv() {
		return getCsvValue(eventDate) + ";" + getCsvValue(quantity) + ";" + getCsvValue(accessCode) + ";" + getCsvValue(parameter1) + ";" + getCsvValue(parameter2) + ";"
				+ getCsvValue(parameter3) + ";" + getCsvValue(parameter4) + ";" + getCsvValue(parameter5) + ";" + getCsvValue(parameter6) + ";" + getCsvValue(parameter7) + ";" + getCsvValue(parameter8)
				+ ";" + getCsvValue(parameter9) + ";" + getCsvValue(dateParam1) + ";" + getCsvValue(dateParam2) + ";" + getCsvValue(dateParam3) + ";" + getCsvValue(dateParam4) + ";"
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

    @Override
    public String getUuid() {
        return null;
    }

    @Override
    public String clearUuid() {
        return null;
    }

    @Override
    public ICustomFieldEntity[] getParentCFEntities() {
        return null;
    }

    @Override
    public CustomFieldValues getCfValues() {
        return cfValues;
    }

    @Override
    public void setCfValues(CustomFieldValues cfValues) {
        this.cfValues = cfValues;
    }

    @Override
    public CustomFieldValues getCfAccumulatedValues() {
        return cfAccumulatedValues;
    }

    @Override
    public void setCfAccumulatedValues(CustomFieldValues cfAccumulatedValues) {
        this.cfAccumulatedValues = cfAccumulatedValues;
    }
}