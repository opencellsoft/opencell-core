package org.meveo.admin.action;

import org.meveo.model.AccountEntity;
import org.primefaces.component.tabview.Tab;
import org.primefaces.event.TabChangeEvent;

public abstract class AccountBean<T extends AccountEntity> extends
		BaseBean<T> {

	private static final long serialVersionUID = 3407699633028715707L;
	
	private int activeTab=0;

	public AccountBean() {

	}

	public AccountBean(Class<T> clazz) {
		super(clazz);
	}
	
	protected void onTabChange(TabChangeEvent event){
		Tab currentTab=event.getTab();
		if(currentTab!=null){
			if("tab0".equals(currentTab.getId())){
				this.activeTab=0;
			}else if("tab1".equals(currentTab.getId())){
				this.activeTab=1;
			}else if("tab1".equals(currentTab.getId())){
				this.activeTab=2;
			}else if("tab2".equals(currentTab.getId())){
				this.activeTab=3;
			}else if("tab3".equals(currentTab.getId())){
				this.activeTab=4;
			}
		}
	}
	protected int getActiveTab(){
		return this.activeTab;
	}
	protected void setActiveTab(int activeTab){
		//
	}


}
