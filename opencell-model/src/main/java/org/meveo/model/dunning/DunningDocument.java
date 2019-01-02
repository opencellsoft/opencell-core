package org.meveo.model.dunning;

import org.hibernate.annotations.GenericGenerator;
import org.meveo.model.AuditableEntity;
import org.meveo.model.billing.Invoice;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.Payment;

import javax.persistence.*;
import java.util.List;

/**
 * Dunning document
 */
@Entity
@Table(name="dunning_document")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @org.hibernate.annotations.Parameter(name = "sequence_name", value = "dunning_document_seq"), })
public class DunningDocument extends AuditableEntity {

    /**
     * Customer account
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_account_id")
    private CustomerAccount customerAccount;

    /**
     * Unpaid invoices associated to this dunning document
     */
    @OneToMany(mappedBy = "dunningDocument", fetch = FetchType.LAZY)
    private List<Invoice> dueInvoices;

    /**
     * payments done withing the dunning process
     * associated with this dunning doc
     */
    @OneToMany(mappedBy = "dunningDocument", fetch = FetchType.LAZY)
    private List<Payment> payments;

    /**
     * dunning process events
     */
    @OneToMany(mappedBy = "dunningDocument", fetch = FetchType.LAZY)
    private List<DunningEvent> events;

    /**
     * Dunning doc status
     */
    @Column
    @Enumerated(EnumType.STRING)
    private DunningDocStatusEnum status;
}
