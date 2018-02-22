package org.meveo.model.module;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ModuleItem;
import org.meveo.model.ObservableEntity;
import org.meveo.model.scripts.ScriptInstance;

/**
 * meveo module has CETs, CFTs, filters, scripts, jobs, notifications
 * 
 * @author Tyshan Shi(tyshanchn@manaty.net)
 * 
 */

@Entity
@ObservableEntity
@Cacheable
@ModuleItem
@ExportIdentifier({ "code"})
@Table(name = "meveo_module", uniqueConstraints = @UniqueConstraint(columnNames = { "code"}))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {@Parameter(name = "sequence_name", value = "meveo_module_seq"), })
@Inheritance(strategy = InheritanceType.JOINED)
public class MeveoModule extends BusinessEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @OneToMany(mappedBy = "meveoModule", cascade = { CascadeType.ALL }, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<MeveoModuleItem> moduleItems = new ArrayList<MeveoModuleItem>();

    @Enumerated(EnumType.STRING)
    @Column(name = "module_license", nullable = false)
    @NotNull
    private ModuleLicenseEnum license = ModuleLicenseEnum.GPL;

    @Column(name = "logo_picture", length = 255)
    @Size(max = 255)
    private String logoPicture;

    @Type(type="numeric_boolean")
    @Column(name = "installed")
    private boolean installed;

    @Column(name = "module_source", nullable = false, columnDefinition = "TEXT")
    private String moduleSource;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "script_instance_id")
    private ScriptInstance script;

    public List<MeveoModuleItem> getModuleItems() {
        return moduleItems;
    }

    public void setModuleItems(List<MeveoModuleItem> moduleItems) {
        this.moduleItems = moduleItems;
    }

    public void addModuleItem(MeveoModuleItem moduleItem) {
        this.moduleItems.add(moduleItem);
        moduleItem.setMeveoModule(this);
    }

    public void removeItem(MeveoModuleItem item) {
        this.moduleItems.remove(item);
        item.setMeveoModule(null);
    }

    public ModuleLicenseEnum getLicense() {
        return license;
    }

    public void setLicense(ModuleLicenseEnum license) {
        this.license = license;
    }

    public String getLogoPicture() {
        return logoPicture;
    }

    public void setLogoPicture(String logoPicture) {
        this.logoPicture = logoPicture;
    }

    public boolean isInstalled() {
        return installed;
    }

    public void setInstalled(boolean installed) {
        this.installed = installed;
    }

    public void setScript(ScriptInstance script) {
        this.script = script;
    }

    public ScriptInstance getScript() {
        return script;
    }

    public void setModuleSource(String moduleSource) {
        this.moduleSource = moduleSource;
    }

    public String getModuleSource() {
        return moduleSource;
    }

    public boolean isDownloaded() {
        return !StringUtils.isBlank(moduleSource);
    }
}