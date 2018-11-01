package org.meveo.model.dwh;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BaseEntity;
import org.meveo.model.IEntity;
import org.meveo.model.crm.Provider;

/**
 * Holds the duration of how many years specific data should be kept. Work together with a job. The job runs every end of the day to test the database with the given criteria.
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.2
 **/
@Entity
@Table(name = "adm_gdpr_configuration")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", //
        parameters = { @Parameter(name = "sequence_name", value = "adm_gdpr_configuration_seq"), })
public class GdprConfiguration extends BaseEntity implements Serializable, IEntity {

    private static final long serialVersionUID = -207809406272424682L;

    /**
     * Provider/tenant that configuration is associated with
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id")
    private Provider provider;

    /**
     * Lifetime of inactive subscription
     */
    @Column(name = "inactive_subscription_life", columnDefinition = "int default 5")
    private int inactiveSubscriptionLife = 5;

    /**
     * Lifetime of inactive order
     */
    @Column(name = "inactive_order_life", columnDefinition = "int default 10")
    private int inactiveOrderLife = 10;

    /**
     * Lifetime of invoice
     */
    @Column(name = "invoice_life", columnDefinition = "int default 10")
    private int invoiceLife = 10;

    /**
     * Lifetime of account operations
     */
    @Column(name = "accounting_life", columnDefinition = "int default 10")
    private int accountingLife = 10;

    /**
     * Lifetime of customer prospect
     */
    @Column(name = "customer_profile_life", columnDefinition = "int default 3")
    private int customerProspectLife = 3;

    /**
     * Lifetime of communication
     */
    @Column(name = "mailing_life", columnDefinition = "int default 3")
    private int mailingLife = 3;

    /**
     * Lifetime of unpaid Account operation
     */
    @Column(name = "ao_check_unpaid_life", columnDefinition = "int default 3")
    private int aoCheckUnpaidLife = 3;

    /**
     * Should subscriptions be deleted
     */
    @Type(type = "numeric_boolean")
    @Column(name = "delete_sub")
    private boolean deleteSubscription = false;

    /**
     * Should orders be deleted
     */
    @Type(type = "numeric_boolean")
    @Column(name = "delete_order")
    private boolean deleteOrder = false;

    /**
     * Should invoices be deleted
     */
    @Type(type = "numeric_boolean")
    @Column(name = "delete_invoice")
    private boolean deleteInvoice = false;

    /**
     * Should account operations be deleted
     */
    @Type(type = "numeric_boolean")
    @Column(name = "delete_accounting")
    private boolean deleteAccounting = false;

    /**
     * Should customer prospects be deleted
     */
    @Type(type = "numeric_boolean")
    @Column(name = "delete_cust_prospect")
    private boolean deleteCustomerProspect = false;

    /**
     * Should communicatio be deleted
     */
    @Type(type = "numeric_boolean")
    @Column(name = "delete_mailing_life")
    private boolean deleteMailingLife = false;

    /**
     * Should unpaid Account operations be deleted
     */
    @Type(type = "numeric_boolean")
    @Column(name = "delete_ao_check_unpaid")
    private boolean deleteAoCheckUnpaidLife = false;

    public int getInactiveSubscriptionLife() {
        return inactiveSubscriptionLife;
    }

    public void setInactiveSubscriptionLife(int inactiveSubscriptionLife) {
        this.inactiveSubscriptionLife = inactiveSubscriptionLife;
    }

    public int getInactiveOrderLife() {
        return inactiveOrderLife;
    }

    public void setInactiveOrderLife(int inactiveOrderLife) {
        this.inactiveOrderLife = inactiveOrderLife;
    }

    public int getInvoiceLife() {
        return invoiceLife;
    }

    public void setInvoiceLife(int invoiceLife) {
        this.invoiceLife = invoiceLife;
    }

    public int getAccountingLife() {
        return accountingLife;
    }

    public void setAccountingLife(int accountingLife) {
        this.accountingLife = accountingLife;
    }

    public int getCustomerProspectLife() {
        return customerProspectLife;
    }

    public void setCustomerProspectLife(int customerProspectLife) {
        this.customerProspectLife = customerProspectLife;
    }

    public int getMailingLife() {
        return mailingLife;
    }

    public void setMailingLife(int mailingLife) {
        this.mailingLife = mailingLife;
    }

    public int getAoCheckUnpaidLife() {
        return aoCheckUnpaidLife;
    }

    public void setAoCheckUnpaidLife(int aoCheckUnpaidLife) {
        this.aoCheckUnpaidLife = aoCheckUnpaidLife;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public boolean isDeleteSubscription() {
        return deleteSubscription;
    }

    public void setDeleteSubscription(boolean deleteSubscription) {
        this.deleteSubscription = deleteSubscription;
    }

    public boolean isDeleteOrder() {
        return deleteOrder;
    }

    public void setDeleteOrder(boolean deleteOrder) {
        this.deleteOrder = deleteOrder;
    }

    public boolean isDeleteInvoice() {
        return deleteInvoice;
    }

    public void setDeleteInvoice(boolean deleteInvoice) {
        this.deleteInvoice = deleteInvoice;
    }

    public boolean isDeleteAccounting() {
        return deleteAccounting;
    }

    public void setDeleteAccounting(boolean deleteAccounting) {
        this.deleteAccounting = deleteAccounting;
    }

    public boolean isDeleteCustomerProspect() {
        return deleteCustomerProspect;
    }

    public void setDeleteCustomerProspect(boolean deleteCustomerProspect) {
        this.deleteCustomerProspect = deleteCustomerProspect;
    }

    public boolean isDeleteMailingLife() {
        return deleteMailingLife;
    }

    public void setDeleteMailingLife(boolean deleteMailingLife) {
        this.deleteMailingLife = deleteMailingLife;
    }

    public boolean isDeleteAoCheckUnpaidLife() {
        return deleteAoCheckUnpaidLife;
    }

    public void setDeleteAoCheckUnpaidLife(boolean deleteAoCheckUnpaidLife) {
        this.deleteAoCheckUnpaidLife = deleteAoCheckUnpaidLife;
    }

}
