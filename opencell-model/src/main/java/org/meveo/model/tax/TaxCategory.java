package org.meveo.model.tax;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ISearchable;

/**
 * Tax category
 * 
 * @author Andrius Karpavicius
 *
 */
@Entity
@Cacheable
@ExportIdentifier({ "code" })
@Table(name = "billing_tax_category", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "billing_tax_category_seq"), })
public class TaxCategory extends BusinessCFEntity implements Serializable, ISearchable {
    private static final long serialVersionUID = 1L;

    /**
     * Translated descriptions in JSON format with language code as a key and translated description as a value
     **/
    @Type(type = "json")
    @Column(name = "description_i18n", columnDefinition = "text")
    private Map<String, String> descriptionI18n;

    /**
     * @param descriptionI18n Translated descriptions in JSON format with language code as a key and translated description as a value
     */
    public Map<String, String> getDescriptionI18n() {
        return this.descriptionI18n;
    }

    /**
     * @return Translated descriptions in JSON format with language code as a key and translated description as a value
     */
    public void setDescriptionI18n(Map<String, String> descriptionI18n) {
        this.descriptionI18n = descriptionI18n;
    }

    /**
     * Instantiate descriptionI18n field if it is null. NOTE: do not use this method unless you have an intention to modify it's value, as entity will be marked dirty and record
     * will be updated in DB
     * 
     * @return descriptionI18n value or instantiated descriptionI18n field value
     */
    public Map<String, String> getDescriptionI18nNullSafe() {
        if (this.descriptionI18n == null) {
            this.descriptionI18n = new HashMap<>();
        }
        return this.descriptionI18n;
    }
}