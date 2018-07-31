package org.meveo.model.communication.postalmail;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BaseEntity;
import org.meveo.model.IEntity;
import org.meveo.model.crm.Provider;

/**
 * Holds the duration of how many years specific data should be kept. Work
 * together with a job. 6 The job runs every end of the day to test the database
 * with the given criteria.
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

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "provider_id")
	private Provider provider;

	@Column(name = "inactive_subscription_life", columnDefinition = "int default 5")
	private int inactiveSubscriptionLife = 5;

	@Column(name = "inactive_order_life", columnDefinition = "int default 10")
	private int inactiveOrderLife = 10;

	@Column(name = "invoice_life", columnDefinition = "int default 10")
	private int invoiceLife = 10;

	@Column(name = "accounting_life", columnDefinition = "int default 10")
	private int accountingLife = 10;

	@Column(name = "customer_profile_life", columnDefinition = "int default 3")
	private int customerProspectLife = 3;

	@Column(name = "mailing_life", columnDefinition = "int default 3")
	private int mailingLife = 3;

	@Column(name = "ao_check_unpaid_life", columnDefinition = "int default 3")
	private int aoCheckUnpaidLife = 3;

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

}
