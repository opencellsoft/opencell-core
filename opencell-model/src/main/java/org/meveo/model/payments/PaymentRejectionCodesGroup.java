package org.meveo.model.payments;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ModuleItem;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@ModuleItem
@CustomFieldEntity(cftCodePrefix = "PaymentRejectionCodesGroup")
@Table(name = "ar_payment_rejection_codes_group")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {@Parameter(name = "sequence_name", value = "ar_payment_rejection_codes_group_seq"),})
public class PaymentRejectionCodesGroup extends BusinessCFEntity {

    /**
     * PaymentGateway associated to the rejection code.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_gateway_id")
    private PaymentGateway paymentGateway;

    /**
     * Associated rejection codes.
     */
    @OneToMany(mappedBy = "paymentRejectionCodesGroup", fetch = FetchType.LAZY)
    private List<PaymentRejectionCode> paymentRejectionCodes;

    /**
     * Associated rejection actions.
     */
    @OneToMany(mappedBy = "paymentRejectionCodesGroup", fetch = FetchType.LAZY)
    private List<PaymentRejectionAction> paymentRejectionActions;

    public PaymentGateway getPaymentGateway() {
        return paymentGateway;
    }

    public void setPaymentGateway(PaymentGateway paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    public List<PaymentRejectionCode> getPaymentRejectionCodes() {
        return paymentRejectionCodes;
    }

    public void setPaymentRejectionCodes(List<PaymentRejectionCode> paymentRejectionCodes) {
        this.paymentRejectionCodes = paymentRejectionCodes;
    }

    public List<PaymentRejectionAction> getPaymentRejectionActions() {
        return paymentRejectionActions;
    }

    public void setPaymentRejectionActions(List<PaymentRejectionAction> paymentRejectionActions) {
        this.paymentRejectionActions = paymentRejectionActions;
    }
}
