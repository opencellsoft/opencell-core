package org.meveo.model.crm;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.validation.constraints.Size;

@Embeddable
public class CustomFieldMatrixColumn implements Serializable, Comparable<CustomFieldMatrixColumn> {

    private static final long serialVersionUID = 4307211518190785915L;

    private int position;

    // @Column(name = "CODE", nullable = false, length = 20)
    @Size(max = 20)
    private String code;

    // @Column(name = "LABEL", nullable = false, length = 50)
    @Size(max = 50)
    private String label;

    // @Column(name = "KEY_TYPE", nullable = false, length = 10)
    // @Enumerated(EnumType.STRING)
    private CustomFieldMapKeyEnum keyType;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public CustomFieldMapKeyEnum getKeyType() {
        return keyType;
    }

    public void setKeyType(CustomFieldMapKeyEnum keyType) {
        this.keyType = keyType;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        } else if (!(obj instanceof CustomFieldMatrixColumn)) { // Fails with proxed objects: getClass() != obj.getClass()){
            return false;
        }

        CustomFieldMatrixColumn other = (CustomFieldMatrixColumn) obj;

        if (code == null) {
            if (other.getCode() != null) {
                return false;
            }
        } else if (!code.equals(other.getCode())) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(CustomFieldMatrixColumn other) {
        return this.position - other.getPosition();
    }
}