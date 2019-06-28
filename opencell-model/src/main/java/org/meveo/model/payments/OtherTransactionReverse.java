package org.meveo.model.payments;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value = "OTR")
public class OtherTransactionReverse extends OtherTransaction {

    private static final long serialVersionUID = 1L;

}
