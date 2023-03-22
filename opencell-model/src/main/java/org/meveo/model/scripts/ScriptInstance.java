/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */
package org.meveo.model.scripts;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.EnableBusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ModuleItem;

/**
 * Custom script
 * 
 * @author Andrius Karpavicius
 */
@Entity
@ModuleItem
@Cacheable
@ExportIdentifier({ "code" })
@Table(name = "meveo_script_instance", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "meveo_script_instance_seq"), })
@NamedQueries({ @NamedQuery(name = "CustomScript.countScriptInstanceOnError", query = "select count (*) from ScriptInstance o where o.error=:isError "),
        @NamedQuery(name = "CustomScript.getScriptInstanceOnError", query = "from ScriptInstance o where o.error=:isError "),
        @NamedQuery(name = "CustomScript.getScriptInstanceByTypeActive", query = "from ScriptInstance o where o.sourceTypeEnum=:sourceTypeEnum and o.disabled = false") })
public class ScriptInstance extends EnableBusinessEntity {

    private static final long serialVersionUID = -7691357496569390167L;

    /**
     * Script contents/source
     */
    @Type(type = "longText")
    @Column(name = "script")
//    @XStreamConverter(XStreamCDATAConverter.class)
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
    @Column(name = "is_error", nullable = false)
    @NotNull
    protected boolean error = false;

    /**
     * Shall same script instance be utilized in repeated calls
     */
    @Type(type = "numeric_boolean")
    @Column(name = "reuse", nullable = false)
    @NotNull
    protected boolean reuse = false;

    /**
     * Script category
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "script_instance_cat_id")
    private ScriptInstanceCategory scriptInstanceCategory;

    /**
     * A list of roles that can execute a script
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "role")
    @CollectionTable(name = "adm_script_exec_role", joinColumns = @JoinColumn(name = "script_instance_id", referencedColumnName = "id"))
    private Set<String> executionRoles = new HashSet<String>();

    /**
     * A list of roles that can view/modify the script
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "role")
    @CollectionTable(name = "adm_script_sourc_role", joinColumns = @JoinColumn(name = "script_instance_id", referencedColumnName = "id"))   
    private Set<String> sourcingRoles = new HashSet<String>();
    
    @Type(type = "json")
    @Column(name = "description_i18n", columnDefinition = "jsonb")
    private Map<String, String> descriptionI18n;    
    
    /**
     * A list of script parameters that can use the script
     */
    @OneToMany(mappedBy = "scriptInstance", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScriptParameter> scriptParameters=new ArrayList<>();

    public ScriptInstance() {

    }

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
    public boolean isError() {
        return error;
    }

    /**
     * @return the error
     */
    public boolean getError() {
        return error;
    }

    /**
     * @param error the error to set
     */
    public void setError(boolean error) {
        this.error = error;
    }

    /**
     * @return Shall same script instance be utilized in repeated calls
     */
    public boolean getReuse() {
        return reuse;
    }

    /**
     * @return Shall same script instance be utilized in repeated calls
     */
    public boolean isReuse() {
        return reuse;
    }

    /**
     * @param reuse Shall same script instance be utilized in repeated calls
     */
    public void setReuse(boolean reuse) {
        this.reuse = reuse;
    }

    /**
     * @return the executionRoles
     */
    public Set<String> getExecutionRoles() {
        return executionRoles;
    }

    /**
     * @param executionRoles the executionRoles to set
     */
    public void setExecutionRoles(Set<String> executionRoles) {
        this.executionRoles = executionRoles;
    }

    /**
     * @return the sourcingRoles
     */
    public Set<String> getSourcingRoles() {
        return sourcingRoles;
    }

    /**
     * @param sourcingRoles the sourcingRoles to set
     */
    public void setSourcingRoles(Set<String> sourcingRoles) {
        this.sourcingRoles = sourcingRoles;
    }

    public ScriptInstanceCategory getScriptInstanceCategory() {
        return scriptInstanceCategory;
    }

    public void setScriptInstanceCategory(ScriptInstanceCategory scriptInstanceCategory) {
        this.scriptInstanceCategory = scriptInstanceCategory;
    }
    
    public Map<String, String> getDescriptionI18n() {
        return descriptionI18n;
    }

    public void setDescriptionI18n(Map<String, String> descriptionI18n) {
        this.descriptionI18n = descriptionI18n;
    }

	public List<ScriptParameter> getScriptParameters() {
		return scriptParameters;
	}

	public void setScriptParameters(List<ScriptParameter> scriptParameters) {
		this.scriptParameters = scriptParameters;
	}
    
}