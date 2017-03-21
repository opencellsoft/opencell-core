package org.meveo.model.notification;

public enum InstantMessagingProviderEnum {
SKYPE(1,"enum.InstantMessagingProviderEnum.SKYPE"),
FACEBOOK(2,"enum.InstantMessagingProviderEnum.FACEBOOK"),
TWITTER(3,"enum.InstantMessagingProviderEnum.TWITTER"),
GTALK(4,"enum.InstantMessagingProviderEnum.GTALK"),
YAHOO_MESSENGER(5,"enum.InstantMessagingProviderEnum.YAHOO_MESSENGER");

private Integer id;
private String label;

InstantMessagingProviderEnum(Integer id, String label) {
    this.id = id;
    this.label = label;
}

public Integer getId() {
    return id;
}

public String getLabel() {
    return this.label;
}


public static InstantMessagingProviderEnum getValue(Integer id) {
    if (id != null) {
        for (InstantMessagingProviderEnum type : values()) {
            if (id.equals(type.getId())) {
                return type;
            }
        }
    }
    return null;
}
}
