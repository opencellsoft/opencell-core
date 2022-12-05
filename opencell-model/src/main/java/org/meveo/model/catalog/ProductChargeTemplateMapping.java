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

import java.util.List;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BaseEntity;
import org.meveo.model.cpq.Product;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * product to charge template mapping
 * 
 * @author Rachid Aity-yaazza
 * @lastModifiedVersion 11.0
 * @param <T> Charge template type
 */


@Entity
@Table(name = "cpq_product_charge_template_mapping", uniqueConstraints = @UniqueConstraint(columnNames = { "id" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_product_charge_template_mapping_seq"), })
public class ProductChargeTemplateMapping<T extends ChargeTemplate> extends BaseEntity {

    private static final long serialVersionUID = -1872859127097329926L;

    /**
     * Service template
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    protected Product product;

    /**
     * Charge template
     */
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = ChargeTemplate.class)
    @JoinColumn(name = "charge_template_id")
    protected T chargeTemplate;

    /**
     * Counter associated to a charge template.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "counter_template_id")
    private CounterTemplate counterTemplate;

    /**
     * Prepaid wallet templates to charge on.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "cat_product_wallet_template_1", joinColumns = @JoinColumn(name = "product_templt_id"), inverseJoinColumns = @JoinColumn(name = "wallet_template_id"))
    @OrderColumn(name = "INDX")
    private List<WalletTemplate> walletTemplates;

    /**
     * Counters associated to a charge template.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "cat_product_counter_template", joinColumns = @JoinColumn(name = "product_templt_id"), inverseJoinColumns = @JoinColumn(name = "counter_template_id"))
    @OrderColumn(name = "INDX")
    private List<CounterTemplate> accumulatorCounterTemplates;

    public T getChargeTemplate() {
        return chargeTemplate;
    }

    public void setChargeTemplate(T chargeTemplate) {
        this.chargeTemplate = chargeTemplate;
    }

    public CounterTemplate getCounterTemplate() {
        return counterTemplate;
    }

    public void setCounterTemplate(CounterTemplate counterTemplate) {
        this.counterTemplate = counterTemplate;
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
	 * @return the walletTemplates
	 */
	public List<WalletTemplate> getWalletTemplates() {
		return walletTemplates;
	}

	/**
	 * @param walletTemplates the walletTemplates to set
	 */
	public void setWalletTemplates(List<WalletTemplate> walletTemplates) {
		this.walletTemplates = walletTemplates;
	}

	/**
	 * @return the accumulatorCounterTemplates
	 */
	public List<CounterTemplate> getAccumulatorCounterTemplates() {
		return accumulatorCounterTemplates;
	}

	/**
	 * @param accumulatorCounterTemplates the accumulatorCounterTemplates to set
	 */
	public void setAccumulatorCounterTemplates(List<CounterTemplate> accumulatorCounterTemplates) {
		this.accumulatorCounterTemplates = accumulatorCounterTemplates;
	}
}