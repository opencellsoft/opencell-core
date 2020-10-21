package org.meveo.model.cpq;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;

/** 
 * @author Mbarek-Ay
 * @version 11.0
 *
 */
@Entity
@Table(name = "cpq_accounting_article_mapping", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_accounting_article_seq"), })
public class AccountingArticle extends BusinessEntity {
 
	private static final long serialVersionUID = 1L;

	
	 

 



}
