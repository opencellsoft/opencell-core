package org.meveo.api.rest.notification;

import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.notification.SMSInfoDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.rest.IBaseRs;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
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