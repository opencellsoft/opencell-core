package org.meveo.model.dunning;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BusinessEntity;
import org.meveo.model.payments.OCCTemplate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "ar_customer_balance")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "ar_customer_balance_seq"), })
@NamedQueries({
        @NamedQuery(name = "CustomerBalance.findDefaultCustomerBalance",
                query = "SELECT cb FROM CustomerBalance cb WHERE cb.defaultBalance = true") })
public class CustomerBalance extends BusinessEntity {

    @Type(type = "numeric_boolean")
    @Column(name = "default_balance")
    private boolean defaultBalance = false;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "ar_customer_balance_templates",
            joinColumns = @JoinColumn(name = "customer_balance_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "template_id", referencedColumnName = "id"))
    private List<OCCTemplate> occTemplates;

    public boolean isDefaultBalance() {
        return defaultBalance;
    }

    public void setDefaultBalance(boolean defaultBalance) {
        this.defaultBalance = defaultBalance;
    }

    public List<OCCTemplate> getOccTemplates() {
        return occTemplates;
    }

    public void setOccTemplates(List<OCCTemplate> templates) {
        this.occTemplates = templates;
    }
}