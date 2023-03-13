/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.model.billing;


import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * View to enhance billing performance
 *
 * @author Wassim Drira
 */
@Entity
@Immutable
@Table(name="view_usage_charge_instances")
public class UsageChargeInstancesView {
    @Id
    @Column(name = "id", nullable = false)
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id")
    private UsageChargeInstance chargeInstance;

    @Column(name = "charge_type")
    @Size(max = 1)
    private String chargeType;

    @Column(name = "status")
    private InstanceStatusEnum status;

    @Column(name = "termination_date")
    private Date terminationDate;

    @Column(name = "subscription_id")
    private Long subscription;

    @Column(name = "priority")
    private int priority = 1;

    public UsageChargeInstance getChargeInstance() {
        return chargeInstance;
    }

    public String getChargeType() {
        return chargeType;
    }

    public InstanceStatusEnum getStatus() {
        return status;
    }

    public Date getTerminationDate() {
        return terminationDate;
    }

    public Long getSubscription() {
        return subscription;
    }

    public int getPriority() {
        return priority;
    }
}
