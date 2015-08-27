package org.meveo.service.script.todelete;

import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.parse.csv.CDR;
import org.meveo.model.BaseEntity;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.mediation.Access;
import org.meveo.model.rating.EDR;
import org.meveo.model.rating.EDRStatusEnum;
import org.meveo.service.billing.impl.EdrService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mediation_CreateEdr extends org.meveo.service.script.Script {

	private static final Logger log = LoggerFactory.getLogger(Mediation_CreateEdr.class);

	public void execute(Map<String, Object> initContext, Provider provider) throws BusinessException {
		log.debug("Execute...");
		User currentUser = (User) initContext.get("currentUser");
		CDR cdr = (CDR) initContext.get("record");
		String originBatch = (String) initContext.get("originBatch");
		String originRecord = (String) initContext.get("originRecord");

		List<Access> accesses = (List<Access>) initContext.get("accesses");

		EdrService edrService = (EdrService) getServiceInterface("EdrService");

		for (Access accessPoint : accesses) {
			if ((accessPoint.getStartDate() == null || accessPoint.getStartDate().getTime() <= cdr.getTimestamp().getTime()) && (accessPoint.getEndDate() == null || accessPoint.getEndDate().getTime() > cdr.getTimestamp().getTime())) {

				EDR edr = new EDR();
				edr.setCreated(new Date());
				edr.setEventDate(cdr.getTimestamp());
				edr.setOriginBatch(originBatch);
				edr.setOriginRecord(originRecord);
				edr.setParameter1(cdr.getParam1());
				edr.setParameter2(cdr.getParam2());
				edr.setParameter3(cdr.getParam3());
				edr.setParameter4(cdr.getParam4());
				edr.setParameter5(cdr.getParam5());
				edr.setParameter6(cdr.getParam6());
				edr.setParameter7(cdr.getParam7());
				edr.setParameter8(cdr.getParam8());
				edr.setParameter9(cdr.getParam9());
				edr.setDateParam1(cdr.getDateParam1() != 0 ? new Date(cdr.getDateParam1()) : null);
				edr.setDateParam2(cdr.getDateParam2() != 0 ? new Date(cdr.getDateParam2()) : null);
				edr.setDateParam3(cdr.getDateParam3() != 0 ? new Date(cdr.getDateParam3()) : null);
				edr.setDateParam4(cdr.getDateParam4() != 0 ? new Date(cdr.getDateParam4()) : null);
				edr.setDateParam5(cdr.getDateParam5() != 0 ? new Date(cdr.getDateParam5()) : null);
				edr.setDecimalParam1(cdr.getDecimalParam1() != null ? cdr.getDecimalParam1().setScale(BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP) : null);
				edr.setDecimalParam2(cdr.getDecimalParam2() != null ? cdr.getDecimalParam2().setScale(BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP) : null);
				edr.setDecimalParam3(cdr.getDecimalParam3() != null ? cdr.getDecimalParam3().setScale(BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP) : null);
				edr.setDecimalParam4(cdr.getDecimalParam4() != null ? cdr.getDecimalParam4().setScale(BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP) : null);
				edr.setDecimalParam5(cdr.getDecimalParam5() != null ? cdr.getDecimalParam5().setScale(BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP) : null);
				edr.setAccessCode(accessPoint.getAccessUserId());
				edr.setProvider(accessPoint.getProvider());
				edr.setQuantity(cdr.getQuantity());
				edr.setStatus(EDRStatusEnum.OPEN);
				edr.setSubscription(accessPoint.getSubscription());
				edrService.create(edr, currentUser, provider);
			}
			log.info("Execute update entity OK");
		}
	}

}