package org.meveo.service.job;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

import org.meveo.model.jobs.TimerEntity;


@FacesConverter(forClass=TimerEntity.class,value="timerEntityConverter")
public class TimerEntityConverter implements Converter {

	@Inject
	TimerEntityService timerEntityService;
	
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
   	 if (value == null || value.length() == 0) {
         return null;
     }
   	 return timerEntityService.findById(Long.parseLong(value));
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {

    	TimerEntity timerEntity = (TimerEntity) value;
        return timerEntity==null?"":timerEntity.getId().toString();
    }
}