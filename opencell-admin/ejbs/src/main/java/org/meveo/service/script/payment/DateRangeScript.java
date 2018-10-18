package org.meveo.service.script.payment;

import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.DateRange;
import org.meveo.service.script.ScriptInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An abstract class extending ScriptInterface, aimed to compute a date range by a custom script
 * 
 * @author Said Ramli
 */
public abstract class DateRangeScript implements ScriptInterface {
    
    protected Logger log = LoggerFactory.getLogger(this.getClass());
    
    @Override
    public void init(Map<String, Object> methodContext) throws BusinessException {
    }

    @Override
    public void execute(Map<String, Object> methodContext) throws BusinessException {
    }

    @Override
    public void finalize(Map<String, Object> methodContext) throws BusinessException {
    }

    public abstract DateRange computeDateRange(Map<String, Object> methodContext);
}
