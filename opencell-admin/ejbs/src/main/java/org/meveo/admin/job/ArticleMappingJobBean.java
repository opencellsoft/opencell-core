package org.meveo.admin.job;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.Query;

import org.meveo.admin.async.SynchronizedIterator;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.billing.AccountingArticleAssignmentItem;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.article.AccountingArticleService;

@Stateless
public class ArticleMappingJobBean extends IteratorBasedScopedJobBean<AccountingArticleAssignmentItem> {

    private static final long serialVersionUID = 1402462714195024317L;

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
        super.execute(jobExecutionResult, jobInstance, this::initJobAndGetDataToProcess, null, this::assignAccountingArticle, this::assignAccountingArticles, null, null, null);
    }

    private Optional<Iterator<AccountingArticleAssignmentItem>> initJobAndGetDataToProcess(JobExecutionResultImpl jobExecutionResult) {
        return getIterator(jobExecutionResult);
    }

    private void assignAccountingArticles(List<AccountingArticleAssignmentItem> items, JobExecutionResultImpl jobExecutionResult) {

        for (AccountingArticleAssignmentItem item : items) {
            assignArticle(item);
        }
    }

    private void assignAccountingArticle(AccountingArticleAssignmentItem item, JobExecutionResultImpl jobExecutionResult) {
        assignArticle(item);
    }

    private void assignArticle(AccountingArticleAssignmentItem item) {
        AccountingArticle article = accountingArticleService.getAccountingArticle(item.getServiceInstanceId(), item.getChargeTemplateId(), item.getOfferTemplateId());
        ratedTransactionService.updateAccountingArticlesByChargeInstanceIdsOrOtherCriterias(item.getChargeInstancesIDs(), item.getServiceInstanceId(), item.getOfferTemplateId(), article);
    }

    @Override
    protected boolean isProcessItemInNewTx() {
        return false;
    }

    private Optional<Iterator<AccountingArticleAssignmentItem>> getSynchronizedIterator(JobExecutionResultImpl jobExecutionResult, int jobItemsLimit) {
        Query query = emWrapper.getEntityManager().createNamedQuery("RatedTransaction.getMissingAccountingArticleInputs");
        if (jobItemsLimit > 0) {
            query = query.setMaxResults(jobItemsLimit);
        }
        return Optional.of(new SynchronizedIterator<AccountingArticleAssignmentItem>(query.getResultList()));
    }

    @Override
    Optional<Iterator<AccountingArticleAssignmentItem>> getSynchronizedIteratorWithLimit(JobExecutionResultImpl jobExecutionResult, int jobItemsLimit) {
        return getSynchronizedIterator(jobExecutionResult, jobItemsLimit);
    }

    @Override
    Optional<Iterator<AccountingArticleAssignmentItem>> getSynchronizedIterator(JobExecutionResultImpl jobExecutionResult) {
        return getSynchronizedIterator(jobExecutionResult, 0);
    }
}