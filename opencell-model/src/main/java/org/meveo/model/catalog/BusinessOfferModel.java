package org.meveo.model.catalog;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.meveo.model.module.MeveoModule;

@Entity
@Table(name = "cat_business_offer_model")
public class BusinessOfferModel extends MeveoModule {

    private static final long serialVersionUID = 683873220792653929L;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "offer_template_id")
    private OfferTemplate offerTemplate;

    public OfferTemplate getOfferTemplate() {
        return offerTemplate;
    }

    public void setOfferTemplate(OfferTemplate offerTemplate) {
        this.offerTemplate = offerTemplate;
    }
}