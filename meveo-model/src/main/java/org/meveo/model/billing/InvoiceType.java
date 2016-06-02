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
package org.meveo.model.billing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.admin.Seller;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.OCCTemplate;

@Entity
@ExportIdentifier({ "code", "provider" })
@Table(name = "BILLING_INVOICE_TYPE", uniqueConstraints = @UniqueConstraint(columnNames = { "CODE", "PROVIDER_ID","OCC_TEMPLATE_ID" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BILLING_INVOICE_TYPE_SEQ")
@NamedQueries({ @NamedQuery(name = "InvoiceType.currentInvoiceNb", query = "select max(sequence.currentInvoiceNb) from InvoiceType i where i.provider=:provider") })
public class InvoiceType extends BusinessEntity {

	private static final long serialVersionUID = 1L;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OCC_TEMPLATE_ID")
	private OCCTemplate occTemplate;
		
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "BILLING_INVOICE_TYPE_APPLIES_TO", joinColumns = @JoinColumn(name = "INVOICE_TYPE_ID"), inverseJoinColumns = @JoinColumn(name = "APPLIES_TO_ID"))
	private List<InvoiceType> appliesTo = new ArrayList<InvoiceType>();
	
	@Embedded
	Sequence sequence = new Sequence();
	
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "BILLING_SEQ_INVTYP_SELL") 
	@MapKeyJoinColumn(name="SELLER_ID")
	Map<Seller,Sequence> sellerSequence = new HashMap<Seller,Sequence>();
	
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "BILLING_SEQ_INVTYP_PROV") 
	@MapKeyJoinColumn(name="PROVIDER_ID")
	Map<Provider,Sequence> providerSequence = new HashMap<Provider,Sequence>();

	
	@Column(name = "MATCHING_AUTO")
	private boolean matchingAuto = false;
	
	 
	public InvoiceType(){
		
	}

	/**
	 * @return the occTemplate
	 */
	public OCCTemplate getOccTemplate() {
		return occTemplate;
	}

	/**
	 * @param occTemplate the occTemplate to set
	 */
	public void setOccTemplate(OCCTemplate occTemplate) {
		this.occTemplate = occTemplate;
	}

	/**
	 * @return the appliesTo
	 */
	public List<InvoiceType> getAppliesTo() {
		return appliesTo;
	}

	/**
	 * @param appliesTo the appliesTo to set
	 */
	public void setAppliesTo(List<InvoiceType> appliesTo) {
		this.appliesTo = appliesTo;
	}

	/**
	 * @return the matchingAuto
	 */
	public boolean isMatchingAuto() {
		return matchingAuto;
	}

	/**
	 * @param matchingAuto the matchingAuto to set
	 */
	public void setMatchingAuto(boolean matchingAuto) {
		this.matchingAuto = matchingAuto;
	}

	/**
	 * @return the sequence
	 */
	public Sequence getSequence() {		
		return sequence;
	}

	/**
	 * @param sequence the sequence to set
	 */
	public void setSequence(Sequence sequence) {
		this.sequence = sequence;
	}

	/**
	 * @return the sellerSequence
	 */
	public Map<Seller, Sequence> getSellerSequence() {
		return sellerSequence;
	}

	/**
	 * @param sellerSequence the sellerSequence to set
	 */
	public void setSellerSequence(Map<Seller, Sequence> sellerSequence) {
		this.sellerSequence = sellerSequence;
	}

	/**
	 * @return the providerSequence
	 */
	public Map<Provider, Sequence> getProviderSequence() {
		return providerSequence;
	}

	/**
	 * @param providerSequence the providerSequence to set
	 */
	public void setProviderSequence(Map<Provider, Sequence> providerSequence) {
		this.providerSequence = providerSequence;
	}
	
	
	
}
