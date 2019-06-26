package org.meveo.model.payments;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value = "OTP")
public class OtherTransactionPayment extends OtherTransaction {

    private static final long serialVersionUID = 1L;

}
