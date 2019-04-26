package org.meveo.model.scripts;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.meveo.commons.utils.XStreamCDATAConverter;
import org.meveo.model.EnableBusinessEntity;
import org.meveo.model.ExportIdentifier;

import com.thoughtworks.xstream.annotations.XStreamConverter;

/**
 * Customization script
 * 
 * @author Andrius Karpavicius
 */
@ExportIdentifier({ "code" })
@MappedSuperclass
public abstract class CustomScript extends EnableBusinessEntity {

    private static final long serialVersionUID = 8176170199770220430L;

    /**
     * Script contents/source
     */
    @Column(name = "script", nullable = false, columnDefinition = "TEXT")
    @NotNull
    @XStreamConverter(XStreamCDATAConverter.class)
    protected String script;

    /**
     * Script language
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "src_type")
    protected ScriptSourceTypeEnum sourceTypeEnum = ScriptSourceTypeEnum.JAVA;

    /**
     * Script compilation errors
     */
    @Transient
    protected List<ScriptInstanceError> scriptErrors = new ArrayList<ScriptInstanceError>();

    /**
     * Does script currently have compilation errors
     */
    @Type(type = "numeric_boolean")
    @Column(name = "is_error")
    protected Boolean error = false;

    /**
     * Shall same script instance be utilized in repeated calls
     */
    @Type(type = "numeric_boolean")
    @Column(name = "reuse")
    protected Boolean reuse = false;

    /**
     * @return Script language
     */
    public ScriptSourceTypeEnum getSourceTypeEnum() {
        return sourceTypeEnum;
    }

    /**
     * @param sourceTypeEnum Script language
     */
    public void setSourceTypeEnum(ScriptSourceTypeEnum sourceTypeEnum) {
        this.sourceTypeEnum = sourceTypeEnum;
    }

    /**
     * @return the script
     */
    public String getScript() {
        return script;
    }

    /**
     * @param script the script to set
     */
    public void setScript(String script) {
        this.script = script;
    }

    /**
     * @return the Script errors
     */
    public List<ScriptInstanceError> getScriptErrors() {
        return scriptErrors;
    }

    /**
     * @param scriptErrors Script errors to set
     */
    public void setScriptErrors(List<ScriptInstanceError> scriptErrors) {
        this.scriptErrors = scriptErrors;
    }

    /**
     * @return the error
     */
    public Boolean isError() {
        return error;
    }

    /**
     * @return the error
     */
    public Boolean getError() {
        return error;
    }

    /**
     * @param error the error to set
     */
    public void setError(Boolean error) {
        this.error = error;
    }

    /**
     * @return Shall same script instance be utilized in repeated calls
     */
    public Boolean getReuse() {
        return reuse;
    }

    /**
     * @return Shall same script instance be utilized in repeated calls
     */
    public Boolean isReuse() {
        return reuse;
    }

    /**
     * @param reuse Shall same script instance be utilized in repeated calls
     */
    public void setReuse(Boolean reuse) {
        this.reuse = reuse;
    }
}