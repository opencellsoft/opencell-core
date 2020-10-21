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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BaseEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.ProductOffering;
import org.meveo.model.cpq.quote.QuoteCpqItem;

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
    
    @Embedded
    private QuoteCpqItem quoteCpqItem;

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
	 * @return the quoteCpqItem
	 */
	public QuoteCpqItem getQuoteCpqItem() {
		return quoteCpqItem;
	}

	/**
	 * @param quoteCpqItem the quoteCpqItem to set
	 */
	public void setQuoteCpqItem(QuoteCpqItem quoteCpqItem) {
		this.quoteCpqItem = quoteCpqItem;
	}
}