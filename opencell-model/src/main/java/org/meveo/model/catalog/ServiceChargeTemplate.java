package org.meveo.model.catalog;

import java.util.List;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import org.meveo.model.BaseEntity;

/**
 * Service template to charge template mapping
 * 
 * @author Andrius Karpavicius
 *
 * @param <T> Charge template type
 */
@MappedSuperclass
public abstract class ServiceChargeTemplate<T extends ChargeTemplate> extends BaseEntity {

    private static final long serialVersionUID = -1872859127097329926L;

    /**
     * Service template (identifier)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_template_id")
    protected ServiceTemplate serviceTemplate;

    /**
     * Charge template (identifier)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charge_template_id")
    protected T chargeTemplate;

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
