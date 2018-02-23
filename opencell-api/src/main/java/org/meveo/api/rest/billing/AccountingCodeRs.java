package org.meveo.api.rest.billing;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.billing.AccountingCodeDto;
import org.meveo.api.rest.IBaseRs;

/**
 * @author Edward P. Legaspi
 * @version 23 Feb 2018
 **/
@Path("/billing/accountingCode")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public interface AccountingCodeRs extends IBaseRs {

    /**
     * Creates a new AccountingCode.
     * 
     * @param postData
     * @return
     */
    @POST
    @Path("/")
    ActionStatus create(AccountingCodeDto postData);

    /**
     * Removes an AccountingCode entity.
     * @param accountingCode
     * @return
     */
    @DELETE
    @Path("/{accountingCode}")
    ActionStatus remove(@PathParam("accountingCode") String accountingCode);

}
