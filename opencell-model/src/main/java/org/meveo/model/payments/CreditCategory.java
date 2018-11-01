package org.meveo.model.payments;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;

/**
 * Credit category
 * 
 * @author Edward P. Legaspi
 **/
@Entity
@Cacheable
@ExportIdentifier({ "code" })
@Table(name = "ar_credit_category", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "ar_credit_category_seq"), })
public class CreditCategory extends BusinessEntity {

    private static final long serialVersionUID = 3517517101499066805L;

}
