package org.meveo.api.dto.notification;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.notification.ScriptNotification;

/**
 * The Class ScriptNotificationDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "ScriptNotification")
@XmlAccessorType(XmlAccessType.FIELD)
public class ScriptNotificationDto extends NotificationDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -4101784852715599125L;

    /**
     * Script Notification dto constructor
     */
    public ScriptNotificationDto() {

    }

    /**
     * Construct dto from a Script Notification entity
     * 
     * @param scriptNotification Script Notification.
     */
    public ScriptNotificationDto(ScriptNotification scriptNotification) {
        super(scriptNotification);
    }

}