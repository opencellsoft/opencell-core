package org.meveo.api.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.LanguageIsoDto;
import org.meveo.api.dto.response.GetLanguageIsoResponse;
import org.meveo.api.dto.response.GetLanguagesIsoResponse;

/**
 * * Web service for managing {@link org.meveo.model.billing.Language}.
 * 
 * @author Edward P. Legaspi
 **/
@Path("/languageIso")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface LanguageIsoRs extends IBaseRs {

    /**
     * Creates tradingLanguage base on language code. If the language code does not exists, a language record is created.
     * 
     * @param languageIsoDto language iso.
     * @return action status
     */
    @POST
    @Path("/")
    ActionStatus create(LanguageIsoDto languageIsoDto);

    /**
     * Search language given a code.
     * 
     * @param languageCode code of language
     * @return language iso for given code
     */
    @GET
    @Path("/")
    GetLanguageIsoResponse find(@QueryParam("languageCode") String languageCode);

    /**
     * Does not delete a language but the tradingLanguage associated to it.
     * 
     * @param languageCode code of language.
     * @return action status
     */
    @DELETE
    @Path("/{languageCode}")
    ActionStatus remove(@PathParam("languageCode") String languageCode);

    /**
     * modify a language. Same input parameter as create. The language and trading Language are created if they don't exists. The operation fails if the tradingLanguage is null.
     * 
     * @param languageIsoDto language iso
     * @return action status
     */
    @PUT
    @Path("/")
    ActionStatus update(LanguageIsoDto languageIsoDto);

    /**
     * Create or update a language if it doesn't exists.
     * 
     * @param languageIsoDto langauge iso
     * @return action status
     */
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(LanguageIsoDto languageIsoDto);

    /**
     * List all languages.
     * 
     * @return all languages
     */
    @GET
    @Path("/")
    GetLanguagesIsoResponse list();
}
