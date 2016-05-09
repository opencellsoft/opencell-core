package org.meveo.service.script.revenue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.admin.User;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.finance.RevenueSchedule;
import org.meveo.service.finance.RevenueScheduleService;
import org.meveo.service.script.Script;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class RevenueRecognitionScript extends Script implements RevenueRecognitionScriptInterface{

	
	RevenueScheduleService revenueScheduleService= (RevenueScheduleService)getServiceInterface("revenueScheduleService");

    protected Logger log = LoggerFactory.getLogger(this.getClass());
    	
	public void createRevenueSchedule(ChargeInstance chargeInstance,User currentUser) throws BusinessException{
		List<WalletOperation> woList = new ArrayList<WalletOperation>(chargeInstance.getWalletOperations());
        Collections.sort(woList, new Comparator<WalletOperation>() {
             public int compare(WalletOperation c0, WalletOperation c1) {
                 return c0.getOperationDate().compareTo(c1.getOperationDate());
             }
        });
		if(woList==null || woList.size()==0){
			log.warn("createRevenueSchedule list of wallet operations is empty for charge instance {}",chargeInstance.getId());
		} else {
			BigDecimal contractAmount = BigDecimal.ZERO;
			for(WalletOperation wo:woList){
				if(wo.getAmountWithoutTax()!=null){
					contractAmount.add(wo.getAmountWithoutTax());
				}
			}
			log.debug("createRevenueSchedule contractAmount={}",contractAmount);
			Date startDate = chargeInstance.getSubscription().getSubscriptionDate();
			Date endDate = chargeInstance.getSubscription().getEndAgreementDate();
			List<RevenueSchedule> schedule = new ArrayList<>(scheduleRevenue(chargeInstance,woList, startDate, endDate, currentUser));
			if(schedule.size()>0){
				Collections.sort(schedule, new Comparator<RevenueSchedule>() {
		             public int compare(RevenueSchedule c0, RevenueSchedule c1) {
		                 return c0.getRevenueDate().compareTo(c1.getRevenueDate());
		             }
		        });
				log.debug("createRevenueSchedule schedule={}",schedule);
				BillingAccount billingAccount= chargeInstance.getSubscription().getUserAccount()
						.getBillingAccount();
				BillingCycle billingCycle = billingAccount.getBillingCycle();
		        List<Date> invoicingDates = new ArrayList<>();
		        Date initCalendarDate = billingAccount.getSubscriptionDate();
				if(initCalendarDate==null){
					initCalendarDate=billingAccount.getAuditable().getCreated();
				}
		        Date invoicingDate = new Date(startDate.getTime());
		        while(!billingCycle.getNextCalendarDate(initCalendarDate,invoicingDate).after(endDate)){
		        	invoicingDates.add(invoicingDate);
		        	invoicingDate = new Date((billingCycle.getNextCalendarDate(initCalendarDate,invoicingDate)).getTime());
		        }
		        BigDecimal recognizedRevenue=BigDecimal.ZERO;
		        BigDecimal chargedRevenue=BigDecimal.ZERO;
		        int chargeIndex=0;
		        BigDecimal invoicedRevenue=BigDecimal.ZERO;
		        int invoiceIndex=0;
		        for(RevenueSchedule revenueSchedule : schedule){
		        	recognizedRevenue.add(revenueSchedule.getRecognizedRevenue());
		        	while(chargeIndex<woList.size() && !woList.get(chargeIndex).getOperationDate().after(revenueSchedule.getRevenueDate())){
		        		chargedRevenue.add(woList.get(chargeIndex).getAmountWithoutTax());
		        		chargeIndex++;
		        	}
		        	if(invoiceIndex<invoicingDates.size() && !invoicingDates.get(invoiceIndex).after(revenueSchedule.getRevenueDate())){
		        		invoicedRevenue=chargedRevenue.add(BigDecimal.ZERO);
		        	}
		        	revenueSchedule.setInvoicedRevenue(invoicedRevenue);
		        	if(recognizedRevenue.compareTo(invoicedRevenue)<=0){
		        		revenueSchedule.setAccruedRevenue(invoicedRevenue.subtract(recognizedRevenue));
		        		revenueSchedule.setDefferedRevenue(BigDecimal.ZERO);
		        	} else {
		        		revenueSchedule.setAccruedRevenue(BigDecimal.ZERO);
		        		revenueSchedule.setDefferedRevenue(recognizedRevenue.subtract(invoicedRevenue));
		        	}
		        	revenueScheduleService.create(revenueSchedule, currentUser);
		        }
			} else {
				log.debug("createRevenueSchedule no schedule created for chargeInstance {}",chargeInstance.getId());
			}
		}
	}
}
