package org.meveo.event;

import java.io.Serializable;

import org.meveo.model.billing.CounterPeriod;

/**
 * @author Tyshan　Shi(tyshan@manaty.net)
 * @date Aug 3, 2016 9:57:39 PM
 **/
public class CounterPeriodEvent implements Serializable {

	private static final long serialVersionUID = 3446505677645939620L;
	
	private CounterPeriod counterPeriod;

	public void setCounterPeriod(CounterPeriod counterPeriod) {
		this.counterPeriod=counterPeriod;
		
	}

	public CounterPeriod getCounterPeriod() {
		return counterPeriod;
	}

}

