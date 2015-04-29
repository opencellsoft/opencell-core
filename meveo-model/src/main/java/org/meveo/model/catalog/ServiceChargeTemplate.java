package org.meveo.model.catalog;

import java.util.List;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import org.meveo.model.BaseEntity;

@MappedSuperclass
public abstract class ServiceChargeTemplate<T extends ChargeTemplate> extends BaseEntity {

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

	public abstract List<WalletTemplate> getWalletTemplates();

	public abstract void setWalletTemplates(List<WalletTemplate> walletTemplates);
	

}
