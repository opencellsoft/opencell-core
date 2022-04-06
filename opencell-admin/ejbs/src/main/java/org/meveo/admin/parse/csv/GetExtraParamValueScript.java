package org.meveo.admin.parse.csv;

import java.util.Date;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.rating.EDR;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.billing.impl.EdrService;
import org.meveo.service.catalog.impl.TaxService;
import org.meveo.service.script.Script;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetExtraParamValueScript extends org.meveo.service.script.Script {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3883274241247376940L;
	private static final Logger log = LoggerFactory.getLogger(GetExtraParamValueScript.class);
	EdrService edrService = (EdrService) getServiceInterface(EdrService.class.getSimpleName());

	public void execute(Map<String, Object> context) throws BusinessException {
		log.info("EXECUTE context {}", context);
		WalletOperation wo = (WalletOperation) context.get(Script.CONTEXT_ENTITY);
		String paramCode = (String) context.get("paramCode");
		log.info("edr ID = {}", wo.getEdr().getId());
		EDR edr = wo.getEdr();
		try {
			if (edr != null) {
				log.info("paramCode={},edr.extraParameter={}", paramCode, edr.getExtraParameter());

				JSONParser parser = new JSONParser();
				JSONObject extraParameter = (JSONObject) parser.parse(edr.getExtraParameter());

				log.info("extraParameter : {}", extraParameter);
				if (extraParameter == null) {
					log.info("extraParameter is null");
					throw new BusinessException("extraParameter is null");
				}

				if ("CD_TYPE_MVT".equals(paramCode) && extraParameter.get("NBFOINIT") != null) {
					Long nbrFibre = (Long) extraParameter.get("NBFOINIT");
					log.info("NBFOINIT={}", nbrFibre);
					if (nbrFibre > 0) {
						extraParameter.put("CD_TYPE_MVT", "MODIFICATION");
					} else {
						extraParameter.put("CD_TYPE_MVT", "CREATION");
					}
					// resultat extraParam : {"NBFOINIT":0,"CD_TYPE_MVT":"CREATION"}
					edr.setExtraParameter(extraParameter.toJSONString());
					edrService.update(edr);

				} else if ("CD_ATT_TYPE_COFI".equals(paramCode) && extraParameter.get("CD_DATE_INSTALLATION") != null
						&& extraParameter.get("CD_DATE_COMMANDE") != null) {
					Date installationDate = DateUtils.parseDateWithPattern((String)extraParameter.get("CD_DATE_INSTALLATION"), "yyyy-MM-dd");
					Date orderDate = DateUtils.parseDateWithPattern((String)extraParameter.get("CD_DATE_COMMANDE"), "yyyy-MM-dd");
					log.info("installation date: {}", installationDate);
					log.info("order date: {}", orderDate);
					if (orderDate.before(installationDate)) {
						extraParameter.put("CD_ATT_TYPE_COFI", "ABINITIO");
					} else {
						extraParameter.put("CD_ATT_TYPE_COFI", "EXPOST");
					}
					edr.setExtraParameter(extraParameter.toJSONString());
					edrService.update(edr);
				} else {
					log.error("paramValue {} is not recognized.", paramCode);
				}

				String paramValue = (String) extraParameter.get(paramCode);
				log.info("paramValue: {}", paramValue);
				log.info("Script.RESULT_VALUE: {}", Script.RESULT_VALUE);
				context.put(Script.RESULT_VALUE, paramValue);
			} else {
				context.put(Script.RESULT_VALUE, "MODIFICATION");
			}

		} catch (BusinessException | ParseException e) {
			log.error("Exception:", e);
			throw new BusinessException(e.getMessage());
		}
	}
}
