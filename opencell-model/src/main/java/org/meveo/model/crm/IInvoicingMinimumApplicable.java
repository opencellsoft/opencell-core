/**
 * 
 */
package org.meveo.model.crm;

import org.meveo.model.admin.Seller;
import org.meveo.model.article.AccountingArticle;

public interface IInvoicingMinimumApplicable {

	public String getMinimumLabelEl();

	public String getMinimumAmountEl();

	public AccountingArticle getMinimumArticle();

	public Seller getSeller();

	public Long getId();
	
	public String getCode();
}
