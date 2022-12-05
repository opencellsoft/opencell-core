package org.meveo.api.rest.dataCollector;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Hidden;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.APPLICATION_XML;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.DataCollectorDto;
import org.meveo.api.dto.response.DataCollectorResponse;
import org.meveo.api.rest.IBaseRs;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;

@Path("/dataCollector")
@Tag(name = "DataCollector", description = "@%DataCollector")
@Consumes({ APPLICATION_JSON, APPLICATION_XML })
@Produces({ APPLICATION_JSON, APPLICATION_XML })
public interface DataCollectorRs extends IBaseRs {

    /**
     * Create a new data collector using a DataCollectorDto.
     *
     * @param postData DataCollector's data
     * @return action status
     */
    @POST
    @Path("/")
	@Operation(
			summary=" Create a new data collector using a DataCollectorDto. ",
			description=" Create a new data collector using a DataCollectorDto. ",
			operationId="    POST_DataCollector_create",
			responses= {
				@ApiResponse(description=" action status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(DataCollectorDto postData);

    /**
     * Find a data collector with a given code.
     *
     * @param code Data collector's code
     * @return Data collector information
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Find a data collector with a given code. ",
			description=" Find a data collector with a given code. ",
			operationId="    GET_DataCollector_search",
			responses= {
				@ApiResponse(description=" Data collector information ",
						content=@Content(
									schema=@Schema(
											implementation= DataCollectorResponse.class
											)
								)
				)}
	)
    DataCollectorResponse find(@QueryParam("code") String code);

    /**
     * Execute data collector SQL query
     *
     * @param dataCollectorCode Data collector's code
     *  @return action status
     */
    @GET
    @Path("/executeQuery")
	@Operation(
			summary=" Execute data collector SQL query ",
			description=" Execute data collector SQL query ",
			operationId="    GET_DataCollector_executeQuery",
			responses= {
				@ApiResponse(description=" action status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus execute(@QueryParam("code") String dataCollectorCode);
}
