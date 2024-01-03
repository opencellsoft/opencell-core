package org.meveo.model.payments;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ModuleItem;
import org.meveo.model.scripts.ScriptInstance;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@ModuleItem
@CustomFieldEntity(cftCodePrefix = "PaymentRejectionAction")
@Table(name = "ar_payment_rejection_action")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {@Parameter(name = "sequence_name", value = "ar_payment_rejection_action_seq"),})
public class PaymentRejectionAction extends BusinessCFEntity {

    /**
     * Action sequence
     */
    @Column(name = "sequence")
    private int sequence;

    /**
     * Script instance associated to rejection action.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "script_instance_id")
    private ScriptInstance script;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_rejection_codes_group_id")
    private PaymentRejectionCodesGroup paymentRejectionCodesGroup;

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public ScriptInstance getScript() {
        return script;
    }

    public void setScript(ScriptInstance script) {
        this.script = script;
    }

    public PaymentRejectionCodesGroup getPaymentRejectionCodesGroup() {
        return paymentRejectionCodesGroup;
    }

    public void setPaymentRejectionCodesGroup(PaymentRejectionCodesGroup paymentRejectionCodesGroup) {
        this.paymentRejectionCodesGroup = paymentRejectionCodesGroup;
    }
}
