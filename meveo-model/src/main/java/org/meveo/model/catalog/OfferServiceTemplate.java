package org.meveo.model.catalog;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.meveo.model.BaseEntity;
import org.meveo.model.ExportIdentifier;

@Entity
@ExportIdentifier({ "offerTemplate.code", "serviceTemplate.code", "provider" })
@Table(name = "CAT_OFFER_SERVICE_TEMPLATE")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CAT_OFFER_SERV_TEMPLT_SEQ")
public class OfferServiceTemplate extends BaseEntity {

	private static final long serialVersionUID = -1872859127097329926L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OFFER_TEMPLATE_ID")
	private OfferTemplate offerTemplate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SERVICE_TEMPLATE_ID")
	private ServiceTemplate serviceTemplate;

	@Column(name = "MANDATORY")
	private boolean mandatory;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "CAT_OFFER_SERV_INCOMP", joinColumns = @JoinColumn(name = "OFFER_TEMPLATE_ID"), inverseJoinColumns = @JoinColumn(name = "OFFER_SERVICE_TEMPLATE_ID"))
	private List<ServiceTemplate> incompatibleServices = new ArrayList<>();

	public OfferTemplate getOfferTemplate() {
		return offerTemplate;
	}

	public void setOfferTemplate(OfferTemplate offerTemplate) {
		this.offerTemplate = offerTemplate;
	}

	public ServiceTemplate getServiceTemplate() {
		return serviceTemplate;
	}

	public void setServiceTemplate(ServiceTemplate serviceTemplate) {
		this.serviceTemplate = serviceTemplate;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	public List<ServiceTemplate> getIncompatibleServices() {
		return incompatibleServices;
	}

	public void setIncompatibleServices(List<ServiceTemplate> incompatibleServices) {
		this.incompatibleServices = incompatibleServices;
	}

	public void addIncompatibleServiceTemplate(ServiceTemplate serviceTemplate) {
		if (getIncompatibleServices() == null) {
			incompatibleServices = new ArrayList<ServiceTemplate>();
		}
		incompatibleServices.add(serviceTemplate);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((incompatibleServices == null) ? 0 : incompatibleServices.hashCode());
		result = prime * result + ((offerTemplate == null) ? 0 : offerTemplate.hashCode());
		result = prime * result + ((serviceTemplate == null) ? 0 : serviceTemplate.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;

		OfferServiceTemplate e = (OfferServiceTemplate) obj;
		if (offerTemplate == null || serviceTemplate == null || e.getOfferTemplate() == null
				|| e.getServiceTemplate() == null)
			return false;

		if (offerTemplate.getId() != e.getOfferTemplate().getId()
				|| serviceTemplate.getId() != e.getServiceTemplate().getId())
			return false;

		return true;
	}

}
