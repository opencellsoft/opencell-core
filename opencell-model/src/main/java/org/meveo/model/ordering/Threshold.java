package org.meveo.model.ordering;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.payments.CustomerAccount;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "open_order_Threshold")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "open_order_Threshold_seq"),})

public class Threshold extends BusinessEntity {

    @NotNull
    private Integer sequence;
    @NotNull
    private Integer percentage;

    @OneToMany
    private List<Seller> sellers;

    @OneToMany
    private List<CustomerAccount> customers;

    @OneToMany
    private List<UserAccount> users;

}
