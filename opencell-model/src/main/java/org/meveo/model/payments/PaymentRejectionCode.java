package org.meveo.model.payments;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ModuleItem;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Map;

@Entity
@ModuleItem
@CustomFieldEntity(cftCodePrefix = "PaymentRejectionCode")
@Table(name = "ar_payment_rejection_code")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "ar_payment_rejection_code_seq"), })
@NamedQueries({
        @NamedQuery(name = "PaymentRejectionCode.findByCodeAndPaymentGateway", query = "SELECT rc from PaymentRejectionCode rc where rc.code = :code and rc.paymentGateway.id = :paymentGatewayId"),
        @NamedQuery(name = "PaymentRejectionCode.clearAllByPaymentGateway", query = "DELETE from PaymentRejectionCode rc where rc.paymentGateway.id = :paymentGatewayId"),
        @NamedQuery(name = "PaymentRejectionCode.clearAll", query = "DELETE from PaymentRejectionCode"),
        @NamedQuery(name = "PaymentRejectionCode.findByPaymentGateway", query = "SELECT rc from PaymentRejectionCode rc where rc.paymentGateway.id = :paymentGatewayId")
})
public class PaymentRejectionCode extends BusinessCFEntity {

    /**
     * PaymentGateway associated to the rejection code.
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_gateway_id", nullable = false, unique = true)
    private PaymentGateway paymentGateway;

    /**
     * Translated descriptions in JSON format with language code as a key and translated description as a value
     **/
    @Type(type = "json")
    @Column(name = "description_i18n", columnDefinition = "jsonb")
    private Map<String, String> descriptionI18n;

    public PaymentGateway getPaymentGateway() {
        return paymentGateway;
    }

    public void setPaymentGateway(PaymentGateway paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    public Map<String, String> getDescriptionI18n() {
        return descriptionI18n;
    }

    public void setDescriptionI18n(Map<String, String> descriptionI18n) {
        this.descriptionI18n = descriptionI18n;
    }
}
