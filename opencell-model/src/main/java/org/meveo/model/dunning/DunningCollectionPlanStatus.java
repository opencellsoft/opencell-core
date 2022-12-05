package org.meveo.model.dunning;

import static jakarta.persistence.EnumType.STRING;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableEntity;
import org.meveo.model.payments.DunningCollectionPlanStatusEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

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

    public DunningCollectionPlanStatus(@Size(max = 50) @NotNull DunningCollectionPlanStatusEnum status, @NotNull String description, @NotNull DunningSettings dunningSettings, String colorCode) {
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
    @Enumerated(STRING)
    @NotNull
    private DunningCollectionPlanStatusEnum status;

    /**
     * description
     */
    @Column(name = "description")
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

    public DunningCollectionPlanStatusEnum getStatus() {
        return status;
    }

    public void setStatus(DunningCollectionPlanStatusEnum status) {
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
