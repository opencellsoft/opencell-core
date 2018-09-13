/**
 * 
 */
package org.meveo.api.dto.payment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.EnableBusinessDto;
import org.meveo.model.payments.DDRequestBuilder;
import org.meveo.model.payments.DDRequestBuilderTypeEnum;

/**
 * The DDRequestBuilderDto Dto.
 * 
 * @author anasseh
 *
 */
@XmlRootElement(name = "DDRequestBuilderDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class DDRequestBuilderDto extends EnableBusinessDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 8977150158860312821L;

    /** The type. */
    private DDRequestBuilderTypeEnum type;

    /** The script instance code. */
    private String scriptInstanceCode;

    /** The implementation class name. */
    private String implementationClassName;

    /** The nb operation per file. */
    private Long nbOperationPerFile;

    /** The max size file in ko. */
    private Long maxSizeFile;

    /** The custom fields. */
    private CustomFieldsDto customFields;


    /**
     * Instantiates a new DD request builder dto.
     */
    public DDRequestBuilderDto() {

    }

    /**
     * Convert DDRequestBuilder entity to DTO.
     *
     * @param ddRequestBuilder Entity to convert
     */
    public DDRequestBuilderDto(DDRequestBuilder ddRequestBuilder) {
        super(ddRequestBuilder);
        this.id = ddRequestBuilder.getId();
        this.implementationClassName = ddRequestBuilder.getImplementationClassName();
        this.scriptInstanceCode = ddRequestBuilder.getScriptInstance() == null ? null : ddRequestBuilder.getScriptInstance().getCode();
        this.type = ddRequestBuilder.getType();
        this.maxSizeFile = ddRequestBuilder.getMaxSizeFile();
        this.nbOperationPerFile = ddRequestBuilder.getNbOperationPerFile();
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    public DDRequestBuilderTypeEnum getType() {
        return type;
    }

    /**
     * Sets the type.
     *
     * @param type the type to set
     */
    public void setType(DDRequestBuilderTypeEnum type) {
        this.type = type;
    }

    /**
     * Gets the script instance code.
     *
     * @return the scriptInstanceCode
     */
    public String getScriptInstanceCode() {
        return scriptInstanceCode;
    }

    /**
     * Sets the script instance code.
     *
     * @param scriptInstanceCode the scriptInstanceCode to set
     */
    public void setScriptInstanceCode(String scriptInstanceCode) {
        this.scriptInstanceCode = scriptInstanceCode;
    }

    /**
     * Gets the implementation class name.
     *
     * @return the implementationClassName
     */
    public String getImplementationClassName() {
        return implementationClassName;
    }

    /**
     * Sets the implementation class name.
     *
     * @param implementationClassName the implementationClassName to set
     */
    public void setImplementationClassName(String implementationClassName) {
        this.implementationClassName = implementationClassName;
    }

    /**
     * Gets the nb operation per file.
     *
     * @return the nbOperationPerFile
     */
    public Long getNbOperationPerFile() {
        return nbOperationPerFile;
    }

    /**
     * Sets the nb operation per file.
     *
     * @param nbOperationPerFile the nbOperationPerFile to set
     */
    public void setNbOperationPerFile(Long nbOperationPerFile) {
        this.nbOperationPerFile = nbOperationPerFile;
    }

    /**
     * Gets the max size file.
     *
     * @return the maxSizeFile
     */
    public Long getMaxSizeFile() {
        return maxSizeFile;
    }

    /**
     * Sets the max size file.
     *
     * @param maxSizeFile the maxSizeFile to set
     */
    public void setMaxSizeFile(Long maxSizeFile) {
        this.maxSizeFile = maxSizeFile;
    }

    /**
     * Gets the custom fields.
     *
     * @return the customFields
     */
    public CustomFieldsDto getCustomFields() {
        return customFields;
    }

    /**
     * Sets the custom fields.
     *
     * @param customFields the customFields to set
     */
    public void setCustomFields(CustomFieldsDto customFields) {
        this.customFields = customFields;
    }
}
