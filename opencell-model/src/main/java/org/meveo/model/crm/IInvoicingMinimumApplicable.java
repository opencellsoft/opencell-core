/**
 * 
 */
package org.meveo.model.crm;

import java.io.Serializable;

import org.meveo.model.admin.Seller;
import org.meveo.model.article.AccountingArticle;

public interface IInvoicingMinimumApplicable extends Serializable {

	public String getMinimumLabelEl();

	public String getMinimumAmountEl();

	public AccountingArticle getMinimumArticle();

	public Seller getSeller();

	public Long getId();
	
	public String getCode();
}
