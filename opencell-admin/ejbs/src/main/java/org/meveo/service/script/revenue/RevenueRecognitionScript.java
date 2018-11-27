package org.meveo.service.script.revenue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.finance.RevenueSchedule;
import org.meveo.service.finance.RevenueScheduleService;
import org.meveo.service.script.Script;

public abstract class RevenueRecognitionScript extends Script implements RevenueRecognitionScriptInterface{

	
	RevenueScheduleService revenueScheduleService= (RevenueScheduleService)getServiceInterface("RevenueScheduleService");

    public void createRevenueSchedule(ChargeInstance chargeInstance) throws BusinessException{
		List<WalletOperation> woList = new ArrayList<WalletOperation>(chargeInstance.getWalletOperations());
		log.debug("woList {}",woList.size());
        Collections.sort(woList, new Comparator<WalletOperation>() {
             public int compare(WalletOperation c0, WalletOperation c1) {
                 return c0.getOperationDate().compareTo(c1.getOperationDate());
             }
        });
        //log.debug("sorted woList {}",woList);
        if(woList==null || woList.size()==0){
			log.warn("createRevenueSchedule list of wallet operations is empty for charge instance {}",chargeInstance.getId());
		} else {
			Date startDate = chargeInstance.getSubscription().getSubscriptionDate();
			Date endDate = chargeInstance.getSubscription().getEndAgreementDate();
			List<RevenueSchedule> schedule = new ArrayList<>(scheduleRevenue(chargeInstance,woList, startDate, endDate));
			if(schedule.size()>0){
				Collections.sort(schedule, new Comparator<RevenueSchedule>() {
		             public int compare(RevenueSchedule c0, RevenueSchedule c1) {
		                 return c0.getRevenueDate().compareTo(c1.getRevenueDate());
		             }
		        });
				BillingAccount billingAccount= chargeInstance.getUserAccount()
						.getBillingAccount();
				BillingCycle billingCycle = billingAccount.getBillingCycle();
		        List<Date> invoicingDates = new ArrayList<>();
		        Date initCalendarDate = billingAccount.getSubscriptionDate();
				if(initCalendarDate==null){
					initCalendarDate=billingAccount.getAuditable().getCreated();
				}
		        Date invoicingDate = new Date(startDate.getTime());
		        invoicingDate = billingCycle.getNextCalendarDate(initCalendarDate,invoicingDate);
		        while(!billingCycle.getNextCalendarDate(initCalendarDate,invoicingDate).after(endDate)){
		        	invoicingDates.add(invoicingDate);
		        	log.debug("added invoicingDate "+invoicingDate);
		        	invoicingDate = new Date((billingCycle.getNextCalendarDate(initCalendarDate,invoicingDate)).getTime());
		        }
		        BigDecimal recognizedRevenue=BigDecimal.ZERO;
		        BigDecimal chargedRevenue=BigDecimal.ZERO;
		        int chargeIndex=0;
		        BigDecimal invoicedRevenue=BigDecimal.ZERO;
		        BigDecimal negativeOne = new BigDecimal(-1);
		        int invoiceIndex=0;
		        for(RevenueSchedule revenueSchedule : schedule){
		        	recognizedRevenue=recognizedRevenue.add(revenueSchedule.getRecognizedRevenue());
		        	while(chargeIndex<woList.size() && !woList.get(chargeIndex).getOperationDate().after(revenueSchedule.getRevenueDate())){
		        		chargedRevenue=chargedRevenue.add(woList.get(chargeIndex).getAmountWithoutTax());
		        		chargeIndex++;
		        	}
		        	if(invoiceIndex<invoicingDates.size() && !invoicingDates.get(invoiceIndex).after(revenueSchedule.getRevenueDate())){
		        		invoicedRevenue=chargedRevenue.add(BigDecimal.ZERO);
		        	}
		        	revenueSchedule.setRecognizedRevenue(recognizedRevenue.multiply(negativeOne));
		        	revenueSchedule.setInvoicedRevenue(invoicedRevenue);
		        	if(recognizedRevenue.compareTo(invoicedRevenue)<=0){
		          		revenueSchedule.setAccruedRevenue(BigDecimal.ZERO);
		        		revenueSchedule.setDefferedRevenue((recognizedRevenue.subtract(invoicedRevenue)));
		        	} else {
		          		revenueSchedule.setAccruedRevenue((invoicedRevenue.subtract(recognizedRevenue)).multiply(negativeOne));
		        		revenueSchedule.setDefferedRevenue(BigDecimal.ZERO);
		        	}
		        	revenueScheduleService.create(revenueSchedule);
		        }
			} else {
				log.debug("createRevenueSchedule no schedule created for chargeInstance {}",chargeInstance.getId());
			}
		}
	}
}
