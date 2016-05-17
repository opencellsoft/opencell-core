package org.meveo.model.catalog;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.meveo.model.ObservableEntity;
import org.meveo.model.module.MeveoModule;
import org.meveo.model.scripts.OfferModelScript;

@Entity
@ObservableEntity
@Table(name = "CAT_BUSINESS_OFFER_MODEL")
public class BusinessOfferModel extends MeveoModule {

    private static final long serialVersionUID = 683873220792653929L;

    @ManyToOne
    @JoinColumn(name = "OFFER_TEMPLATE_ID")
    private OfferTemplate offerTemplate;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "SCRIPT_INSTANCE_ID")
    private OfferModelScript script;

    public OfferTemplate getOfferTemplate() {
        return offerTemplate;
    }

    public void setOfferTemplate(OfferTemplate offerTemplate) {
        this.offerTemplate = offerTemplate;
    }

    public OfferModelScript getScript() {
        return script;
    }

    public void setScript(OfferModelScript script) {
        this.script = script;
    }

}
