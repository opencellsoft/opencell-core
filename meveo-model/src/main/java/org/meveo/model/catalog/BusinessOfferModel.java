package org.meveo.model.catalog;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.meveo.model.module.MeveoModule;

@Entity
@Table(name = "CAT_BUSINESS_OFFER_MODEL")
public class BusinessOfferModel extends MeveoModule {

    private static final long serialVersionUID = 683873220792653929L;

    @ManyToOne
    @JoinColumn(name = "OFFER_TEMPLATE_ID")
    private OfferTemplate offerTemplate;

    public OfferTemplate getOfferTemplate() {
        return offerTemplate;
    }

    public void setOfferTemplate(OfferTemplate offerTemplate) {
        this.offerTemplate = offerTemplate;
    }
}