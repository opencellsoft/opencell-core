package org.meveo.model.dunning;

import static javax.persistence.EnumType.STRING;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableEntity;
import org.meveo.model.payments.DunningCollectionPlanStatusEnum;

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
