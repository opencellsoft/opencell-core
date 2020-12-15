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
package org.meveo.model.catalog;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BaseEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.EnableBusinessCFEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ModuleItem;
import org.meveo.model.ObservableEntity;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.OperationTypeEnum;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.enums.RecurrenceDurationEnum;
import org.meveo.model.finance.RevenueRecognitionRule;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.model.tax.TaxClass;

/**
 * Charge template/definition
 * 
 * @author Andrius Karpavicius
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Entity
@ModuleItem
@ObservableEntity
@Cacheable
@CustomFieldEntity(cftCodePrefix = "ChargeTemplate")
@ExportIdentifier({ "code" })
@Table(name = "cat_charge_template", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "cat_charge_template_seq"), })
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "charge_type", discriminatorType = DiscriminatorType.STRING)
public abstract class ChargeTemplate extends EnableBusinessCFEntity {

    private static final long serialVersionUID = -6619927605555822610L;
    
    public enum ChargeTypeEnum {
        RECURRING, USAGE, SUBSCRIPTION, TERMINATION
    }

    /**
     * Main charge types
     */
    public enum ChargeMainTypeEnum {
        /**
         * Recurring charges
         */
        RECURRING,

        /**
         * One shot charges
         */
        ONESHOT,

        /**
         * Usage charges
         */
        USAGE,

        /**
         * Product charges
         */
        PRODUCT;
    }

    /**
     * Operation type - Credit/Debit
     */
    @Column(name = "credit_debit_flag")
    protected OperationTypeEnum type;

    /**
     * Is amount editable
     */
    @Type(type = "numeric_boolean")
    @Column(name = "amount_editable")
    protected Boolean amountEditable;

    /**
     * Corresponding invoice subcategory
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_sub_category", nullable = false)
    @NotNull
    protected InvoiceSubCategory invoiceSubCategory;

    /**
     * EDR templates charge may trigger
     */
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "cat_chrg_edr", joinColumns = @JoinColumn(name = "charge_tmpl_id"), inverseJoinColumns = @JoinColumn(name = "trigg_edr_id"))
    protected List<TriggeredEDRTemplate> edrTemplates = new ArrayList<TriggeredEDRTemplate>();

    /**
     * Input unit description
     */
    @Column(name = "input_unit_description", length = 20)
    @Size(max = 20)
    protected String inputUnitDescription;

    /**
     * Rating unit description
     */
    @Column(name = "rating_unit_description", length = 20)
    @Size(max = 20)
    protected String ratingUnitDescription;

    /**
     * input_unit_unitOfMeasure
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "input_unitofmeasure")
    private UnitOfMeasure inputUnitOfMeasure;

    /**
     * rating_unit_unitOfMeasure
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rating_unitofmeasure")
    private UnitOfMeasure ratingUnitOfMeasure;

    /**
     * Expression to calculate input unitOfMeasure
     */
    @Column(name = "input_unit_el", columnDefinition = "TEXT")
    @Size(max = 2000)
    private String inputUnitEL;

    /**
     * Expression to calculate input unitOfMeasure
     */
    @Column(name = "output_unit_el", columnDefinition = "TEXT")
    @Size(max = 2000)
    private String outputUnitEL;

    /**
     * Unit multiplicator between input and rating unit
     */
    @Column(name = "unit_multiplicator", precision = BaseEntity.NB_PRECISION, scale = BaseEntity.NB_DECIMALS)
    private BigDecimal unitMultiplicator;

    /**
     * EDR and WO quantity field value precision
     */
    @Column(name = "unit_nb_decimal")
    protected int unitNbDecimal = BaseEntity.NB_DECIMALS;

    /**
     * EDR and WO quantity field value rounding
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "rounding_mode")
    protected RoundingModeEnum roundingMode = RoundingModeEnum.NEAREST;

    /**
     * Revenue recognition rule
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "revenue_recog_rule_id")
    protected RevenueRecognitionRule revenueRecognitionRule;

    /**
     * Translated descriptions in JSON format with language code as a key and translated description as a value.
     */
    @Type(type = "json")
    @Column(name = "description_i18n", columnDefinition = "text")
    protected Map<String, String> descriptionI18n;

    /**
     * Expression to determine if charge applies
     */
    @Column(name = "filter_expression", length = 2000)
    @Size(max = 2000)
    protected String filterExpression = null;

    /**
     * Expression to determine if charge matches - for Spark
     */
    @Column(name = "filter_el_sp", length = 2000)
    @Size(max = 2000)
    private String filterExpressionSpark = null;

    /**
     * Charge tax class
     **/
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tax_class_id", nullable = false)
    private TaxClass taxClass;

    /**
     * Expression to determine tax class
     */
    @Column(name = "tax_class_el", length = 2000)
    @Size(max = 2000)
    private String taxClassEl;

    /**
     * Expression to determine tax class - for Spark
     */
    @Column(name = "tax_class_el_sp", length = 2000)
    @Size(max = 2000)
    private String taxClassElSpark;

    /**
     * Script to handle rating instead of a regular price plan logic
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rating_script_id")
    private ScriptInstance ratingScript;

    /**
     * Enable/disable removing rated WO to 0.
     */
    @Type(type = "numeric_boolean")
    @Column(name = "drop_zero_wo")
    protected boolean dropZeroWo;

    /**
     * El expression for sorting index used in WO and rated transaction
     */
    @Column(name = "sort_index_el")
    @Size(max = 2000)
    private String sortIndexEl;

    // Calculated values
    @Transient
    private boolean roundingValuesComputed;

    @Transient
    private int roundingUnityNbDecimal = 2;

    @Transient
    private int roundingEdrNbDecimal = BaseEntity.NB_DECIMALS;
    
    
	/**
	 * offer template
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name =  "offer_template_id", referencedColumnName = "id")
	//@NotNull
	protected OfferTemplate offerTemplate;
	
	
	/**
	 * product code
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", referencedColumnName = "id", nullable = false)
	//@NotNull
	protected Product product;
	
	
	 /**
     * Service template
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_template_id",referencedColumnName = "id")
    protected ServiceTemplate  serviceTemplate;
	
    
	 /**
     * Price Plan Matrix
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "price_plan_matrix_id",referencedColumnName = "id")
    protected PricePlanMatrix pricePlanMatrix;
    
    
    /**
     * recurence duration
     */
    @Column(name = "recurrence_duration_enum")
    @Enumerated(EnumType.STRING)
    protected RecurrenceDurationEnum recurrenceDurationEnum;
    
     
	 /**
     * duration service
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "duration_service",referencedColumnName = "id")
    protected ServiceTemplate durationService;
    
    /**
     * recurrence calendar
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recurrence_calendar")
    protected Calendar recurrenceCalendar;
    
    
  
	 /**
     * calendar service
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_service",referencedColumnName = "id")
    protected ServiceTemplate calendarService;
   
      
    /**
     * usage service
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usage_service_template_id",referencedColumnName = "id")
    protected ServiceTemplate  usageService;
    
    
    
    /**
     * surchage
     */
    @Type(type = "numeric_boolean")
    @Column(name = "surcharge")
    //@NotNull
    protected Boolean surcharge;
    
    
    /**
     * price matrix
     */
    @Type(type = "numeric_boolean")
    @Column(name = "price_matrix")
    //@NotNull
    protected Boolean priceMatrix;
    
    
    
    /**
     *param1
     */
    @Column(name = "param1", length = 50)
    @Size(max = 50)
    protected String param1;
    
    /**
     *param1 service
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "param1_service_id",referencedColumnName = "id")
    protected ServiceTemplate  param1Service;
    
    
    /**
     *param2
     */
    @Column(name = "param2", length = 50)
    @Size(max = 50)
    protected String param2;
    
    /**
     *param2 service
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "param2_service_id",referencedColumnName = "id")
    protected ServiceTemplate  param2Service;
    
    /**
     *param3
     */
    @Column(name = "param3", length = 50)
    @Size(max = 50)
    protected String param3;
    
    /**
     *param3 service
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "param3_service_id",referencedColumnName = "id")
    protected ServiceTemplate  param3Service;
    
    
    /**
     *param4
     */
    @Column(name = "param4", length = 50)
    @Size(max = 50)
    protected String param4;
    
    /**
     *param4 service
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "param4_service_id",referencedColumnName = "id")
    protected ServiceTemplate  param4Service;
    
    
    /**
     *dimension 1 matrix
     */
    @Column(name = "dim1_matrix", length = 50)
    @Size(max = 50)
    protected String dim1Matrix;
    
    
    /**
     * DIMENSION 1 MATRIX BYSTEP
     */
    @Type(type = "numeric_boolean")
    @Column(name = "dim1_matrix_bystep") 
    protected Boolean dim1MatrixByStep=false;
    
    
    /**
     *dimension 1 matrix service
     */ 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dim1_matrix_service",referencedColumnName = "id")
    protected ServiceTemplate dim1MatrixService;
    
    
    
    /**
     *dimension 2 matrix
     */
    @Column(name = "dim2_matrix", length = 50)
    @Size(max = 50)
    protected String dim2Matrix;
    
    
    /**
     * DIMENSION 2 MATRIX BYSTEP
     */
    @Type(type = "numeric_boolean")
    @Column(name = "dim2_matrix_bystep") 
    protected Boolean dim2MatrixByStep=false;
    
    
    /**
     *dimension 2 matrix service
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dim2_matrix_service",referencedColumnName = "id")
    protected ServiceTemplate dim2MatrixService;
    
    
    /**
     *dimension 3 matrix
     */
    @Column(name = "dim3_matrix", length = 50)
    @Size(max = 50)
    protected String dim3Matrix;
    
    
    /**
     * DIMENSION 3 MATRIX BYSTEP
     */
    @Type(type = "numeric_boolean")
    @Column(name = "dim3_matrix_bystep") 
    protected Boolean dim3MatrixByStep=false;
    
    
    /**
     *dimension 3 matrix service
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dim3_matrix_service",referencedColumnName = "id")
    protected ServiceTemplate dim3MatrixService;

    
    public String getInputUnitEL() {
        return inputUnitEL;
    }

    public void setInputUnitEL(String inputUnitEL) {
        this.inputUnitEL = inputUnitEL;
    }

    public String getOutputUnitEL() {
        return outputUnitEL;
    }

    public void setOutputUnitEL(String outputUnitEL) {
        this.outputUnitEL = outputUnitEL;
    }
    
    /**
     * Get a charge main type
     * 
     * @return Charge main type
     */
    public abstract ChargeMainTypeEnum getChargeMainType();

    public OperationTypeEnum getType() {
        return type;
    }

    public void setType(OperationTypeEnum type) {
        this.type = type;
    }

    public Boolean getAmountEditable() {
        return amountEditable;
    }

    public void setAmountEditable(Boolean amountEditable) {
        this.amountEditable = amountEditable;
    }

    public InvoiceSubCategory getInvoiceSubCategory() {
        return invoiceSubCategory;
    }

    public void setInvoiceSubCategory(InvoiceSubCategory invoiceSubCategory) {
        this.invoiceSubCategory = invoiceSubCategory;
    }

    public List<TriggeredEDRTemplate> getEdrTemplates() {
        return edrTemplates;
    }

    public void setEdrTemplates(List<TriggeredEDRTemplate> edrTemplates) {
        this.edrTemplates = edrTemplates;
    }

    public String getInputUnitDescription() {
        return inputUnitDescription;
    }

    public void setInputUnitDescription(String inputUnitDescription) {
        this.inputUnitDescription = inputUnitDescription;
    }

    public String getRatingUnitDescription() {
        return ratingUnitOfMeasure != null ? ratingUnitOfMeasure.getDescription() : ratingUnitDescription;
    }

    public void setRatingUnitDescription(String ratingUnitDescription) {
        this.ratingUnitDescription = ratingUnitDescription;
    }

    public BigDecimal getUnitMultiplicator() {
        return unitMultiplicator;
    }

    public void setUnitMultiplicator(BigDecimal unitMultiplicator) {
        updateUnitMultiplicator(unitMultiplicator);
    }

    /**
     * @return EDR and WO quantity field value precision
     */
    public int getUnitNbDecimal() {
        return unitNbDecimal;
    }

    /**
     * @param unitNbDecimal EDR and WO quantity field value precision
     */
    public void setUnitNbDecimal(int unitNbDecimal) {
        this.unitNbDecimal = unitNbDecimal;
    }

    /**
     * @return EDR and WO quantity field value rounding
     */
    public RoundingModeEnum getRoundingMode() {
        return roundingMode;
    }

    /**
     * @param roundingMode EDR and WO quantity field value rounding
     */
    public void setRoundingMode(RoundingModeEnum roundingMode) {
        this.roundingMode = roundingMode;
    }

    public RevenueRecognitionRule getRevenueRecognitionRule() {
        return revenueRecognitionRule;
    }

    public void setRevenueRecognitionRule(RevenueRecognitionRule revenueRecognitionRule) {
        this.revenueRecognitionRule = revenueRecognitionRule;
    }

    public Map<String, String> getDescriptionI18n() {
        return descriptionI18n;
    }

    public void setDescriptionI18n(Map<String, String> descriptionI18n) {
        this.descriptionI18n = descriptionI18n;
    }

    /**
     * Instantiate descriptionI18n field if it is null. NOTE: do not use this method unless you have an intention to modify it's value, as entity will be marked dirty and record
     * will be updated in DB
     * 
     * @return descriptionI18n value or instantiated descriptionI18n field value
     */
    public Map<String, String> getDescriptionI18nNullSafe() {
        if (descriptionI18n == null) {
            descriptionI18n = new HashMap<>();
        }
        return descriptionI18n;
    }

    /**
     * @return The EL expression if charge applies
     */
    public String getFilterExpression() {
        return filterExpression;
    }

    /**
     * @param filterExpression The EL expression if charge applies
     */
    public void setFilterExpression(String filterExpression) {
        this.filterExpression = filterExpression;
    }

    /**
     * @return Expression to determine if charge applies - for Spark
     */
    public String getFilterExpressionSpark() {
        return filterExpressionSpark;
    }

    /**
     * @param filterExpressionSpark Expression to determine if charge applies - for Spark
     */
    public void setFilterExpressionSpark(String filterExpressionSpark) {
        this.filterExpressionSpark = filterExpressionSpark;
    }

    private void computeRoundingValues() {
        if (roundingValuesComputed) {
            return;
        }
        try {
            if (unitNbDecimal >= BaseEntity.NB_DECIMALS) {
                roundingUnityNbDecimal = BaseEntity.NB_DECIMALS;
            } else {
                roundingUnityNbDecimal = unitNbDecimal;
                roundingEdrNbDecimal = (int) Math.round(roundingUnityNbDecimal + Math.floor(Math.log10(unitMultiplicator.doubleValue())));
                if (roundingEdrNbDecimal > BaseEntity.NB_DECIMALS) {
                    roundingEdrNbDecimal = BaseEntity.NB_DECIMALS;
                }
            }
        } catch (Exception e) {
        }
    }

    protected int getRoundingEdrNbDecimal() {
        computeRoundingValues();// See if this can be computed only once upon entity load or upon value change
        return roundingEdrNbDecimal;
    }

    protected int getRoundingUnityNbDecimal() {
        computeRoundingValues(); // See if this can be computed only once upon entity load or upon value change
        return roundingUnityNbDecimal;
    }

    public UnitOfMeasure getInputUnitOfMeasure() {
        return inputUnitOfMeasure;
    }

    public void setInputUnitOfMeasure(UnitOfMeasure inputUnitOfMeasure) {
        this.inputUnitOfMeasure = inputUnitOfMeasure;
        updateUnitMultiplicator(null);
    }

    public UnitOfMeasure getRatingUnitOfMeasure() {
        return ratingUnitOfMeasure;
    }

    public void setRatingUnitOfMeasure(UnitOfMeasure ratingUnitOfMeasure) {
        this.ratingUnitOfMeasure = ratingUnitOfMeasure;
        updateUnitMultiplicator(null);
    }

    private void updateUnitMultiplicator(BigDecimal multiplicator) {
        this.unitMultiplicator = calculateUnitMultiplicator(multiplicator, this.inputUnitOfMeasure, this.ratingUnitOfMeasure);
    }

    private BigDecimal calculateUnitMultiplicator(BigDecimal multiplicator, UnitOfMeasure IUM, UnitOfMeasure RUM) {
        if (IUM != null && RUM != null && IUM.isCompatibleWith(RUM)) {
            return BigDecimal.valueOf(IUM.getMultiplicator()).divide(BigDecimal.valueOf(RUM.getMultiplicator()), BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP);
        } else if (multiplicator != null) {
            return multiplicator;
        }
        return unitMultiplicator;
    }

    /**
     * @return Charge tax class
     */
    public TaxClass getTaxClass() {
        return taxClass;
    }

    /**
     * @param taxClass Charge tax class
     */
    public void setTaxClass(TaxClass taxClass) {
        this.taxClass = taxClass;
    }

    /**
     * @return Expression to determine tax class
     */
    public String getTaxClassEl() {
        return taxClassEl;
    }

    /**
     * @param taxClassEl Expression to determine tax class
     */
    public void setTaxClassEl(String taxClassEl) {
        this.taxClassEl = taxClassEl;
    }

    /**
     * @return Expression to determine tax class - for Spark
     */
    public String getTaxClassElSpark() {
        return taxClassElSpark;
    }

    /**
     * @param taxClassElSpark Expression to determine tax class - for Spark
     */
    public void setTaxClassElSpark(String taxClassElSpark) {
        this.taxClassElSpark = taxClassElSpark;
    }

    /**
     * @return Script to handle rating instead of a regular price plan logic
     */
    public ScriptInstance getRatingScript() {
        return ratingScript;
    }

    /**
     * @param ratingScript Script to handle rating instead of a regular price plan logic
     */
    public void setRatingScript(ScriptInstance ratingScript) {
        this.ratingScript = ratingScript;
    }

    /**
     * Check if removing WO rated to 0 is enabled or not.
     *
     * @return true if is enabled false else.
     */
    public boolean isDropZeroWo() {
        return dropZeroWo;
    }

    /**
     * Enable/disable removing WO rated to 0.
     *
     * @param dropZeroWo
     */
    public void setDropZeroWo(boolean dropZeroWo) {
        this.dropZeroWo = dropZeroWo;
    }

    /**
     * Gets an El expression for the sorting index.
     *
     * @return an El expression
     */
    public String getSortIndexEl() {
        return sortIndexEl;
    }

    /**
     * Sets the El expression for the sorting index.
     *
     * @param sortIndexEl the sorting index El
     */
    public void setSortIndexEl(String sortIndexEl) {
        this.sortIndexEl = sortIndexEl;
    }

	/**
	 * @return the offerTemplate
	 */
	public OfferTemplate getOfferTemplate() {
		return offerTemplate;
	}

	/**
	 * @param offerTemplate the offerTemplate to set
	 */
	public void setOfferTemplate(OfferTemplate offerTemplate) {
		this.offerTemplate = offerTemplate;
	}

	/**
	 * @return the product
	 */
	public Product getProduct() {
		return product;
	}

	/**
	 * @param product the product to set
	 */
	public void setProduct(Product product) {
		this.product = product;
	}

	/**
	 * @return the serviceTemplate
	 */
	public ServiceTemplate getServiceTemplate() {
		return serviceTemplate;
	}

	/**
	 * @param serviceTemplate the serviceTemplate to set
	 */
	public void setServiceTemplate(ServiceTemplate serviceTemplate) {
		this.serviceTemplate = serviceTemplate;
	}

	/**
	 * @return the pricePlanMatrix
	 */
	public PricePlanMatrix getPricePlanMatrix() {
		return pricePlanMatrix;
	}

	/**
	 * @param pricePlanMatrix the pricePlanMatrix to set
	 */
	public void setPricePlanMatrix(PricePlanMatrix pricePlanMatrix) {
		this.pricePlanMatrix = pricePlanMatrix;
	}

	/**
	 * @return the recurrenceDurationEnum
	 */
	public RecurrenceDurationEnum getRecurrenceDurationEnum() {
		return recurrenceDurationEnum;
	}

	/**
	 * @param recurrenceDurationEnum the recurrenceDurationEnum to set
	 */
	public void setRecurrenceDurationEnum(RecurrenceDurationEnum recurrenceDurationEnum) {
		this.recurrenceDurationEnum = recurrenceDurationEnum;
	}
 

	/**
	 * @return the recurrenceCalendar
	 */
	public Calendar getRecurrenceCalendar() {
		return recurrenceCalendar;
	}

	/**
	 * @param recurrenceCalendar the recurrenceCalendar to set
	 */
	public void setRecurrenceCalendar(Calendar recurrenceCalendar) {
		this.recurrenceCalendar = recurrenceCalendar;
	}


	/**
	 * @return the surcharge
	 */
	public Boolean getSurcharge() {
		return surcharge;
	}

	/**
	 * @param surcharge the surcharge to set
	 */
	public void setSurcharge(Boolean surcharge) {
		this.surcharge = surcharge;
	}

	/**
	 * @return the priceMatrix
	 */
	public Boolean getPriceMatrix() {
		return priceMatrix;
	}

	/**
	 * @param priceMatrix the priceMatrix to set
	 */
	public void setPriceMatrix(Boolean priceMatrix) {
		this.priceMatrix = priceMatrix;
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
	 * @return the dim1Matrix
	 */
	public String getDim1Matrix() {
		return dim1Matrix;
	}

	/**
	 * @param dim1Matrix the dim1Matrix to set
	 */
	public void setDim1Matrix(String dim1Matrix) {
		this.dim1Matrix = dim1Matrix;
	}

	/**
	 * @return the dim1MatrixByStep
	 */
	public Boolean getDim1MatrixByStep() {
		return dim1MatrixByStep;
	}

	/**
	 * @param dim1MatrixByStep the dim1MatrixByStep to set
	 */
	public void setDim1MatrixByStep(Boolean dim1MatrixByStep) {
		this.dim1MatrixByStep = dim1MatrixByStep;
	}

	 
	/**
	 * @return the dim2Matrix
	 */
	public String getDim2Matrix() {
		return dim2Matrix;
	}

	/**
	 * @param dim2Matrix the dim2Matrix to set
	 */
	public void setDim2Matrix(String dim2Matrix) {
		this.dim2Matrix = dim2Matrix;
	}

	/**
	 * @return the dim2MatrixByStep
	 */
	public Boolean getDim2MatrixByStep() {
		return dim2MatrixByStep;
	}

	/**
	 * @param dim2MatrixByStep the dim2MatrixByStep to set
	 */
	public void setDim2MatrixByStep(Boolean dim2MatrixByStep) {
		this.dim2MatrixByStep = dim2MatrixByStep;
	}

	 

	/**
	 * @return the dim3Matrix
	 */
	public String getDim3Matrix() {
		return dim3Matrix;
	}

	/**
	 * @param dim3Matrix the dim3Matrix to set
	 */
	public void setDim3Matrix(String dim3Matrix) {
		this.dim3Matrix = dim3Matrix;
	}

	/**
	 * @return the dim3MatrixByStep
	 */
	public Boolean getDim3MatrixByStep() {
		return dim3MatrixByStep;
	}

	/**
	 * @param dim3MatrixByStep the dim3MatrixByStep to set
	 */
	public void setDim3MatrixByStep(Boolean dim3MatrixByStep) {
		this.dim3MatrixByStep = dim3MatrixByStep;
	}

	/**
	 * @return the durationService
	 */
	public ServiceTemplate getDurationService() {
		return durationService;
	}

	/**
	 * @param durationService the durationService to set
	 */
	public void setDurationService(ServiceTemplate durationService) {
		this.durationService = durationService;
	}

	/**
	 * @return the calendarService
	 */
	public ServiceTemplate getCalendarService() {
		return calendarService;
	}

	/**
	 * @param calendarService the calendarService to set
	 */
	public void setCalendarService(ServiceTemplate calendarService) {
		this.calendarService = calendarService;
	}

	/**
	 * @return the usageService
	 */
	public ServiceTemplate getUsageService() {
		return usageService;
	}

	/**
	 * @param usageService the usageService to set
	 */
	public void setUsageService(ServiceTemplate usageService) {
		this.usageService = usageService;
	}

	/**
	 * @return the param1Service
	 */
	public ServiceTemplate getParam1Service() {
		return param1Service;
	}

	/**
	 * @param param1Service the param1Service to set
	 */
	public void setParam1Service(ServiceTemplate param1Service) {
		this.param1Service = param1Service;
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
	 * @return the param2Service
	 */
	public ServiceTemplate getParam2Service() {
		return param2Service;
	}

	/**
	 * @param param2Service the param2Service to set
	 */
	public void setParam2Service(ServiceTemplate param2Service) {
		this.param2Service = param2Service;
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
	 * @return the param3Service
	 */
	public ServiceTemplate getParam3Service() {
		return param3Service;
	}

	/**
	 * @param param3Service the param3Service to set
	 */
	public void setParam3Service(ServiceTemplate param3Service) {
		this.param3Service = param3Service;
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
	 * @return the param4Service
	 */
	public ServiceTemplate getParam4Service() {
		return param4Service;
	}

	/**
	 * @param param4Service the param4Service to set
	 */
	public void setParam4Service(ServiceTemplate param4Service) {
		this.param4Service = param4Service;
	}

	/**
	 * @return the dim1MatrixService
	 */
	public ServiceTemplate getDim1MatrixService() {
		return dim1MatrixService;
	}

	/**
	 * @param dim1MatrixService the dim1MatrixService to set
	 */
	public void setDim1MatrixService(ServiceTemplate dim1MatrixService) {
		this.dim1MatrixService = dim1MatrixService;
	}

	/**
	 * @return the dim2MatrixService
	 */
	public ServiceTemplate getDim2MatrixService() {
		return dim2MatrixService;
	}

	/**
	 * @param dim2MatrixService the dim2MatrixService to set
	 */
	public void setDim2MatrixService(ServiceTemplate dim2MatrixService) {
		this.dim2MatrixService = dim2MatrixService;
	}

	/**
	 * @return the dim3MatrixService
	 */
	public ServiceTemplate getDim3MatrixService() {
		return dim3MatrixService;
	}

	/**
	 * @param dim3MatrixService the dim3MatrixService to set
	 */
	public void setDim3MatrixService(ServiceTemplate dim3MatrixService) {
		this.dim3MatrixService = dim3MatrixService;
	}

	 

    
}