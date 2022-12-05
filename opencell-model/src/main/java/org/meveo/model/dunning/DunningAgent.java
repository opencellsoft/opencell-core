package org.meveo.model.dunning;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.type.NumericBooleanConverter;
import org.meveo.model.AuditableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;

/**
 * @author Mbarek-Ay
 * @version 11.0
 *
 */
@Entity
@Table(name = "dunning_agent")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "dunning_agent_seq") })
public class DunningAgent extends AuditableEntity {

    private static final long serialVersionUID = -2094816912162086909L;

    public DunningAgent() {
        super();
    }

    public DunningAgent(boolean external, @Size(max = 100) String collectionAgency, @Size(max = 100) String agentFirstNameItem, @Size(max = 100) String agentLastNameItem,
            @Size(max = 100) String agentEmailItem, DunningSettings dunningSettings) {
        super();
        this.external = external;
        this.collectionAgency = collectionAgency;
        this.agentFirstNameItem = agentFirstNameItem;
        this.agentLastNameItem = agentLastNameItem;
        this.agentEmailItem = agentEmailItem;
        this.dunningSettings = dunningSettings;
    }

    /**
     * include collection agency
     */
    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "external")
    private boolean external = false;

    /**
     * email collection agency
     */
    @Column(name = "collection_agency", length = 100)
    @Size(max = 100)
    private String collectionAgency;

    /**
     * agent First Name Item
     */
    @Column(name = "agent_first_name_item", length = 100)
    @Size(max = 100)
    private String agentFirstNameItem;

    /**
     * agent Last Name Item
     */
    @Column(name = "agent_last_name_item", length = 100)
    @Size(max = 100)
    private String agentLastNameItem;

    /**
     * agent email Item
     */
    @Column(name = "agent_email_item", length = 100)
    @Size(max = 100)
    private String agentEmailItem;

    /**
     * dunning settings associated to the entity
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dunning_settings_id")
    private DunningSettings dunningSettings;

    public DunningSettings getDunningSettings() {
        return dunningSettings;
    }

    public void setDunningSettings(DunningSettings dunningSettings) {
        this.dunningSettings = dunningSettings;
    }

    public boolean isExternal() {
        return external;
    }

    public void setExternal(boolean external) {
        this.external = external;
    }

    public String getCollectionAgency() {
        return collectionAgency;
    }

    public void setCollectionAgency(String collectionAgency) {
        this.collectionAgency = collectionAgency;
    }

    public String getAgentFirstNameItem() {
        return agentFirstNameItem;
    }

    public void setAgentFirstNameItem(String agentFirstNameItem) {
        this.agentFirstNameItem = agentFirstNameItem;
    }

    public String getAgentLastNameItem() {
        return agentLastNameItem;
    }

    public void setAgentLastNameItem(String agentLastNameItem) {
        this.agentLastNameItem = agentLastNameItem;
    }

    public String getAgentEmailItem() {
        return agentEmailItem;
    }

    public void setAgentEmailItem(String agentEmailItem) {
        this.agentEmailItem = agentEmailItem;
    }

}
