package org.meveo.commons.utils;

import java.util.List;

import org.meveo.model.module.MeveoModule;
import org.meveo.service.admin.impl.MeveoModuleService;

public class MeveoModuleUtil {
	/**
	 * find related modules by items
	 * @param meveoModuleService
	 * @param code
	 * @param type
	 * @param appliesTo
	 * @return
	 */
	public static String generateModules(MeveoModuleService meveoModuleService,String code,String type,String appliesTo){
		List<MeveoModule> modules=meveoModuleService.findModuleByItemCodeAndClazzAppliesTo(code, type, appliesTo);
		if(modules!=null){
			StringBuilder sb=new StringBuilder();
			int i=0;
			for(MeveoModule module:modules){
				if(i!=0){
					sb.append(";");
				}
				sb.append(module.getCode());
				i++;
			}
			return sb.toString();
		}
		return null;
	}
}
