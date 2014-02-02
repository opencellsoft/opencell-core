package org.meveo.asg.api.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.meveo.api.ActionStatus;
import org.meveo.api.ActionStatusEnum;
import org.meveo.api.dto.OrganizationDto;
import org.meveo.asg.api.model.EntityCodeEnum;
import org.meveo.asg.api.service.AsgIdMappingService;
import org.meveo.commons.utils.StringUtils;
import org.meveo.util.MeveoJpaForJobs;

/**
 * @author Edward P. Legaspi
 * @since Oct 11, 2013
 **/
@Path("/asg/organization")
@RequestScoped
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class OrganizationWS extends org.meveo.api.rest.OrganizationWS {

	@Inject
	private AsgIdMappingService asgIdMappingService;

	@Inject
	@MeveoJpaForJobs
	private EntityManager em;

	@POST
	@Path("/")
	public ActionStatus create(OrganizationDto orgDto) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			String asgOrganizationId = orgDto.getOrganizationId();
			String asgParentId = orgDto.getParentId();

			orgDto.setOrganizationId(asgIdMappingService.getNewCode(em,
					asgOrganizationId, EntityCodeEnum.ORG));
			if (orgDto.getParentId() != null
					&& !StringUtils.isBlank(orgDto.getParentId())) {
				orgDto.setParentId(asgIdMappingService.getMeveoCode(em,
						asgParentId, EntityCodeEnum.ORG));
			}
			result = super.create(orgDto);

			if (result.getStatus() == ActionStatusEnum.FAIL) {
				asgIdMappingService.removeByCodeAndType(em, asgOrganizationId,
						EntityCodeEnum.ORG);
				if (asgParentId != null && !StringUtils.isBlank(asgParentId)) {
					asgIdMappingService.removeByCodeAndType(em, asgParentId,
							EntityCodeEnum.ORG);
				}
			}
		} catch (Exception e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}

		return result;
	}

	@PUT
	@Path("/")
	public ActionStatus update(OrganizationDto orgDto) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			orgDto.setOrganizationId(asgIdMappingService.getMeveoCode(em,
					orgDto.getOrganizationId(), EntityCodeEnum.ORG));
			if (orgDto.getParentId() != null
					&& !StringUtils.isBlank(orgDto.getParentId())) {
				orgDto.setParentId(asgIdMappingService.getMeveoCode(em,
						orgDto.getParentId(), EntityCodeEnum.ORG));
			}

			result = super.update(orgDto);
		} catch (Exception e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}

		return result;
	}

	@DELETE
	@Path("/{organizationId}")
	public ActionStatus remove(
			@PathParam("organizationId") String organizationId) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			organizationId = asgIdMappingService.getMeveoCode(em,
					organizationId, EntityCodeEnum.ORG);

			result = super.remove(organizationId);

			if (result.getStatus() == ActionStatusEnum.SUCCESS) {
				asgIdMappingService.removeByCodeAndType(em, organizationId,
						EntityCodeEnum.ORG);
			}
		} catch (Exception e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}

		return result;
	}

}
