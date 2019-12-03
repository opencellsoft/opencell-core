package org.meveo.service.script.wf;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.wf.WorkflowType;
import org.meveo.admin.wf.WorkflowTypeClass;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.service.script.RunTimeLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WorkflowTypeClass
public class WFTypeScript<E extends BusinessEntity> extends WorkflowType<E> implements Serializable, WFTypeScriptInterface {

    private static final long serialVersionUID = -9103159611108102999L;

    /**
     * A logger
     */
    protected Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * A logger to replace with when running script in test mode (from GUI), so logs can be returned/visible to the end user
     */
    protected RunTimeLogger logTest = new RunTimeLogger(this.getClass());

    public WFTypeScript() {
        super();
    }

    public WFTypeScript(E e) {
        super(e);
    }

    @Override
    public void init(Map<String, Object> methodContext) throws BusinessException {
 
    }

    @Override
    public void execute(Map<String, Object> methodContext) throws BusinessException {
        throw new UnsupportedOperationException();

    }

    @Override
    public void terminate(Map<String, Object> methodContext) throws BusinessException {
        throw new UnsupportedOperationException();
    }

    protected Object getServiceInterface(String serviceInterfaceName) {
        return EjbUtils.getServiceInterface(serviceInterfaceName);
    }

    @Override
    public List<String> getStatusList() {
        return null;
    }

    @Override
    public void changeStatus(String newStatus) throws BusinessException {

    }

    @Override
    public String getActualStatus() {
        return null;
    }

    /**
     * Get log messages related to script execution (test mode run only)
     * 
     * @return Log messages
     */
    public String getLogMessages() {
        return logTest.getLog();
    }
}