package org.meveo.model.catalog;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ObservableEntity;
import org.meveo.model.communication.MeveoInstance;

@Entity
@ObservableEntity
@Cacheable
@ExportIdentifier({ "code" })
@Table(name = "cat_triggered_edr", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cat_triggered_edr_seq"), })
public class TriggeredEDRTemplate extends BusinessEntity {
    private static final long serialVersionUID = 1L;

    @Column(name = "subscription_el", length = 2000)
    @Size(max = 2000)
    private String subscriptionEl;

    @ManyToOne
    @JoinColumn(name = "meveo_instance_id")
    private MeveoInstance meveoInstance;

    @Column(name = "condition_el", length = 2000)
    @Size(max = 2000)
    private String conditionEl;

    @Column(name = "quantity_el", length = 2000)
    @Size(max = 2000)
    private String quantityEl;

    @Column(name = "param_1_el", length = 2000)
    @Size(max = 2000)
    private String param1El;

    @Column(name = "param_2_el", length = 2000)
    @Size(max = 2000)
    private String param2El;

    @Column(name = "param_3_el", length = 2000)
    @Size(max = 2000)
    private String param3El;

    @Column(name = "param_4_el", length = 2000)
    @Size(max = 2000)
    private String param4El;

    public String getSubscriptionEl() {
        return subscriptionEl;
    }

    public void setSubscriptionEl(String subscriptionEl) {
        this.subscriptionEl = subscriptionEl;
    }

    public MeveoInstance getMeveoInstance() {
        return meveoInstance;
    }

    public void setMeveoInstance(MeveoInstance meveoInstance) {
        this.meveoInstance = meveoInstance;
    }

    public String getConditionEl() {
        return conditionEl;
    }

    public void setConditionEl(String conditionEl) {
        this.conditionEl = conditionEl;
    }

    public String getQuantityEl() {
        return quantityEl;
    }

    public void setQuantityEl(String quantityEl) {
        this.quantityEl = quantityEl;
    }

    public String getParam1El() {
        return param1El;
    }

    public void setParam1El(String param1El) {
        this.param1El = param1El;
    }

    public String getParam2El() {
        return param2El;
    }

    public void setParam2El(String param2El) {
        this.param2El = param2El;
    }

    public String getParam3El() {
        return param3El;
    }

    public void setParam3El(String param3El) {
        this.param3El = param3El;
    }

    public String getParam4El() {
        return param4El;
    }

    public void setParam4El(String param4El) {
        this.param4El = param4El;
    }

}
