package org.meveo.model.catalog;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.meveo.model.ObservableEntity;
import org.meveo.model.module.MeveoModule;
import org.meveo.model.scripts.ServiceModelScript;

@Entity
@ObservableEntity
@Table(name = "CAT_BUSINESS_SERV_MODEL")
public class BusinessServiceModel extends MeveoModule {

    private static final long serialVersionUID = 683873220792653929L;

    @ManyToOne
    @JoinColumn(name = "SERVICE_TEMPLATE_ID")
    private ServiceTemplate serviceTemplate;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "SCRIPT_INSTANCE_ID")
    private ServiceModelScript script;

    @Column(name = "DUPLICATE_SERVICE")
    private boolean duplicateService;

    @Column(name = "DUPLICATE_PRICE_PLAN")
    private boolean duplicatePricePlan;

    public ServiceTemplate getServiceTemplate() {
        return serviceTemplate;
    }

    public void setServiceTemplate(ServiceTemplate serviceTemplate) {
        this.serviceTemplate = serviceTemplate;
    }

    public ServiceModelScript getScript() {
        return script;
    }

    public void setScript(ServiceModelScript script) {
        this.script = script;
    }

    public boolean isDuplicateService() {
        return duplicateService;
    }

    public void setDuplicateService(boolean duplicateService) {
        this.duplicateService = duplicateService;
    }

    public boolean isDuplicatePricePlan() {
        return duplicatePricePlan;
    }

    public void setDuplicatePricePlan(boolean duplicatePricePlan) {
        this.duplicatePricePlan = duplicatePricePlan;
    }

}
