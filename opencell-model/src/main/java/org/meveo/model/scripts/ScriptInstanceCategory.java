package org.meveo.model.scripts;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.1
 */
@Entity
@Table(name = "meveo_script_instance_cat", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
		@Parameter(name = "sequence_name", value = "meveo_script_instance_cat_seq"), })
public class ScriptInstanceCategory extends BusinessEntity {

	private static final long serialVersionUID = 3368033230915325843L;

}
