package org.meveo.admin.job;

import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.meveo.admin.exception.BusinessException;
import org.meveo.service.script.ScriptInterface;

@Stateless
public class UnitFlatFileProcessingJobBean {

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void execute(ScriptInterface script, Map<String, Object> executeParams) throws BusinessException {
        script.execute(executeParams);
    }

}
