package org.meveo.service.script.wf;

import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.wf.WorkflowType;
import org.meveo.admin.wf.WorkflowTypeClass;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.model.BaseEntity;
import org.meveo.model.admin.User;

@WorkflowTypeClass
public abstract class WFTypeScript<E extends BaseEntity> extends WorkflowType<E> implements WFTypeScriptInterface {

    @Override
    public void init(Map<String, Object> methodContext, User user) throws BusinessException {
        throw new UnsupportedOperationException();

    }

    @Override
    public void execute(Map<String, Object> methodContext, User user) throws BusinessException {
        throw new UnsupportedOperationException();

    }

    @Override
    public void finalize(Map<String, Object> methodContext, User user) throws BusinessException {
        throw new UnsupportedOperationException();
    }

    protected Object getServiceInterface(String serviceInterfaceName) {
        return EjbUtils.getServiceInterface(serviceInterfaceName);
    }
}