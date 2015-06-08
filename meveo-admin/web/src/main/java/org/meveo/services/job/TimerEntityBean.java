package org.meveo.services.job;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.jobs.TimerEntity;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.job.TimerEntityService;
import org.omnifaces.cdi.ViewScoped;

@Named
@ViewScoped
public class TimerEntityBean extends BaseBean<TimerEntity> {

    private static final long serialVersionUID = 1L;

    @Inject
    TimerEntityService timerEntityservice;

   

    public TimerEntityBean() {
        super(TimerEntity.class);
    }


    @Override
    protected IPersistenceService<TimerEntity> getPersistenceService() {
        return timerEntityservice;
    }

    public List<TimerEntity> getTimerEntityList() {
        return timerEntityservice.list();
    }

  
    protected String getListViewName() {
        return "timerEntities";
    }


}