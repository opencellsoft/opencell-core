package org.meveo.model.scripts;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.meveo.commons.utils.XStreamCDATAConverter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;

import com.thoughtworks.xstream.annotations.XStreamConverter;

@ExportIdentifier({ "code", "provider" })
@MappedSuperclass
public abstract class CustomScript extends BusinessEntity {

    private static final long serialVersionUID = 8176170199770220430L;

    @Column(name = "SCRIPT", nullable = false, columnDefinition = "TEXT")
    @NotNull
    @XStreamConverter(XStreamCDATAConverter.class)
    private String script;

    @Enumerated(EnumType.STRING)
    @Column(name = "SRC_TYPE")
    private ScriptSourceTypeEnum sourceTypeEnum = ScriptSourceTypeEnum.JAVA;

    @Transient
    private List<ScriptInstanceError> scriptErrors = new ArrayList<ScriptInstanceError>();

    @Column(name = "IS_ERROR")
    private Boolean error = false;

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
}