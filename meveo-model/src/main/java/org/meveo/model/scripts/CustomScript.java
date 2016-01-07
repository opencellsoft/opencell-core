package org.meveo.model.scripts;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;

@Entity
@ExportIdentifier({ "code", "appliesTo", "provider" })
@Table(name = "MEVEO_SCRIPT_INSTANCE", uniqueConstraints = @UniqueConstraint(columnNames = { "CODE", "APPLIES_TO", "PROVIDER_ID" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "MEVEO_SCRIPT_INSTANCE_SEQ")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="SCRIPT_TYPE")
@NamedQueries({ @NamedQuery(name = "CustomScript.countScriptInstanceOnError", query = "select count (*) from CustomScript o where o.error=:isError and o.provider=:provider"),
        @NamedQuery(name = "CustomScript.getScriptInstanceOnError", query = "from CustomScript o where o.error=:isError and o.provider=:provider") })
public abstract class CustomScript extends BusinessEntity {

    private static final long serialVersionUID = 8176170199770220430L;

    @Column(name = "SCRIPT", nullable = false, columnDefinition = "TEXT")
    private String script;

    @Enumerated(EnumType.STRING)
    @Column(name = "SRC_TYPE")
    private ScriptSourceTypeEnum sourceTypeEnum = ScriptSourceTypeEnum.JAVA;

//    @OneToMany(mappedBy = "script", orphanRemoval = true, fetch = FetchType.EAGER)
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