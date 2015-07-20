package org.meveo.model.jobs;

import java.util.Date;
import java.util.List;

public interface JobExecutionResult {
    public Date getStartDate();
    public Date getEndDate();
    
    /**
     * @return return -1 if this value has no meaning
     */
    public long getNbItemsToProcess();
    
    public long getNbItemsCorrectlyProcessed();
    
    public long getNbItemsProcessedWithWarning();
    
    public long getNbItemsProcessedWithError();
    
    /**
     * @return return null if warnings are available somewhere else (for example in a file)
     */
    public List<String> getWarnings();

    /**
     * @return return null if errors are available somewhere else (for example in a file)
     */
    public List<String> getErrors();
    
    /**
     * 
     * @return true if the job didn't detect anything else to do. If false the Jobservice will execute it again immediatly
     */
    public boolean isDone();
    
    /**
     * @return general report displayed in GUI, put here info that do not fit other places
     */
    public String getReport();
    
    public Long getId();
    
    public void setId(Long id);
    
}
