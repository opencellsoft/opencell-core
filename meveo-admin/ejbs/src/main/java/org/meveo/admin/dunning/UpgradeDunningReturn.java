package org.meveo.admin.dunning;

import java.util.ArrayList;
import java.util.List;

import org.meveo.model.payments.ActionDunning;
import org.meveo.model.payments.OtherCreditAndCharge;

public class UpgradeDunningReturn {

	
	private boolean upgraded=false;
	private List<ActionDunning> listActionDunning = new ArrayList<ActionDunning>();
	private List<OtherCreditAndCharge> listOCC = new ArrayList<OtherCreditAndCharge>();
	
	
	
	public boolean isUpgraded() {
		return upgraded;
	}
	public void setUpgraded(boolean upgraded) {
		this.upgraded = upgraded;
	}
	public List<ActionDunning> getListActionDunning() {
		return listActionDunning;
	}
	public void setListActionDunning(List<ActionDunning> listActionDunning) {
		this.listActionDunning = listActionDunning;
	}
	public List<OtherCreditAndCharge> getListOCC() {
		return listOCC;
	}
	public void setListOCC(List<OtherCreditAndCharge> listOCC) {
		this.listOCC = listOCC;
	}
	
	
}
