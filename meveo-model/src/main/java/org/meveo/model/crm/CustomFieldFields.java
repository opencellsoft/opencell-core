package org.meveo.model.crm;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.meveo.model.BaseEntity;
import org.meveo.model.ExportIdentifier;

@Entity
@ExportIdentifier({ "uuid", "provider" })
@Table(name = "CRM_CUSTOM_FIELD_FIELDS", uniqueConstraints = @UniqueConstraint(columnNames = { "UUID", "PROVIDER_ID" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CRM_CUSTOM_FIELD_FIELDS_SEQ")
public class CustomFieldFields extends BaseEntity {

    private static final long serialVersionUID = 1053066986708284642L;

    @Column(name = "UUID", nullable = false, updatable = false)
    private String uuid = UUID.randomUUID().toString();

    @OneToMany(mappedBy = "cfFields", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @MapKeyColumn(name = "code")
    private Map<String, CustomFieldInstance> customFields = new HashMap<String, CustomFieldInstance>();

    public String getUuid() {
        return uuid;
    }

    public Map<String, CustomFieldInstance> getCustomFields() {
        return customFields;
    }

    public Object getCFValue(String cfCode) {
        if (customFields.containsKey(cfCode)) {
            return customFields.get(cfCode);
        }
        return null;
    }

    public Object getCFValue(String cfCode, Date date) {
        if (customFields.containsKey(cfCode)) {
            return customFields.get(cfCode).getValue(date);
        }
        return null;
    }

    public void setCFValue(String cfCode, Object value, CustomFieldTemplate cft) {

        CustomFieldInstance cfi = getCustomFields().get(cfCode);
        if (cfi == null) {
            if (value == null) {
                return;
            }
            if (cft != null) {
                cfi = CustomFieldInstance.fromTemplate(cft);
            } else {
                cfi = new CustomFieldInstance();
                cfi.setCode(cfCode);
            }
            cfi.setCfFields(this);

            this.getCustomFields().put(cfCode, cfi);
        }
        cfi.setValue(value);
    }

    public void setCFValue(String cfCode, Object value, Date valueDate, CustomFieldTemplate cft) {

        CustomFieldInstance cfi = getCustomFields().get(cfCode);
        if (cfi == null) {
            if (value == null) {
                return;
            }
            if (cft != null) {
                cfi = CustomFieldInstance.fromTemplate(cft);
            } else {
                // cfi = new CustomFieldInstance();
                // cfi.setCode(cfCode);
                // cfi.setVersionable(true);
                throw new RuntimeException("Can not determine a period for Custom Field value if no calendar is provided");
            }
            cfi.setCfFields(this);

            this.getCustomFields().put(cfCode, cfi);
        }
        cfi.setValue(value, valueDate);
    }

    public void setCFValue(String cfCode, Object value, Date valueDateFrom, Date valueDateTo, CustomFieldTemplate cft) {

        CustomFieldInstance cfi = getCustomFields().get(cfCode);
        if (cfi == null) {
            if (value == null) {
                return;
            }
            if (cft != null) {
                cfi = CustomFieldInstance.fromTemplate(cft);
            } else {
                cfi = new CustomFieldInstance();
                cfi.setCode(cfCode);
                cfi.setVersionable(true);
            }
            cfi.setCfFields(this);

            this.getCustomFields().put(cfCode, cfi);
        }
        cfi.setValue(value, valueDateFrom, valueDateTo);
    }

    /**
     * Get custom field instance by code
     * 
     * @param code Custom field code
     * @return
     */
    public CustomFieldInstance getCFI(String code) {
        CustomFieldInstance cfi = customFields.get(code);
        if (cfi != null && cfi.getCfValue() == null) {
            cfi.setCfValue(new CustomFieldValue());
        }

        return cfi;
    }

    /**
     * Add or update Custom field instance
     * 
     * @param cfi Custom field instance
     */
    public void addUpdateCFI(CustomFieldInstance cfi) {
        cfi.setCfFields(this);
        customFields.put(cfi.getCode(), cfi);
    }

    /**
     * Remove Custom field instance
     * 
     * @param code Custom field code to remove
     */
    public void removeCFI(String code) {
        customFields.remove(code);
    }

    public String asJson() {
        String result = "";
        String sep = "";

        for (Entry<String, CustomFieldInstance> cf : customFields.entrySet()) {
            result += sep + cf.getValue().toJson();
            sep = ";";
        }

        return result;
    }

    @Override
    public String toString() {
        final int maxLen = 10;
        return String.format("CustomField [id=%s, uuid=%s, customFields=%s]", id, uuid, customFields != null ? toString(customFields.entrySet(), maxLen) : null);
    }

    private String toString(Collection<?> collection, int maxLen) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        int i = 0;
        for (Iterator<?> iterator = collection.iterator(); iterator.hasNext() && i < maxLen; i++) {
            if (i > 0)
                builder.append(", ");
            builder.append(iterator.next());
        }
        builder.append("]");
        return builder.toString();
    }

    /**
     * Remove ids from entity and child entities, reconstruct new persistent collections
     */
    public void clearForDuplication() {
        id = null;
        uuid = UUID.randomUUID().toString();

        if (customFields != null) {
            Map<String, CustomFieldInstance> cfis = new HashMap<String, CustomFieldInstance>();

            for (CustomFieldInstance cfi : customFields.values()) {
                cfi.clearForDuplication();
                cfi.setCfFields(this);
                cfis.put(cfi.getCode(), cfi);
            }
            customFields = cfis;
        }
    }
}
