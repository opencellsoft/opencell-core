
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
 * @author Tarik F.
 * @version 10.0
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "cpq_attribute_type", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_attribute_type_seq"), })
public class AttributeType extends BusinessEntity{

	@Enumerated(EnumType.STRING)
	@Column(name = "service_type", nullable = false, length = 100)
	private ServiceTypeEnum serviceType;

	/**
	 * @return the serviceType
	 */
	public ServiceTypeEnum getServiceType() {
		return serviceType;
	}

	/**
	 * @param serviceType the serviceType to set
	 */
	public void setServiceType(ServiceTypeEnum serviceType) {
		this.serviceType = serviceType;
	}
	
	
	
}

