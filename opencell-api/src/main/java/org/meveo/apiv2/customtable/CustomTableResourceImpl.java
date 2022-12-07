package org.meveo.apiv2.customtable;

import static java.util.Optional.ofNullable;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

import org.meveo.apiv2.generic.services.GenericFileExportManager;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.service.custom.CustomTableService;

public class CustomTableResourceImpl implements CustomTableResource {

	@Inject
	private CustomTableService customTableService;

	@Inject
	private GenericFileExportManager genericExportManager;

	@Override
	public Response export(String customTableCode, String fileFormat) {

		if (!"CSV".equals(fileFormat) && !"EXCEL".equals(fileFormat)) {
			throw new BadRequestException("Accepted formats for export are (CSV or EXCEL).");
		}

		CustomEntityTemplate cet = ofNullable(customTableService.getCET(customTableCode)).orElseThrow(
				() -> new NotFoundException("The custom table code " + customTableCode + " does not exits"));

		String filePath = genericExportManager.export(customTableCode, customTableService.exportCustomTable(cet),
				fileFormat, null, null, null);

		return Response.ok()
				.entity("{\"actionStatus\":{\"status\":\"SUCCESS\",\"message\":\"\"}, \"data\":{ \"filePath\":\"" + filePath + "\"}}")
				.build();
	}

}