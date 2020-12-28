package org.meveo.model.cpq.commercial;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;


/** 
 * @author Tarik F.
 * @version 11.0
 *
 */
@Entity
@Table(name = "cpq_order_type", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_order_type_seq")})
public class OrderType extends BusinessEntity {

}
