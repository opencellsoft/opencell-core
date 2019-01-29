package org.meveo.model.intcrm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BaseEntity;
import org.meveo.model.ExportIdentifier;

/**
 * Additional contact details
 * 
 * @author Andrius Karpavicius
 * @lastModifiedVersion 5.4
 */
@Entity
@Table(name = "crm_additional_details")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "crm_additional_details_seq") })
public class AdditionalDetails extends BaseEntity {

    private static final long serialVersionUID = 2502533941498882545L;

    /**
     * Company name
     */
    @Column(name = "company_name", length = 200)
    @Size(max = 200)
    private String companyName;

    /**
     * Position
     */
    @Column(name = "position", length = 200)
    @Size(max = 200)
    private String position;

    /**
     * Instant messenger usernames
     */
    @Column(name = "instant_messengers", length = 2000)
    @Size(max = 500)
    private String instantMessengers;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getInstantMessengers() {
        return instantMessengers;
    }

    public void setInstantMessengers(String instantMessengers) {
        this.instantMessengers = instantMessengers;
    }
}
