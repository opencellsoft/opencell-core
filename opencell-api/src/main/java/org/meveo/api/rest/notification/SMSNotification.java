package org.meveo.api.rest.notification;

import static jakarta.ws.rs.core.MediaType.APPLICATION_XML;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.notification.SMSInfoDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.rest.IBaseRs;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import java.text.ParseException;

@Path("/communications/sms")
@Consumes({ APPLICATION_JSON, APPLICATION_XML })
@Produces({ APPLICATION_JSON, APPLICATION_XML })
public interface SMSNotification extends IBaseRs {

    /**
     * Send SMS to Customer.
     *
     * @param SMSInfoDto
     * @return MessageResponseInfoDTO
     *
     * @throws ParseException
     * @throws MeveoApiException
     * @throws BusinessException
     */
    @POST
    @Path("/send")
    Response send(SMSInfoDto sms) throws MeveoApiException;
}