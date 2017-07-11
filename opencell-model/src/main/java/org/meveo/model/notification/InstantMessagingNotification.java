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

@Entity
@Table(name = "adm_notif_im")
public class InstantMessagingNotification extends Notification {

    private static final long serialVersionUID = 7841414559743010567L;

    @Column(name = "im_provider", length = 20)
    @NotNull
    private InstantMessagingProviderEnum imProvider;

    @Column(name = "id_expression", length = 2000)
    @Size(max = 2000)
    private String idEl;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "adm_notif_im_list")
    private Set<String> ids;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "adm_notif_im_user")
    private Set<User> users;

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
        return String.format("InstantMessagingNotification [imProvider=%s, idEl=%s, ids=%s,  message=%s, notification=%s]", imProvider, idEl, ids != null ? toString(ids, maxLen)
                : null, message, super.toString());
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