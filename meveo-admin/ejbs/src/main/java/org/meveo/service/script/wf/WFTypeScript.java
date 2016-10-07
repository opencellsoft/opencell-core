package org.meveo.service.script.wf;

import java.util.List;
import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.wf.WorkflowType;
import org.meveo.admin.wf.WorkflowTypeClass;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.User;

@WorkflowTypeClass
public class WFTypeScript<E extends BusinessEntity> extends WorkflowType<E> implements WFTypeScriptInterface {
	

	public WFTypeScript() {
		super();
	}

	public WFTypeScript(E e) {
		super(e);
	}

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

	@Override
	public List<String> getStatusList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void changeStatus(String newStatus, User currentUser) throws BusinessException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getActualStatus() {
		// TODO Auto-generated method stub
		return null;
	}
}