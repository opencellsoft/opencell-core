package org.meveo.admin.job;

import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.ScheduleExpression;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerHandle;
import javax.ejb.TimerService;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessEntityException;
import org.meveo.admin.sepa.SepaService;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResult;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.TimerInfo;
import org.meveo.model.payments.DDRequestLotOp;
import org.meveo.model.payments.DDRequestOpEnum;
import org.meveo.model.payments.DDRequestOpStatusEnum;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.service.payments.impl.DDRequestLotOpService;
import org.meveo.services.job.Job;
import org.meveo.services.job.JobExecutionService;
import org.meveo.services.job.TimerEntityService;

/**
 * @author R.AITYAAZZA
 *
 */
@Startup
@Singleton
public class SepaDirectDebitJob
  implements Job
{
  private Logger logger = Logger.getLogger(SepaDirectDebitJob.class.getName());
  @Resource
  TimerService timerService;
  @Inject
  private ProviderService providerService;
  @Inject
  JobExecutionService jobExecutionService;
  @Inject
  DDRequestLotOpService dDRequestLotOpService;
  @Inject
  UserService userService;
  @Inject
  SepaService SepaService;
  
  @PostConstruct
  public void init()
  {
    TimerEntityService.registerJob(this);
  }
  
  public JobExecutionResult execute(String parameter, Provider provider)
  {
    logger.info("execute SepaDirectDebitJob.");
    JobExecutionResultImpl result = new JobExecutionResultImpl();
    User user = null;
    try
    {
      user = userService.getSystemUser();
      List<DDRequestLotOp> ddrequestOps = dDRequestLotOpService.getDDRequestOps();
      logger.info("ddrequestOps founded:" + ddrequestOps.size());
      for (DDRequestLotOp ddrequestLotOp : ddrequestOps)
      {
        try
        {
          if (ddrequestLotOp.getDdrequestOp() == DDRequestOpEnum.CREATE) {
            SepaService.createDDRquestLot(ddrequestLotOp.getFromDueDate(), ddrequestLotOp.getToDueDate(), user, ddrequestLotOp.getProvider());
          }else if (ddrequestLotOp.getDdrequestOp() == DDRequestOpEnum.FILE) {
            SepaService.exportDDRequestLot(ddrequestLotOp.getDdrequestLOT().getId());
          }
          ddrequestLotOp.setStatus(DDRequestOpStatusEnum.PROCESSED);
        }catch (BusinessEntityException e)
        {
            ddrequestLotOp.setStatus(DDRequestOpStatusEnum.ERROR);
            ddrequestLotOp.setErrorCause(StringUtils.truncate(e.getMessage(), 255, true));
        }catch (Exception e)
        {
          e.printStackTrace();
          ddrequestLotOp.setStatus(DDRequestOpStatusEnum.ERROR);
          ddrequestLotOp.setErrorCause(StringUtils.truncate(e.getMessage(), 255, true));
        }
       
      }
    }catch(Exception e){
    	e.printStackTrace();
    	
    }
    
    result.close("");
    return result;
  }
  
  public TimerHandle createTimer(ScheduleExpression scheduleExpression, TimerInfo infos)
  {
    TimerConfig timerConfig = new TimerConfig();
    timerConfig.setInfo(infos);
	//timerConfig.setPersistent(false);
    Timer timer = timerService.createCalendarTimer(scheduleExpression, timerConfig);
    return timer.getHandle();
  }
  
  boolean running = false;
  
  @Timeout
  public void trigger(Timer timer)
  {
    TimerInfo info = (TimerInfo)timer.getInfo();
    if ((!running) && (info.isActive())) {
      try
      {
        running = true;
        Provider provider = (Provider)providerService.findById(info.getProviderId());
        JobExecutionResult result = execute(info.getParametres(), provider);
        jobExecutionService.persistResult(this, result, info, provider);
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
      finally
      {
        running = false;
      }
    }
  }
  
  public Collection<Timer> getTimers()
  {
    return timerService.getTimers();
  }

	@Override
	public JobExecutionService getJobExecutionService() {
		return jobExecutionService;
	}
}
