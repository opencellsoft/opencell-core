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

package org.meveo.model.notification;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.meveo.model.admin.User;

/**
 * Notification by sending instant message
 * 
 * @author Andrius Karpavicius
 */
@Entity
@Table(name = "adm_notif_im")
public class InstantMessagingNotification extends Notification {

    private static final long serialVersionUID = 7841414559743010567L;

    /**
     * Instant message provider
     */
    @Column(name = "im_provider", length = 20)
    @NotNull
    private InstantMessagingProviderEnum imProvider;

    /**
     * Expression to determine recipient's user identifier
     */
    @Column(name = "id_expression", length = 2000)
    @Size(max = 2000)
    private String idEl;

    /**
     * Recipient's user identifiers
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "adm_notif_im_list")
    private Set<String> ids;

    /**
     * Application users as recipients
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "adm_notif_im_user")
    private Set<User> users;

    /**
     * Expression to compose a message
     */
    @Column(name = "message", length = 2000)
    @NotNull
    @Size(max = 2000)
    private String message;

    public String getIdEl() {
        return idEl;
    }

    public void setIdEl(String idEl) {
        this.idEl = idEl;
    }

    public InstantMessagingProviderEnum getImProvider() {
        return imProvider;
    }

    public void setImProvider(InstantMessagingProviderEnum imProvider) {
        this.imProvider = imProvider;
    }

    public Set<String> getIds() {
        return ids;
    }

    public void setIds(Set<String> ids) {
        this.ids = ids;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        final int maxLen = 10;
        return String.format("InstantMessagingNotification [imProvider=%s, idEl=%s, ids=%s,  message=%s, notification=%s]", imProvider, idEl,
            ids != null ? toString(ids, maxLen) : null, message, super.toString());
    }

    private String toString(Collection<?> collection, int maxLen) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        int i = 0;
        for (Iterator<?> iterator = collection.iterator(); iterator.hasNext() && i < maxLen; i++) {
            if (i > 0)
                builder.append(", ");
            builder.append(iterator.next());
        }
        builder.append("]");
        return builder.toString();
    }
}