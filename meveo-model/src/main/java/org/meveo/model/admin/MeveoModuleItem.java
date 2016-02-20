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
import org.meveo.model.ExportIdentifier;

@Entity
@ExportIdentifier({ "meveoModule.code", "provider", "appliesTo", "itemType", "itemCode" })
@Table(name = "MEVEO_MODULE_ITEM")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "MEVEO_MODULE_ITEM_SEQ")
public class MeveoModuleItem extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "MODULE_ID")
    private MeveoModule meveoModule;

    @Column(name = "APPLIES_TO", length = 100)
    private String appliesTo;

    @Enumerated(EnumType.STRING)
    @Column(name = "ITEM_TYPE", length = 20, nullable = false)
    private ModuleItemTypeEnum itemType;

    @Column(name = "ITEM_CODE", length = 60, nullable = false)
    private String itemCode;

    public MeveoModuleItem() {
    }

    public MeveoModuleItem(String itemCode, ModuleItemTypeEnum itemType) {
        this.itemType = itemType;
        this.itemCode = itemCode;
    }

    public MeveoModuleItem(String itemCode, String applyTo, ModuleItemTypeEnum itemType) {
        this.itemCode = itemCode;
        this.appliesTo = applyTo;
        this.itemType = itemType;
    }

    public MeveoModule getMeveoModule() {
        return meveoModule;
    }

    public void setMeveoModule(MeveoModule meveoModule) {
        this.meveoModule = meveoModule;
    }

    public ModuleItemTypeEnum getItemType() {
        return itemType;
    }

    public void setItemType(ModuleItemTypeEnum itemType) {
        this.itemType = itemType;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getAppliesTo() {
        return appliesTo;
    }

    public void setAppliesTo(String applyTo) {
        this.appliesTo = applyTo;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = prime * 1;// super.hashCode();
        result += itemType != null ? itemType.hashCode() : 0;
        result += itemCode != null ? itemCode.hashCode() : 0;
        result += appliesTo != null ? appliesTo.hashCode() : 0;
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

        if (itemType == ModuleItemTypeEnum.CFT) {
            if (itemType.equals(other.getItemType()) && itemCode != null && itemCode.equalsIgnoreCase(other.getItemCode()) && appliesTo != null
                    && appliesTo.equalsIgnoreCase(other.getAppliesTo())) {
                return true;
            }
        } else if (itemType.equals(other.getItemType()) && itemCode != null && itemCode.equalsIgnoreCase(other.getItemCode())) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("MeveoModuleItem [itemType=%s, itemCode=%s, appliesTo=%s]", itemType, itemCode, appliesTo);
    }
}