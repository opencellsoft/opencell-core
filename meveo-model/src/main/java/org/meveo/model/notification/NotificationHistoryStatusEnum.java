package org.meveo.model.notification;

public enum NotificationHistoryStatusEnum {
SENT(1,"enum.notificationHistoryStatusEnum.SENT"),
TO_RETRY(2,"enum.notificationHistoryStatusEnum.TO_RETRY"),
FAILED(3,"enum.notificationHistoryStatusEnum.FAILED"),
CANCELED(4,"enum.notificationHistoryStatusEnum.CANCELED");


private Integer id;
private String label;

NotificationHistoryStatusEnum(Integer id, String label) {
    this.id = id;
    this.label = label;
}

public Integer getId() {
    return id;
}

public String getLabel() {
    return this.label;
}


public static NotificationHistoryStatusEnum getValue(Integer id) {
    if (id != null) {
        for (NotificationHistoryStatusEnum type : values()) {
            if (id.equals(type.getId())) {
                return type;
            }
        }
    }
    return null;
}
}
