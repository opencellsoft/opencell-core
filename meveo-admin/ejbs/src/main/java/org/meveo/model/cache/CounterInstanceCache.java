package org.meveo.model.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.meveo.model.billing.CounterInstance;
import org.meveo.model.billing.CounterPeriod;

/**
 * 
 * A View of CounterInstance stored in Data Grid with its entity counterpart stored in Database
 *
 */
public class CounterInstanceCache implements Serializable{

	private static final long serialVersionUID = 4237398308951202639L;
	
	private Long counterInstanceId;
	private List<CounterPeriodCache> counterPeriods;

	private CounterInstanceCache(){
		
	}

	public List<CounterPeriodCache> getCounterPeriods() {
		return counterPeriods;
	}

	public void setCounterPeriods(List<CounterPeriodCache> counterPeriods) {
		this.counterPeriods = counterPeriods;
	}

	/**
	 * Create a cache view of a counter entity
	 * it will store all the periods in a ascending order by startDate
	 * for a fast selection during rating
	 * @param counter
	 * @return
	 */
	public static CounterInstanceCache getInstance(CounterInstance counter) {
		CounterInstanceCache cacheValue = new CounterInstanceCache();
		
		cacheValue.counterInstanceId=counter.getId();
		if(counter.getCounterPeriods()!=null && counter.getCounterPeriods().size()>0){
			cacheValue.counterPeriods =  new ArrayList<CounterPeriodCache>(counter.getCounterPeriods().size());
			for(CounterPeriod counterPeriod : counter.getCounterPeriods()){
				CounterPeriodCache periodCache = CounterPeriodCache.getInstance(counterPeriod, counter.getCounterTemplate());
				boolean added=false;
				for(int i=0;i<cacheValue.counterPeriods.size();i++){
					if(cacheValue.counterPeriods.get(i).getStartDate().after(periodCache.getStartDate())){
						cacheValue.counterPeriods.add(i,periodCache);
						added=true;
						break;
					}
				}
				if(!added){
					cacheValue.counterPeriods.add(periodCache);
				}
			}
		}
		return cacheValue;
	}

	public Long getCounterInstanceId(){
		return counterInstanceId;
	}
}