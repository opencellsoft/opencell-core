package org.meveo.admin.job;

import java.io.File;
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

import org.meveo.admin.sepa.SepaService;
import org.meveo.commons.utils.FileUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResult;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.TimerInfo;
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
public class SepaRejectedTransactionsJob
  implements Job
{
  private Logger logger = Logger.getLogger(SepaRejectedTransactionsJob.class.getName());
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
  SepaService sepaService;
  

	ParamBean param = ParamBean.getInstance("meveo-admin.properties");
  
  String importDir = param.getProperty("sepaRejectedTransactionsJob.importDir",
			"/tmp/meveo/SepaRejectedTransactions");
  
  @PostConstruct
  public void init()
  {
    TimerEntityService.registerJob(this);
  }
  
  public JobExecutionResult execute(String parameter, Provider provider)
  {
    logger.info("execute SepaDirectDebitJob.");
    
    
    String dirIN = importDir + File.separator + provider.getCode()
			+ File.separator + "rejectedSepaTransactions" + File.separator + "input";
    logger.info("dirIN=" + dirIN);
	String dirOK = importDir + File.separator + provider.getCode()
			+ File.separator + "rejectedSepaTransactions" + File.separator + "output";
	String dirKO = importDir + File.separator + provider.getCode()
			+ File.separator + "rejectedSepaTransactions" + File.separator + "reject";
	String prefix = param.getProperty("sepaRejectedTransactionsJob.file.prefix",
			"Pain002_");
	String ext = param.getProperty("sepaRejectedTransactionsJob.file.extension",
			"xml");
    
    JobExecutionResultImpl result = new JobExecutionResultImpl();
    
    try
    {
    	
    	File dir = new File(dirIN);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		List<File> files = sepaService.getFilesToProcess(dir, prefix, ext);
		int numberOfFiles = files.size();
		logger.info("InputFiles job " + numberOfFiles + " to import");
		result.setNbItemsToProcess(numberOfFiles);
		for (File file : files) {
			File currentFile = null;
			try {
				logger.info("InputFiles job " + file.getName() + " in progres");
				currentFile = FileUtils.addExtension(file, ".processing");
				sepaService.processRejectFile(currentFile, file.getName(), provider);
				FileUtils.moveFile(dirOK, currentFile, file.getName());
				logger.info("InputFiles job " + file.getName() + " done");
				result.registerSucces();
			} catch (Exception e) {
				result.registerError(e.getMessage());
				logger.info("InputFiles job " + file.getName() + " failed");
				FileUtils.moveFile(dirKO, currentFile, file.getName());
				e.printStackTrace();
			} finally {
				if (currentFile != null)
					currentFile.delete();
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
	timerConfig.setPersistent(false);
    Timer timer = this.timerService.createCalendarTimer(scheduleExpression, timerConfig);
    return timer.getHandle();
  }
  
  boolean running = false;
  
  @Timeout
  public void trigger(Timer timer)
  {
    TimerInfo info = (TimerInfo)timer.getInfo();
    if ((!this.running) && (info.isActive())) {
      try
      {
        this.running = true;
        Provider provider = (Provider)this.providerService.findById(info.getProviderId());
        JobExecutionResult result = execute(info.getParametres(), provider);
        this.jobExecutionService.persistResult(this, result, info, provider);
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
      finally
      {
        this.running = false;
      }
    }
  }
  
  public Collection<Timer> getTimers()
  {
    return this.timerService.getTimers();
  }
  

	@Override
	public JobExecutionService getJobExecutionService() {
		return jobExecutionService;
	}
}
