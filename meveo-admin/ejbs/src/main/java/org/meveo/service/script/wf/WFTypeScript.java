package org.meveo.service.script.wf;

import java.util.List;
import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.wf.WorkflowType;
import org.meveo.admin.wf.WorkflowTypeClass;
import org.meveo.model.BaseEntity;
import org.meveo.model.admin.User;

@WorkflowTypeClass
public class WFTypeScript<E extends BaseEntity> extends WorkflowType<E> implements WFTypeScriptInterface {

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

	@Override
	public List<String> getStatusList() {
		return null;
	}

	@Override
	public void changeStatus(String newStatus) {
	}

	@Override
	public String getActualStatus() {
		return null;
	}

}
