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

package org.meveo.model.quote;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.ProductOffering;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.offer.OfferComponent;

/**
 * Quote item
 * 
 * @author Andrius Karpavicius
 */
@Entity
@ExportIdentifier({ "quote.code", "itemId" })
@Table(name = "ord_quote_item", uniqueConstraints = @UniqueConstraint(columnNames = { "code"}))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "ord_quote_item_seq"), })
@NamedQuery(name = "QuoteItem.findByCode", query = "select q from QuoteItem q where q.code=:code")
public class QuoteItem extends BusinessEntity {

    private static final long serialVersionUID = -6831399734977276174L;

    /**
     * Quote
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_id", nullable = false, updatable = false)
    @NotNull
    private Quote quote;

    /**
     * Item id in the quote
     */
    @Column(name = "item_id", length = 10, nullable = false)
    @NotNull
    private String itemId;

    /**
     * Product offerings associated to an quote item. In case of bundled offers, the first item in a list is the parent offering.
     */
    @OneToMany(mappedBy = "quoteItem", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderColumn(name = "ITEM_ORDER")
    private List<QuoteItemProductOffering> quoteItemProductOfferings = new ArrayList<>();

    /**
     * Serialized quoteItem dto
     */
    @Column(name = "source", nullable = false, columnDefinition = "TEXT")
    private String source;

    /**
     * Quote item processing status as defined by the workflow.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    @NotNull
    private QuoteStatusEnum status = QuoteStatusEnum.IN_PROGRESS;

    /**
     * Associated user account
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_account_id", nullable = false)
    @NotNull
    private UserAccount userAccount;

    /**
     * Deserialized quoteItemDto
     */
    @Transient
    private Object quoteItemDto;

    /**
     * Main product offering
     */
    @Transient
    private ProductOffering mainOffering;
    
    /**
     * Quote version
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_version_id", nullable = false, updatable = false)
    @NotNull
    private QuoteVersion quoteVersion;
    

    /**
     * customer service
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_customer_service_id", updatable = false)
    private QuoteCustomerService quoteCustomerService;
    

    /**
     * quote product
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_product_id", updatable = false)
    private QuoteProduct quoteProduct;


    
    @Column(name = "service_code", nullable = false, length = 20)
    @Size(max = 20)
    private String serviceCode;

    /**
     * Quantity subscribed
     
    @Column(name = "quantity", precision = 23, scale = 12, nullable = false)
    @NotNull
    private BigDecimal quantity = BigDecimal.ONE;*/
    
    /**
     * service type
     */
    @Column(name = "service_type", nullable = false)
    private Integer serviceType;
    
    /**
     * value
     */
    @Column(name = "value")
    @Lob
    private String value;
    
    /**
     * OS_UNIT_PRICE_WITHOUT_TAX
     
    @Column(name = "os_unite_price_without_tax", precision = 23, scale = 12)
    private BigDecimal osUnitPriceWithoutTax;*/

    /**
     * OS_PRICE_WITHOUT_TAX
     */
    @Column(name = "os_price_without_tax", precision = 23, scale = 12)
    private BigDecimal osPriceWithoutTax;
    
    /**
     * OS_TAX_CODE
     */
    @Column(name = "os_tax_code", precision = 23, scale = 12)
    private BigDecimal osTAxCode;
    
    /**
     * OS_TAX_RATE
     */
    @Column(name = "os_tax_rate")
    private int osTAxRate;
    
    /**
     * OS_PRICE_WITH_TAX
     */
    @Column(name = "os_price_with_tax", precision = 23, scale = 12)
    private BigDecimal osPriceWithTax;

    /**
     * RECURRENCE_DURATION
     */
    @Column(name = "recurrence_duration")
    private int recurrenceDuration;
    /**
     * RECURRENCE_PERIODICITY
     */
    @Column(name = "recurrence_periodicity")
    private int recurrencePeriodicity;
    

    /**
     * RC_UNIT_PRICE_WITHOUT_TAX
     */
    @Column(name = "rc_unite_price_without_tax", precision = 23, scale = 12)
    private BigDecimal rcUnitPriceWithoutTax;

    /**
     * RC_PRICE_WITHOUT_TAX
     */
    @Column(name = "rc_price_without_tax", precision = 23, scale = 12)
    private BigDecimal rcPriceWithoutTax;
    
    /**
     * RC_TAX_CODE
     */
    @Column(name = "rc_tax_code", precision = 23, scale = 12)
    private BigDecimal rcTAxCode;
    
    /**
     * RC_TAX_RATE
     */
    @Column(name = "rc_tax_rate")
    private int rcTAxRate;
    
    /**
     * RC_PRICE_WITH_TAX
     */
    @Column(name = "rc_price_with_tax", precision = 23, scale = 12)
    private BigDecimal rcPriceWithTax;

    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "quote_article_line_id", nullable = false, referencedColumnName = "id")
	@NotNull
    private QuoteArticleLine quoteArticleLine;

    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", nullable = false, referencedColumnName = "id")
	@NotNull
    private Product product;
    
    public Quote getQuote() {
        return quote;
    }

    public void setQuote(Quote quote) {
        this.quote = quote;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public List<QuoteItemProductOffering> getQuoteItemProductOfferings() {
        return quoteItemProductOfferings;
    }

    public void setQuoteItemProductOfferings(List<QuoteItemProductOffering> quoteItemProductOfferings) {
        this.quoteItemProductOfferings = quoteItemProductOfferings;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String oferItemSource) {
        this.source = oferItemSource;
    }

    public QuoteStatusEnum getStatus() {
        return status;
    }

    public void setStatus(QuoteStatusEnum status) {
        this.status = status;
    }

    public Object getQuoteItemDto() {
        return quoteItemDto;
    }

    public void setQuoteItemDto(Object quoteItemDto) {
        this.quoteItemDto = quoteItemDto;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public ProductOffering getMainOffering() {

        if (mainOffering == null && !quoteItemProductOfferings.isEmpty()) {
            mainOffering = quoteItemProductOfferings.get(0).getProductOffering();
        }

        return mainOffering;
    }

    public void setMainOffering(ProductOffering mainOffering) {
        this.mainOffering = mainOffering;
    }

    public void resetMainOffering(ProductOffering newMainOffer) {
        this.mainOffering = newMainOffer;
        quoteItemProductOfferings.clear();
        if (newMainOffer != null) {
            quoteItemProductOfferings.add(new QuoteItemProductOffering(this, newMainOffer, 0));
        }
        quoteItemDto = null;
        source = null;
    }

    /**
     * Interested in comparing quote items within the quote only
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof QuoteItem)) {
            return false;
        }

        QuoteItem other = (QuoteItem) obj;

        if (id != null && other.getId() != null && id.equals(other.getId())) {
            return true;
        }

        return StringUtils.compare(getItemId(), other.getItemId()) == 0;
    }

	/**
	 * @return the quoteVersion
	 */
	public QuoteVersion getQuoteVersion() {
		return quoteVersion;
	}

	/**
	 * @param quoteVersion the quoteVersion to set
	 */
	public void setQuoteVersion(QuoteVersion quoteVersion) {
		this.quoteVersion = quoteVersion;
	}


	/**
	 * @return the quoteProduct
	 */
	public QuoteProduct getQuoteProduct() {
		return quoteProduct;
	}

	/**
	 * @param quoteProduct the quoteProduct to set
	 */
	public void setQuoteProduct(QuoteProduct quoteProduct) {
		this.quoteProduct = quoteProduct;
	}
 

	/**
	 * @return the serviceCode
	 */
	public String getServiceCode() {
		return serviceCode;
	}

	/**
	 * @param serviceCode the serviceCode to set
	 */
	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	/**
	 * @return the serviceType
	 */
	public Integer getServiceType() {
		return serviceType;
	}

	/**
	 * @param serviceType the serviceType to set
	 */
	public void setServiceType(Integer serviceType) {
		this.serviceType = serviceType;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}


	/**
	 * @return the osPriceWithoutTax
	 */
	public BigDecimal getOsPriceWithoutTax() {
		return osPriceWithoutTax;
	}

	/**
	 * @param osPriceWithoutTax the osPriceWithoutTax to set
	 */
	public void setOsPriceWithoutTax(BigDecimal osPriceWithoutTax) {
		this.osPriceWithoutTax = osPriceWithoutTax;
	}

	/**
	 * @return the osTAxCode
	 */
	public BigDecimal getOsTAxCode() {
		return osTAxCode;
	}

	/**
	 * @param osTAxCode the osTAxCode to set
	 */
	public void setOsTAxCode(BigDecimal osTAxCode) {
		this.osTAxCode = osTAxCode;
	}

	/**
	 * @return the osTAxRate
	 */
	public int getOsTAxRate() {
		return osTAxRate;
	}

	/**
	 * @param osTAxRate the osTAxRate to set
	 */
	public void setOsTAxRate(int osTAxRate) {
		this.osTAxRate = osTAxRate;
	}

	/**
	 * @return the osPriceWithTax
	 */
	public BigDecimal getOsPriceWithTax() {
		return osPriceWithTax;
	}

	/**
	 * @param osPriceWithTax the osPriceWithTax to set
	 */
	public void setOsPriceWithTax(BigDecimal osPriceWithTax) {
		this.osPriceWithTax = osPriceWithTax;
	}

	/**
	 * @return the recurrenceDuration
	 */
	public int getRecurrenceDuration() {
		return recurrenceDuration;
	}

	/**
	 * @param recurrenceDuration the recurrenceDuration to set
	 */
	public void setRecurrenceDuration(int recurrenceDuration) {
		this.recurrenceDuration = recurrenceDuration;
	}

	/**
	 * @return the recurrencePeriodicity
	 */
	public int getRecurrencePeriodicity() {
		return recurrencePeriodicity;
	}

	/**
	 * @param recurrencePeriodicity the recurrencePeriodicity to set
	 */
	public void setRecurrencePeriodicity(int recurrencePeriodicity) {
		this.recurrencePeriodicity = recurrencePeriodicity;
	}

	/**
	 * @return the rcUnitPriceWithoutTax
	 */
	public BigDecimal getRcUnitPriceWithoutTax() {
		return rcUnitPriceWithoutTax;
	}

	/**
	 * @param rcUnitPriceWithoutTax the rcUnitPriceWithoutTax to set
	 */
	public void setRcUnitPriceWithoutTax(BigDecimal rcUnitPriceWithoutTax) {
		this.rcUnitPriceWithoutTax = rcUnitPriceWithoutTax;
	}

	/**
	 * @return the rcPriceWithoutTax
	 */
	public BigDecimal getRcPriceWithoutTax() {
		return rcPriceWithoutTax;
	}

	/**
	 * @param rcPriceWithoutTax the rcPriceWithoutTax to set
	 */
	public void setRcPriceWithoutTax(BigDecimal rcPriceWithoutTax) {
		this.rcPriceWithoutTax = rcPriceWithoutTax;
	}

	/**
	 * @return the rcTAxCode
	 */
	public BigDecimal getRcTAxCode() {
		return rcTAxCode;
	}

	/**
	 * @param rcTAxCode the rcTAxCode to set
	 */
	public void setRcTAxCode(BigDecimal rcTAxCode) {
		this.rcTAxCode = rcTAxCode;
	}

	/**
	 * @return the rcTAxRate
	 */
	public int getRcTAxRate() {
		return rcTAxRate;
	}

	/**
	 * @param rcTAxRate the rcTAxRate to set
	 */
	public void setRcTAxRate(int rcTAxRate) {
		this.rcTAxRate = rcTAxRate;
	}

	/**
	 * @return the rcPriceWithTax
	 */
	public BigDecimal getRcPriceWithTax() {
		return rcPriceWithTax;
	}

	/**
	 * @param rcPriceWithTax the rcPriceWithTax to set
	 */
	public void setRcPriceWithTax(BigDecimal rcPriceWithTax) {
		this.rcPriceWithTax = rcPriceWithTax;
	}

	/**
	 * @return the quoteCustomerService
	 */
	public QuoteCustomerService getQuoteCustomerService() {
		return quoteCustomerService;
	}

	/**
	 * @param quoteCustomerService the quoteCustomerService to set
	 */
	public void setQuoteCustomerService(QuoteCustomerService quoteCustomerService) {
		this.quoteCustomerService = quoteCustomerService;
	}

	/**
	 * @return the quoteArticleLine
	 */
	public QuoteArticleLine getQuoteArticleLine() {
		return quoteArticleLine;
	}

	/**
	 * @param quoteArticleLine the quoteArticleLine to set
	 */
	public void setQuoteArticleLine(QuoteArticleLine quoteArticleLine) {
		this.quoteArticleLine = quoteArticleLine;
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

}