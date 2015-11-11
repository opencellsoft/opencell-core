package org.meveo.model.admin;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.meveo.model.BaseEntity;

@Entity
@Table(name = "MEVEO_MODULE_ITEM")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "MEVEO_MODULE_ITEM_SEQ")
public class MeveoModuleItem extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@ManyToOne
	@JoinColumn(name = "MODULE_ID")
	private MeveoModule meveoModule;
	@Column(name = "ENTITY_NAME")
	private String entityName;
	@Column(name = "CLAZZ_NAME")
	private String clazzName;
	@Enumerated(EnumType.STRING)
	@Column(name = "ITEM_TYPE",length=20)
	private ModuleItemTypeEnum itemType;
	@Column(name = "ITEM_ID")
	private Long itemId;
	@Column(name = "ITEM_CODE", length = 100)
	private String itemCode;

	public MeveoModuleItem() {
	}

	public MeveoModuleItem(String entityName,String clazzName,ModuleItemTypeEnum itemType, String itemCode, Long itemId) {
		this.entityName=entityName;
		this.clazzName = clazzName;
		this.itemType = itemType;
		this.itemCode = itemCode;
		this.itemId = itemId;
	}

//	public MeveoModuleItem(String entityName, ModuleItemTypeEnum itemType) {
//		this.entityName = entityName;
//		this.itemType = itemType;
//	}

	public MeveoModule getMeveoModule() {
		return meveoModule;
	}

	public void setMeveoModule(MeveoModule meveoModule) {
		this.meveoModule = meveoModule;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public ModuleItemTypeEnum getItemType() {
		return itemType;
	}

	public void setItemType(ModuleItemTypeEnum itemType) {
		this.itemType = itemType;
	}

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public String getClazzName() {
		return clazzName;
	}

	public void setClazzName(String clazzName) {
		this.clazzName = clazzName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((itemId == null) ? 0 : itemId.hashCode());
		if (ModuleItemTypeEnum.CET.equals(itemType)) {
			result = ((itemCode == null) ? 0 : itemCode.hashCode());
		}
		if (ModuleItemTypeEnum.CFT.equals(itemType)) {
			result += (clazzName == null ? 0 : clazzName.hashCode());
		}
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		} else if (!(obj instanceof MeveoModuleItem)) {
			return false;
		}

		MeveoModuleItem other = (MeveoModuleItem) obj;

		if (getId() != null && other.getId() != null && getId() == other.getId()) {
			return true;
		}

		if (ModuleItemTypeEnum.CET.equals(itemType)) {
			if (itemCode != null && itemCode.equals(other.getItemCode())) {
				return true;
			}
		}
		if (ModuleItemTypeEnum.CFT.equals(itemType)) {
			if (entityName != null && entityName.equals(other.getEntityName())) {
				return true;
			}
		}

		// if (itemId == null) {
		// if (other.getItemId() != null) {
		// return false;
		// }
		// } else if (!itemId.equals(other.getItemId())) {
		// return false;
		// }
		return false;
	}

}
