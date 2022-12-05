package org.meveo.model.settings;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.type.NumericBooleanConverter;
import org.meveo.model.AuditableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "global_settings")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "global_settings_seq"), })
public class GlobalSettings extends AuditableEntity {

    private static final long serialVersionUID = 7097186153182628716L;

    @Column(name = "quote_default_validity_delay", nullable = false)
    private Integer quoteDefaultValidityDelay;

    /**
     * @deprecated in 14.0.0 for not use
     */
    @Deprecated
    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "activate_dunning")
    private Boolean activateDunning = Boolean.FALSE;

    public GlobalSettings() {
        super();
    }

    /**
     * @return the quoteDefaultValidityDelay
     */
    public Integer getQuoteDefaultValidityDelay() {
        return quoteDefaultValidityDelay;
    }

    /**
     * @param quoteDefaultValidityDelay the quoteDefaultValidityDelay to set
     */
    public void setQuoteDefaultValidityDelay(Integer quoteDefaultValidityDelay) {
        this.quoteDefaultValidityDelay = quoteDefaultValidityDelay;
    }

    public Boolean getActivateDunning() {
        return activateDunning;
    }

    public void setActivateDunning(Boolean activateDunning) {
        this.activateDunning = activateDunning;
    }
}
