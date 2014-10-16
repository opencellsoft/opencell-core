package org.meveo.model.catalog;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.meveo.model.BaseEntity;

@Entity
@Table(name = "CAT_SERV_USAGE_CHARGE_TEMPLATE")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CAT_SERV_CHRG_TEMPLT_SEQ")
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public class ServiceChargeTemplate<T extends ChargeTemplate> extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1872859127097329926L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SERVICE_TEMPLATE_ID")
	private ServiceTemplate serviceTemplate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CHARGE_TEMPLATE_ID")
	private T chargeTemplate;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "CAT_SERVCHR_WALLET_TEMPLATE", joinColumns = @JoinColumn(name = "SERVICE_CHRG_TEMPLT_ID"), inverseJoinColumns = @JoinColumn(name = "WALLET_TEMPLATE_ID"))
	@OrderColumn(name="INDEX")
	private List<WalletTemplate> walletTemplates;
	
	public ServiceTemplate getServiceTemplate() {
		return serviceTemplate;
	}

	public void setServiceTemplate(ServiceTemplate serviceTemplate) {
		this.serviceTemplate = serviceTemplate;
	}

	public T getChargeTemplate() {
		return chargeTemplate;
	}

	public void setChargeTemplate(T chargeTemplate) {
		this.chargeTemplate = chargeTemplate;
	}

	public List<WalletTemplate> getWalletTemplates() {
		return walletTemplates;
	}

	public void setWalletTemplates(List<WalletTemplate> walletTemplates) {
		this.walletTemplates = walletTemplates;
	}
	

}
