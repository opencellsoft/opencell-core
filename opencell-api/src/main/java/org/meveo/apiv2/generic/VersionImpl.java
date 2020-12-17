package org.meveo.apiv2.generic;

import javax.ejb.Stateless;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.meveo.apiv2.GenericOpencellRestful;

@Stateless
public class VersionImpl implements Version {

	@Override
	public Response getVersions() {
		return Response.ok().entity(GenericOpencellRestful.VERSION_INFO).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

}
