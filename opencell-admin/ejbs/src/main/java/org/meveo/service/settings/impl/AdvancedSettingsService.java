package org.meveo.service.settings.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.model.settings.AdvancedSettings;
import org.meveo.service.base.BusinessService;

@Stateless
public class AdvancedSettingsService extends BusinessService<AdvancedSettings> {

	@Override
	public void create(AdvancedSettings entity) throws BusinessException {
		checkParameters(entity);
		super.create(entity);
	}

	@Override
	public AdvancedSettings update(AdvancedSettings entity) throws BusinessException {
		checkParameters(entity);
		return super.update(entity);
	}
	
	public <T>  Map<String, T> getAdvancedSettingsMapByGroup(String group, Class<T> valueType) {
        TypedQuery<AdvancedSettings> configQuery = getEntityManager().createNamedQuery("AdvancedSettings.getGroupConfiguration", AdvancedSettings.class);
        configQuery.setParameter("group", group);
        List<AdvancedSettings> configurationList = configQuery.getResultList();
        Map<String, T> map = configurationList.stream().collect(Collectors.toMap(AdvancedSettings::getCode, advancedSettings -> parseValue(advancedSettings)));
        return map;
	}

	private void checkParameters(AdvancedSettings setting) {
		parseValue(setting);
	}

	public Object getParameter(String code) {
		AdvancedSettings setting = findByCode(code);
		return setting == null ? null : parseValue(setting);

	}

	/**
	 * Parse value by type
	 * 
	 * @param setting
	 * @return the object parsed
	 */
	public <T> T parseValue(AdvancedSettings setting) {
		String value = setting.getValue();
		String clazzName = setting.getType();
		try {
			Class<T> clazz = (Class<T>) Class.forName(clazzName);
			return clazz.getConstructor(new Class[] { String.class }).newInstance(value);
		} catch (Exception e) {
			throw new InvalidParameterException(String.format("Failed to parse %s as %s", value, clazzName));
		}
	}
}
