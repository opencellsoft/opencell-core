package org.meveo.model.dunning;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableEntity;

/**
 * @author Mbarek-Ay
 * @version 11.0
 */
@Entity
@Table(name = "dunning_collection_plan_statuses")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "dunning_collection_plan_statuses_seq") })
@NamedQueries({
		@NamedQuery(name = "DunningCollectionPlanStatus.findByStatus", query = "SELECT cps FROM DunningCollectionPlanStatus cps where cps.status = :status")
})
public class DunningCollectionPlanStatus extends AuditableEntity {

    private static final long serialVersionUID = 1L;

    public DunningCollectionPlanStatus() {
        super();
    }

    public DunningCollectionPlanStatus(@Size(max = 50) @NotNull String status, @NotNull String description, @NotNull DunningSettings dunningSettings, String colorCode) {
        super();
        this.status = status;
        this.description = description;
        this.dunningSettings = dunningSettings;
        this.colorCode = colorCode;
    }

    /**
     * status
     */
    @Column(name = "status", length = 50)
    @Size(max = 50)
    @NotNull
    private String status;

    /**
     * description
     */
    @Column(name = "description", length = 255)
    @NotNull
    private String description;

    /**
     * colorCode
     */
    @Column(name = "color_code", length = 50)
    private String colorCode;

    /**
     * dunning settings associated to the entity
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dunning_settings_id", nullable = false, referencedColumnName = "id")
    @NotNull
    private DunningSettings dunningSettings;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public DunningSettings getDunningSettings() {
        return dunningSettings;
    }

    public void setDunningSettings(DunningSettings dunningSettings) {
        this.dunningSettings = dunningSettings;
    }
}
