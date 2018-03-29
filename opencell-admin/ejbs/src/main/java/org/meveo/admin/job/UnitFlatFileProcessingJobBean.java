package org.meveo.admin.job;
import java.util.Map;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import org.meveo.admin.exception.BusinessException;
import org.meveo.service.script.ScriptInterface;
/**
 * The Class UnitFlatFileProcessingJobBean execute one line/record, in a new transaction.
 *
 * @author anasseh
 * @lastModifiedVersion 4.8.1.6
 */
@Stateless
public class UnitFlatFileProcessingJobBean {
    /**
     * Execute one line/record, in a new transaction.
     *
     * @param script script to execute
     * @param executeParams script context parameters
     * @throws BusinessException Business Exception
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void execute(ScriptInterface script, Map<String, Object> executeParams) throws BusinessException {
        script.execute(executeParams);
    }
}
