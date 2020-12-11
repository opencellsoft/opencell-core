package org.meveo.service.cpq;
import javax.ejb.Stateless;
import org.meveo.model.cpq.CpqAccountingArticle;
import org.meveo.service.base.PersistenceService;
/**
 * @author Mbarek-Ay
 * @version 10.0
 * 
 * Accounting article service implementation.
 */
@Stateless
public class AccountingArticleService extends
        PersistenceService<CpqAccountingArticle> {
}
