/**
 *
 */
package org.meveo.model.payments;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
/**
 * @author anasseh
 *
 */
@Entity
@DiscriminatorValue(value = "ARF")
public class AutomatedRefund  extends AutomatedPayment{
    /**
     *
     */
    private static final long serialVersionUID = -8295409359854665764L;
}