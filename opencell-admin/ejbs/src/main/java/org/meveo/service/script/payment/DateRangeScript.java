package org.meveo.service.script.payment;

import java.util.Map;

import org.meveo.admin.job.DateRange;
import org.meveo.service.script.Script;

/**
 * An abstract class extending ScriptInterface, aimed to compute a date range by a custom script
 * 
 * @author Said Ramli
 */
public abstract class DateRangeScript extends Script {

    public abstract DateRange computeDateRange(Map<String, Object> methodContext);
}
