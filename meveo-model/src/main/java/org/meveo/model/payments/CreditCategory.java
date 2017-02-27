package org.meveo.model.payments;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;

/**
 * @author Edward P. Legaspi
 **/
@Entity
@ExportIdentifier({ "code"})
@Table(name = "AR_CREDIT_CATEGORY", uniqueConstraints = @UniqueConstraint(columnNames = { "CODE"}))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {@Parameter(name = "sequence_name", value = "AR_CREDIT_CATEGORY_SEQ"), })
public class CreditCategory extends BusinessEntity {

	private static final long serialVersionUID = 3517517101499066805L;

}
