package org.meveo.admin.job;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.async.SynchronizedIterator;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.billing.AccountingArticleAssignementItem;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.article.AccountingArticleService;

@Stateless
public class ArticleMappingBean extends IteratorBasedJobBean<AccountingArticleAssignementItem> {
	
    @Inject
    private RatedTransactionService ratedTransactionService;
    
    @Inject
    private AccountingArticleService accountingArticleService;
    

    
    @Inject
    @MeveoJpa
    private EntityManagerWrapper emWrapper;


    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
    	super.execute(jobExecutionResult, jobInstance, this::initJobAndGetDataToProcess, this::assignAccountingArticle, this::assignAccountingArticles, null, null, null);
    }

    private Optional<Iterator<AccountingArticleAssignementItem>> initJobAndGetDataToProcess(JobExecutionResultImpl jobExecutionResult) {
        List<AccountingArticleAssignementItem> resultList = emWrapper.getEntityManager().createNamedQuery("RatedTransaction.getMissingAccountingArticleInputs").getResultList();
		return Optional.of(new SynchronizedIterator<AccountingArticleAssignementItem>(resultList));
    }
    
    private void assignAccountingArticles(List<AccountingArticleAssignementItem> items, JobExecutionResultImpl jobExecutionResult) {
    	AccountingArticle article=null;
    	for(AccountingArticleAssignementItem item : items) {
			assigneArticle(item,article);
    	}
    }

    private void assignAccountingArticle(AccountingArticleAssignementItem item, JobExecutionResultImpl jobExecutionResult) {
    	assigneArticle(item,null);
    }

	private void assigneArticle(AccountingArticleAssignementItem item, AccountingArticle article) {
		article = accountingArticleService.getAccountingArticle(item.getServiceInstanceId(),item.getChargeTemplateId(), item.getOfferTemplateId());
		ratedTransactionService.updateAccountingArticlesByChargeInstanceIdsOrOtherCriterias(item.getChargeInstancesIDs(),item.getServiceInstanceId(), item.getOfferTemplateId(), article);
	}

    @Override
    protected boolean isProcessItemInNewTx() {
        return false;
    }
    
}
