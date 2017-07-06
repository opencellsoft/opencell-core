package org.meveo.model.quote;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.IEntity;
import org.meveo.model.catalog.ProductOffering;

@Entity
@ExportIdentifier({ "quoteItem.code", "productOffering.code" })
@Table(name = "ord_quot_item_offerings")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {@Parameter(name = "sequence_name", value = "ord_quot_item_offerings_seq"), })
public class QuoteItemProductOffering implements IEntity {

    public QuoteItemProductOffering() {

    }

    public QuoteItemProductOffering(QuoteItem quoteItem, ProductOffering productOffering, int itemOrder) {
        this.quoteItem = quoteItem;
        this.productOffering = productOffering;
        this.itemOrder = itemOrder;
    }

    @Id
    @GeneratedValue(generator = "ID_GENERATOR", strategy = GenerationType.AUTO)
    @Column(name = "id")
    @Access(AccessType.PROPERTY)
    protected Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quote_item_id")
    @NotNull
    private QuoteItem quoteItem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prd_offering_id")
    @NotNull
    private ProductOffering productOffering;

    @Column(name = "item_order", nullable = false)
    @NotNull
    private int itemOrder;

    @Override
    public Serializable getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;

    }

    @Override
    public boolean isTransient() {
        return id == null;
    }

    public QuoteItem getQuoteItem() {
        return quoteItem;
    }

    public void setQuoteItem(QuoteItem quoteItem) {
        this.quoteItem = quoteItem;
    }

    public ProductOffering getProductOffering() {
        return productOffering;
    }

    public void setProductOffering(ProductOffering productOffering) {
        this.productOffering = productOffering;
    }

    public int getItemOrder() {
        return itemOrder;
    }

    public void setItemOrder(int itemOrder) {
        this.itemOrder = itemOrder;
    }
}