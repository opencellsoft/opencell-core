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
package org.meveo.model.payments;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableEntity;
import org.meveo.model.admin.DunningHistory;

@Entity
@Table(name = "ar_dunning_lot")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "ar_dunning_lot_seq"), })
public class DunningLOT extends AuditableEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "file_name", length = 255)
    @Size(max = 255)
    private String fileName;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type")
    private DunningActionTypeEnum actionType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dunning_history_id")
    private DunningHistory dunningHistory;

    @OneToMany(mappedBy = "dunningLOT", fetch = FetchType.LAZY)
    private List<ActionDunning> actions = new ArrayList<ActionDunning>();

    public DunningLOT() {
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public DunningActionTypeEnum getActionType() {
        return actionType;
    }

    public void setActionType(DunningActionTypeEnum actionType) {
        this.actionType = actionType;
    }

    public List<ActionDunning> getActions() {
        return actions;
    }

    public void setActions(List<ActionDunning> actions) {
        this.actions = actions;
    }

    public void setDunningHistory(DunningHistory dunningHistory) {
        this.dunningHistory = dunningHistory;
    }

    public DunningHistory getDunningHistory() {
        return dunningHistory;
    }

}
