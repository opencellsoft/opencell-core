package org.meveo.api.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.crm.EntityReferenceWrapper;

/**
 * @author Edward P. Legaspi
 * @since Oct 4, 2013
 **/
@XmlRootElement(name = "EntityReference")
@XmlAccessorType(XmlAccessType.FIELD)
public class EntityReferenceDto implements Serializable {

    private static final long serialVersionUID = -4639754869992269238L;

    @XmlAttribute(required = true)
    private String classname;

    @XmlAttribute(required = true)
    private String code;

    public EntityReferenceDto() {

    }

    public EntityReferenceDto(EntityReferenceWrapper e) {
        classname = e.getClassname();
        code = e.getCode();
    }

    public EntityReferenceWrapper fromDTO() {
        if (code == null || code.isEmpty()) {
            return null;
        }
        return new EntityReferenceWrapper(classname, code);
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
        return String.format("EntityReferenceDto [classname=%s, code=%s]", classname, code);
    }

    /**
     * Is value empty
     * 
     * @return True if classname or code are empty
     */
    public boolean isEmpty() {
        return classname == null || classname.length() == 0 || code == null || code.length() == 0;
    }
}