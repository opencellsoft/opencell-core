package org.meveo.model.cpq;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.cpq.enums.ServiceTypeEnum;

/**
 * The type of service will make it possible to define the functioning of a service linked to a product. 
 * Does this service require you to enter a value (an email for example) or to select a value from a predefined list, 
 * And is it just an information service without any entry obligation or a service carrying one or more charges.
 * @author Tarik F.
 * @version 10.0
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "cpq_service_type", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_service_type_seq"), })
public class ServiceType extends BusinessEntity{

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "service_type", nullable = false, length = 100)
	private ServiceTypeEnum serviceType;
}
