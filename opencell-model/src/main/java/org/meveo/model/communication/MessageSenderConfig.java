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
package org.meveo.model.communication;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.type.NumericBooleanConverter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Size;

@Entity
@ExportIdentifier({ "code" })
@Table(name = "com_sender_config", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@DiscriminatorColumn(name = "media")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "com_sndr_conf_seq"), })
public abstract class MessageSenderConfig extends BusinessEntity {

    private static final long serialVersionUID = 1L;

    @Enumerated(EnumType.STRING)
    @Column(name = "media", insertable = false, updatable = false)
    private MediaEnum media;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private PriorityEnum defaultPriority;

    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "manage_non_distrib")
    private Boolean manageNonDistributedMessage;

    @Column(name = "non_distrib_email", length = 255)
    @Size(max = 255)
    private String NonDistributedEmail;

    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "use_ack")
    private Boolean useAcknoledgement;

    @Column(name = "ack_email", length = 255)
    @Size(max = 255)
    private String ackEmail;

    @Embedded
    private CommunicationPolicy senderPolicy;

    public MediaEnum getMedia() {
        return media;
    }

    public void setMedia(MediaEnum media) {
        this.media = media;
    }

    public PriorityEnum getDefaultPriority() {
        return defaultPriority;
    }

    public void setDefaultPriority(PriorityEnum defaultPriority) {
        this.defaultPriority = defaultPriority;
    }

    public Boolean isManageNonDistributedMessage() {
        return manageNonDistributedMessage;
    }

    public void setManageNonDistributedMessage(Boolean manageNonDistributedMessage) {
        this.manageNonDistributedMessage = manageNonDistributedMessage;
    }

    public String getNonDistributedEmail() {
        return NonDistributedEmail;
    }

    public void setNonDistributedEmail(String nonDistributedEmail) {
        NonDistributedEmail = nonDistributedEmail;
    }

    public Boolean isUseAcknoledgement() {
        return useAcknoledgement;
    }

    public void setUseAcknoledgement(Boolean useAcknoledgement) {
        this.useAcknoledgement = useAcknoledgement;
    }

    public String getAckEmail() {
        return ackEmail;
    }

    public void setAckEmail(String ackEmail) {
        this.ackEmail = ackEmail;
    }

    public CommunicationPolicy getSenderPolicy() {
        return senderPolicy;
    }

    public void setSenderPolicy(CommunicationPolicy senderPolicy) {
        this.senderPolicy = senderPolicy;
    }

}
