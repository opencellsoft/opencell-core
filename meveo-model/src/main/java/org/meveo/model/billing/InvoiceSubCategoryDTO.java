package org.meveo.model.billing;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class InvoiceSubCategoryDTO
{
  private String description;
  private String code;
  private BigDecimal amountWithoutTax = BigDecimal.ZERO;
  private BigDecimal amountWithTax = BigDecimal.ZERO;
  private List<RatedTransaction> ratedTransactions=new ArrayList<RatedTransaction>();
  
  public String getDescription()
  {
    return this.description;
  }
  
  public void setDescription(String description)
  {
    this.description = description;
  }
  
  public String getCode()
  {
    return this.code;
  }
  
  public void setCode(String code)
  {
    this.code = code;
  }
  
  public BigDecimal getAmountWithoutTax()
  {
    return this.amountWithoutTax.setScale(2, RoundingMode.HALF_UP);
  }
  
  public void setAmountWithoutTax(BigDecimal amountWithoutTax)
  {
    this.amountWithoutTax = amountWithoutTax;
  }
  
  public BigDecimal getAmountWithTax()
  {
    return this.amountWithTax;
  }
  
  public void setAmountWithTax(BigDecimal amountWithTax)
  {
    this.amountWithTax = amountWithTax;
  }
  
  public void addAmountWithTax(BigDecimal amountToAdd)
  {
    if (amountToAdd != null)
    {
      if (this.amountWithTax == null) {
        this.amountWithTax = new BigDecimal("0");
      }
      this.amountWithTax = this.amountWithTax.add(amountToAdd);
    }
  }
  
  public void addAmountWithoutTax(BigDecimal amountToAdd)
  {
    if (this.amountWithoutTax == null) {
      this.amountWithoutTax = new BigDecimal("0");
    }
    this.amountWithoutTax = this.amountWithoutTax.add(amountToAdd);
  }

public List<RatedTransaction> getRatedTransactions() {
	return ratedTransactions;
}

public void setRatedTransactions(List<RatedTransaction> ratedTransactions) {
	this.ratedTransactions = ratedTransactions;
}
  
  
}
