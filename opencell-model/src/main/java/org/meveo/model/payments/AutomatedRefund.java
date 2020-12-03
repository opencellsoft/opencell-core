/**
 * 
 */
package org.meveo.model.payments;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

/**
 * @author anasseh
 *
 */

@Entity
@DiscriminatorValue(value = "ARF")
public class AutomatedRefund extends Refund {
    /**
     * 
     */
    private static final long serialVersionUID = -8295409009854665764L;
}

