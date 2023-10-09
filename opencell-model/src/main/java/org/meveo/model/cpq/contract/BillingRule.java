package org.meveo.model.cpq.contract;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.EnableEntity;

/**
 * 
 * @author Anas
 * @since 13.0
 *
 */
@Entity
@Table(name = "billing_redirection_rule")
@Cacheable
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_billing_rule_seq"), })
@NamedQueries({
    @NamedQuery(name = "BillingRule.findByAccounts", query = "select br from BillingRule br where "
            + " br.contract.status = 'ACTIVE' and (br.contract.beginDate <= current_date and br.contract.endDate > current_date) "
            + " and (br.contract.billingAccount.id is null or br.contract.billingAccount.id=:billingAccountId) "
            + " and (br.contract.customerAccount.id is null or br.contract.customerAccount.id=:customerAccountId) "
            + " and (br.contract.customer.id is null or br.contract.customer.id=:customerId) "
            + " and (br.contract.seller.id is null or br.contract.seller.id=:sellerId) "
            + " order by br.contract.billingAccount.id, br.contract.customerAccount.id, br.contract.customer.id, br.contract.seller.id, br.priority NULLS LAST"),
    @NamedQuery(name = "BillingRule.findByAccountsWithSellerNull", query = "select br from BillingRule br where "
            + " br.contract.status = 'ACTIVE' and (br.contract.beginDate <= current_date and br.contract.endDate > current_date) "
            + " and (br.contract.billingAccount.id is null or br.contract.billingAccount.id=:billingAccountId) "
            + " and (br.contract.customerAccount.id is null or br.contract.customerAccount.id=:customerAccountId) "
            + " and (br.contract.customer.id is null or br.contract.customer.id=:customerId) "
            + " and (br.contract.seller.id is null) "
            + " order by br.contract.billingAccount.id, br.contract.customerAccount.id, br.contract.customer.id, br.contract.seller.id, br.priority NULLS LAST"),
    @NamedQuery(name = "BillingRule.findByAccountsAndContract", query = "select br from BillingRule br where "
            + " br.contract.id=:contractId "
            + " and (br.contract.billingAccount.id is null or br.contract.billingAccount.id=:billingAccountId) "
            + " and (br.contract.customerAccount.id is null or br.contract.customerAccount.id=:customerAccountId) "
            + " and (br.contract.customer.id is null or br.contract.customer.id=:customerId) "
            + " and (br.contract.seller.id is null or br.contract.seller.id=:sellerId) "
            + " order by br.contract.billingAccount.id, br.contract.customerAccount.id, br.contract.customer.id, br.contract.seller.id, br.priority NULLS LAST"),
    @NamedQuery(name = "BillingRule.findByAccountsAndContractWithSellerNull", query = "select br from BillingRule br where "
            + " br.contract.id=:contractId "
            + " and (br.contract.billingAccount.id is null or br.contract.billingAccount.id=:billingAccountId) "
            + " and (br.contract.customerAccount.id is null or br.contract.customerAccount.id=:customerAccountId) "
            + " and (br.contract.customer.id is null or br.contract.customer.id=:customerId) "
            + " and (br.contract.seller.id is null) "
            + " order by br.contract.billingAccount.id, br.contract.customerAccount.id, br.contract.customer.id, br.contract.seller.id, br.priority NULLS LAST"),
    @NamedQuery(name = "BillingRule.findByContractIdForRating", query = "select id, criteriaEL, invoicedBACodeEL from BillingRule where contract.id=:contractId order by priority NULLS LAST", hints = {
            @QueryHint(name = "org.hibernate.cacheable", value = "TRUE") }) })
public class BillingRule extends EnableEntity {

	private static final long serialVersionUID = 1727135182839389638L;

	@ManyToOne
    @JoinColumn(name = "contract_id")
	private Contract contract;
	
	@Column(name = "priority")
	private Integer priority;

	@Column(name = "criteria_el")
	private String criteriaEL;

	@Column(name = "invoice_ba_code_el")
	private String invoicedBACodeEL;

	public Contract getContract() {
		return contract;
	}

	public void setContract(Contract contract) {
		this.contract = contract;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public String getCriteriaEL() {
		return criteriaEL;
	}

	public void setCriteriaEL(String criteriaEL) {
		this.criteriaEL = criteriaEL;
	}

	public String getInvoicedBACodeEL() {
		return invoicedBACodeEL;
	}

	public void setInvoicedBACodeEL(String invoicedBACodeEL) {
		this.invoicedBACodeEL = invoicedBACodeEL;
	}
}
