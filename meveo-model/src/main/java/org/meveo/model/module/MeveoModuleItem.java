package org.meveo.model.module;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BaseProviderlessEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;

@Entity
@ExportIdentifier({ "meveoModule.code", "meveoModule.provider", "appliesTo", "itemClass", "itemCode" })
@Table(name = "MEVEO_MODULE_ITEM")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "MEVEO_MODULE_ITEM_SEQ")
public class MeveoModuleItem extends BaseProviderlessEntity {

    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "MODULE_ID")
    private MeveoModule meveoModule;

    @Column(name = "APPLIES_TO", length = 100)
    @Size(max = 100)
    private String appliesTo;

    @Column(name = "ITEM_TYPE", length = 100, nullable = false)
    @Size(max = 100)
    @NotNull
    private String itemClass;

    @Column(name = "ITEM_CODE", length = 60, nullable = false)
    @Size(max = 60)
    @NotNull
    private String itemCode;

    @Transient
    private BusinessEntity itemEntity;

    public MeveoModuleItem() {
    }

    public MeveoModuleItem(BusinessEntity itemEntity) {
        this.itemEntity = itemEntity;
        this.itemClass = itemEntity.getClass().getName();
        this.itemCode = itemEntity.getCode();
        if (ReflectionUtils.hasField(itemEntity, "appliesTo")) {
            try {
                this.appliesTo = (String) FieldUtils.readField(itemEntity, "appliesTo", true);
            } catch (IllegalAccessException e) {
            }
        }
    }

    public MeveoModuleItem(String itemCode, String itemClass, String appliesTo) {
        this.itemClass = itemClass;
        this.itemCode = itemCode;
        this.appliesTo = appliesTo;
    }

    public MeveoModule getMeveoModule() {
        return meveoModule;
    }

    public void setMeveoModule(MeveoModule meveoModule) {
        this.meveoModule = meveoModule;
    }

    public String getItemClass() {
        return itemClass;
    }

    public void setItemClass(String itemClass) {
        this.itemClass = itemClass;
    }
    
    public String getItemClassSimpleName() {
        if (itemClass != null) {
            return itemClass.substring(itemClass.lastIndexOf('.') + 1);
        }
        return null;
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
        result += itemClass != null ? itemClass.hashCode() : 0;
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

        if (!itemClass.equals(other.getItemClass()) || !itemCode.equalsIgnoreCase(other.getItemCode()) || StringUtils.compare(appliesTo, other.getAppliesTo()) != 0) {
            return false;
        }
        return true;
    }

    public BusinessEntity getItemEntity() {
        return itemEntity;
    }

    public void setItemEntity(BusinessEntity itemEntity) {
        this.itemEntity = itemEntity;
    }

    @Override
    public String toString() {
        return String.format("MeveoModuleItem [itemClass=%s, itemCode=%s, appliesTo=%s]", itemClass, itemCode, appliesTo);
    }
}