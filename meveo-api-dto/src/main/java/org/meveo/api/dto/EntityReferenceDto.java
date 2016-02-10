package org.meveo.api.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringUtils;
import org.meveo.model.crm.EntityReferenceWrapper;

/**
 * Represents a custom field value type - reference to an Meveo entity identified by a classname and code. In case a class is a generic Custom Entity Template a classnameCode is
 * required to identify a concrete custom entity template by its code
 * 
 * @author Andrius Karpavicius
 **/
@XmlRootElement(name = "EntityReference")
@XmlAccessorType(XmlAccessType.FIELD)
public class EntityReferenceDto implements Serializable {

    private static final long serialVersionUID = -4639754869992269238L;

    /**
     * Classname of an entity
     */
    @XmlAttribute(required = true)
    private String classname;

    /**
     * Custom entity template code - applicable and required when reference is to Custom Entity Template type
     */
    @XmlAttribute(required = false)
    private String classnameCode;

    /**
     * Entity code
     */
    @XmlAttribute(required = true)
    private String code;

    public EntityReferenceDto() {

    }

    public EntityReferenceDto(EntityReferenceWrapper e) {
        classname = e.getClassname();
        classnameCode = e.getClassnameCode();
        code = e.getCode();
    }

    public EntityReferenceWrapper fromDTO() {
        if (isEmpty()) {
            return null;
        }
        return new EntityReferenceWrapper(classname, classnameCode, code);
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return String.format("EntityReferenceDto [classname=%s, classnameCode=%s, code=%s]", classname, classnameCode, code);
    }

    /**
     * Is value empty
     * 
     * @return True if classname or code are empty
     */
    public boolean isEmpty() {
        return StringUtils.isBlank(classname) || StringUtils.isBlank(code);
    }
}