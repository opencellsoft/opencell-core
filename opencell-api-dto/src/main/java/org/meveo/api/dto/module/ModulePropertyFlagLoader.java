package org.meveo.api.dto.module;

/**
 * @author Edward P. Legaspi
 * @version %I%, %G%
 * @since 4 Apr 2018
 **/
public class ModulePropertyFlagLoader {

    private boolean loadOfferServiceTemplate = true;
    private boolean loadOfferProductTemplate = true;
    private boolean loadServiceChargeTemplate = true;
    private boolean loadProductChargeTemplate = true;

    public boolean isLoadOfferServiceTemplate() {
        return loadOfferServiceTemplate;
    }

    public void setLoadOfferServiceTemplate(boolean loadOfferServiceTemplate) {
        this.loadOfferServiceTemplate = loadOfferServiceTemplate;
    }

    public boolean isLoadOfferProductTemplate() {
        return loadOfferProductTemplate;
    }

    public void setLoadOfferProductTemplate(boolean loadOfferProductTemplate) {
        this.loadOfferProductTemplate = loadOfferProductTemplate;
    }

    public boolean isLoadServiceChargeTemplate() {
        return loadServiceChargeTemplate;
    }

    public void setLoadServiceChargeTemplate(boolean loadServiceChargeTemplate) {
        this.loadServiceChargeTemplate = loadServiceChargeTemplate;
    }

    public boolean isLoadProductChargeTemplate() {
        return loadProductChargeTemplate;
    }

    public void setLoadProductChargeTemplate(boolean loadProductChargeTemplate) {
        this.loadProductChargeTemplate = loadProductChargeTemplate;
    }

}
