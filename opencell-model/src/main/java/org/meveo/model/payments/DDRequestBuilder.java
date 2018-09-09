/**
 * 
 */
package org.meveo.model.payments;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.EnableBusinessCFEntity;
import org.meveo.model.ModuleItem;
import org.meveo.model.scripts.ScriptInstance;

/**
 * The DDRequestBuilder on opencell exists in 2 types : Natif as standard Sepa and custom format
 *
 *
 * @author anasseh
 * @since Opencell 5.2
 */

@Entity
@ModuleItem
@CustomFieldEntity(cftCodePrefix = "DDREQ_BUILDER")
@Table(name = "ar_ddrequest_builder")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "ar_ddrequest_builder_seq"), })
public class DDRequestBuilder extends EnableBusinessCFEntity {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 697688142566383812L;

    /** The type. */
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    @NotNull
    private DDRequestBuilderTypeEnum type;

    /** The script instance. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "script_instance_id")
    private ScriptInstance scriptInstance;

    /** The implementation class name. */
    @Column(name = "implementation_class_name", length = 255)
    @Size(max = 255)
    private String implementationClassName;

    /** The nb operation per file. */
    @Column(name = "nb_ops_file")
    private Long nbOperationPerFile;
    
    /** The max size file in ko */
    @Column(name = "max_size_file")
    private Long maxSizeFile;


    public DDRequestBuilder() {

    }

    /**
     * @return the type
     */
    public DDRequestBuilderTypeEnum getType() {
        return type;
    }
    
    /**
     * @param type the type to set
     */
    public void setType(DDRequestBuilderTypeEnum type) {
        this.type = type;
    }

    /**
     * @return the scriptInstance
     */
    public ScriptInstance getScriptInstance() {
        return scriptInstance;
    }

    /**
     * @param scriptInstance the scriptInstance to set
     */
    public void setScriptInstance(ScriptInstance scriptInstance) {
        this.scriptInstance = scriptInstance;
    }

    /**
     * @return the implementationClassName
     */
    public String getImplementationClassName() {
        return implementationClassName;
    }

    /**
     * @param implementationClassName the implementationClassName to set
     */
    public void setImplementationClassName(String implementationClassName) {
        this.implementationClassName = implementationClassName;
    }

    /**
     * @return the nbOperationPerFile
     */
    public Long getNbOperationPerFile() {
        return nbOperationPerFile;
    }

    /**
     * @param nbOperationPerFile the nbOperationPerFile to set
     */
    public void setNbOperationPerFile(Long nbOperationPerFile) {
        this.nbOperationPerFile = nbOperationPerFile;
    }

    /**
     * @return the maxSizeFile
     */
    public Long getMaxSizeFile() {
        return maxSizeFile;
    }

    /**
     * @param maxSizeFile the maxSizeFile to set
     */
    public void setMaxSizeFile(Long maxSizeFile) {
        this.maxSizeFile = maxSizeFile;
    }    
}
