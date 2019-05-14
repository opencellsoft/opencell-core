package org.meveo.model.customEntities;

import java.io.Serializable;
import java.util.Map;

import org.meveo.model.IEntity;
import org.meveo.model.ISearchable;

public class CustomTableRecord implements Serializable, IEntity, ISearchable {

    private static final long serialVersionUID = 6342962203104643392L;

    /**
     * Identifier
     */
    private Long id;

    /**
     * Custom entity template code
     */
    private String cetCode;

    /**
     * Field values with field name as map key and field value as map value
     */
    private Map<String, Object> values;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return Custom entity template code
     */
    public String getCetCode() {
        return cetCode;
    }

    /**
     * @param cetCode Custom entity template code
     */
    public void setCetCode(String cetCode) {
        this.cetCode = cetCode;
    }

    /**
     * @return Field values with field name as map key and field value as map value
     */
    public Map<String, Object> getValues() {
        return values;
    }

    /**
     * @param values Field values with field name as map key and field value as map value
     */
    public void setValues(Map<String, Object> values) {
        this.values = values;
    }

    @Override
    public boolean isTransient() {
        return id == null;
    }

    @Override
    public String getCode() {
        return id != null ? id.toString() : null;
    }

    @Override
    public void setCode(String code) {

    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public void setDescription(String description) {
    }
}