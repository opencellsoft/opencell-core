package org.meveo.service.script.revenue;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Asynchronous;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.payments.RevenueSchedule;
import org.meveo.model.scripts.RevenueRecognitionScript;
import org.meveo.model.scripts.ScriptSourceTypeEnum;
import org.meveo.service.payments.impl.RevenueScheduleService;
import org.meveo.service.script.CustomScriptService;

@Singleton
@Startup
public class RevenueRecognitionScriptService
		extends CustomScriptService<RevenueRecognitionScript, RevenueRecognitionScriptInterface> {

	@Inject
	private ResourceBundle resourceMessages;

	@Inject
	private RevenueScheduleService revenueScheduleService;

	@Override
	public void create(RevenueRecognitionScript revenueRecognitionScript, User creator) throws BusinessException {
		String className = getClassName(revenueRecognitionScript.getScript());
		if (className == null) {
			throw new BusinessException(resourceMessages.getString("message.OfferModelScript.sourceInvalid"));
		}
		revenueRecognitionScript.setCode(getFullClassname(revenueRecognitionScript.getScript()));

		super.create(revenueRecognitionScript, creator);
	}

	public RevenueRecognitionScript update(RevenueRecognitionScript revenueRecognitionScript, User updater)
			throws BusinessException {
		String className = getClassName(revenueRecognitionScript.getScript());
		if (className == null) {
			throw new BusinessException(resourceMessages.getString("message.OfferModelScript.sourceInvalid"));
		}
		revenueRecognitionScript.setCode(getFullClassname(revenueRecognitionScript.getScript()));

		revenueRecognitionScript = super.update(revenueRecognitionScript, updater);

		return revenueRecognitionScript;
	}

	@PostConstruct
	void compileAll() {
		List<RevenueRecognitionScript> revenueRecognitionScripts = findByType(ScriptSourceTypeEnum.JAVA);
		compile(revenueRecognitionScripts);
	}

	@Asynchronous
	public void scheduleRevenue(ChargeInstance chargeInstance, User currentUser) throws BusinessException {
		RevenueRecognitionScript script = chargeInstance.getChargeTemplate().getRevenueRecognitionScript();
/*
		if (chargeInstance.getSubscription() != null) {
			if (script != null) {
				List<RevenueSchedule> revenueScheduleList = script.scheduleRevenue(chargeInstance, startDate, endDate,
						currentUser);
				if (revenueScheduleList != null && revenueScheduleList.size() > 0) {
					getEntityManager().createNamedQuery("RevenueSchedule.deleteForChargeInstance")
							.setParameter("chargeInstance", chargeInstance)
							.setParameter("provider", currentUser.getProvider()).executeUpdate();
					for (RevenueSchedule schedule : revenueScheduleList) {
						revenueScheduleService.create(schedule, currentUser);
					}
				}
			}
		}
		*/
	}

}
