package org.meveo.model.notification;

public enum NotificationEventTypeEnum {
CREATED (1,"enum.notificationEventTypeEnum.CREATED"),
UPDATED(2,"enum.notificationEventTypeEnum.UPDATED"),
REMOVED(3,"enum.notificationEventTypeEnum.REMOVED"),
TERMINATED(4,"enum.notificationEventTypeEnum.TERMINATED"),
DISABLED(5,"enum.notificationEventTypeEnum.DISABLED"),
PROCESSED(6,"enum.notificationEventTypeEnum.PROCESSED"),
REJECTED(7,"enum.notificationEventTypeEnum.REJECTED"),
REJECTED_CDR(8,"enum.notificationEventTypeEnum.REJECTED_CDR"),
LOGGED_IN(9,"enum.notificationEventTypeEnum.LOGGED_IN"),
INBOUND_REQ(10,"enum.notificationEventTypeEnum.INBOUND_REQ"),
ENABLED(11,"enum.notificationEventTypeEnum.ENABLED");

private Integer id;
private String label;

NotificationEventTypeEnum(Integer id, String label) {
    this.id = id;
    this.label = label;
}

public Integer getId() {
    return id;
}

public String getLabel() {
    return this.label;
}


public static NotificationEventTypeEnum getValue(Integer id) {
    if (id != null) {
        for (NotificationEventTypeEnum type : values()) {
            if (id.equals(type.getId())) {
                return type;
            }
        }
    }
    return null;
}
}
