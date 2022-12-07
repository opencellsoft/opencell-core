package org.meveo.apiv2.customtable;

import static java.util.Arrays.asList;
import static javax.ws.rs.core.Response.ok;

import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.apiv2.AcountReceivable.DeferralPayments;
import org.meveo.apiv2.generic.core.GenericHelper;
import org.meveo.apiv2.generic.core.GenericRequestMapper;
import org.meveo.apiv2.generic.services.PersistenceServiceHelper;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.audit.logging.AuditLog;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.audit.logging.AuditLogService;
import org.meveo.service.custom.CustomTableService;
import org.meveo.service.payments.impl.AccountOperationService;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;
import java.util.Date;

public class CustomTableResourceImpl implements CustomTableResource {

	@Inject
	private AccountOperationService accountOperationService;

	@Inject
	private CustomTableService customTableService;

	@Inject
	@CurrentUser
	protected MeveoUser currentUser;

	@Override
	public Response export(String customTableCode, String fileFormat) {

		if (!"CSV".equals(fileFormat) && !"EXCEL".equals(fileFormat)) {
			throw new BadRequestException("Accepted formats for export are (CSV or EXCEL).");
		}

		CustomEntityTemplate cet = customTableService.getCET(customTableCode);
		
		Class entityClass = GenericHelper.getEntityClass(entityName);
		
		GenericRequestMapper genericRequestMapper = new GenericRequestMapper(entityClass, PersistenceServiceHelper.getPersistenceService());
		String filePath = loadService.export(entityClass, genericRequestMapper.mapTo(searchConfig), genericFields, genericFieldDetails, fileFormat, entityName, locale);
		return Response.ok()
                 .entity("{\"actionStatus\":{\"status\":\"SUCCESS\",\"message\":\"\"}, \"data\":{ \"filePath\":\""+ filePath +"\"}}")
                 .build();
    }

}