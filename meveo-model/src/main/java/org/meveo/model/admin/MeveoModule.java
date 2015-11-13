package org.meveo.model.admin;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ObservableEntity;

/**
 * meveo module has CETs, CFTs, filters, scripts, jobs, notifications
 * 
 * @author Tyshan Shi(tyshanchn@manaty.net)
 *
 */

@Entity
@ObservableEntity
@ExportIdentifier({ "code", "provider" })
@Table(name = "MEVEO_MODULE")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "MEVEO_MODULE_SEQ")
public class MeveoModule extends BusinessEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@OneToMany(mappedBy="meveoModule",cascade={CascadeType.ALL},orphanRemoval=true,fetch=FetchType.EAGER)
	private Set<MeveoModuleItem> moduleItems=new HashSet<MeveoModuleItem>();
	public Set<MeveoModuleItem> getModuleItems() {
		return moduleItems;
	}
	public void setModuleItems(Set<MeveoModuleItem> moduleItems) {
		this.moduleItems = moduleItems;
	}
	public void addModuleItem(MeveoModuleItem moduleItem){
		this.moduleItems.add(moduleItem);
		moduleItem.setMeveoModule(this);
	}
	public void removeItem(MeveoModuleItem item) {
		this.moduleItems.remove(item);
		item.setMeveoModule(null);
	}
	
}
