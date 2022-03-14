package org.meveo.model.ordering;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "open_order_template")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "open_order_template_seq"),})
public class OpenOrderTemplate extends BusinessEntity {

    private OpenOrderTypeEnum openOrderType;
}
