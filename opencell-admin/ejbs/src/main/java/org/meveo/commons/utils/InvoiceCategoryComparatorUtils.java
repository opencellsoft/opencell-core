package org.meveo.commons.utils;

import org.meveo.model.billing.CategoryInvoiceAgregate;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.SubCategoryInvoiceAgregate;

import java.util.Comparator;

public class InvoiceCategoryComparatorUtils {
    /**
     * @return A comparator for sorting InvoiceCategory by sortIndex and Description.
     */
    public static Comparator<CategoryInvoiceAgregate> getInvoiceCategoryComparator() {
        return new Comparator<CategoryInvoiceAgregate>() {
            public int compare(CategoryInvoiceAgregate c0, CategoryInvoiceAgregate c1) {
                if (c0.getInvoiceCategory() == null || c1.getInvoiceCategory() == null) {
                    return c0.getInvoiceCategory() == null ? c1.getInvoiceCategory() == null ? 0 : 1 : -1;
                }

                if (c0.getInvoiceCategory().getSortIndex() == null || c1.getInvoiceCategory().getSortIndex() == null) {
                    if (c0.getInvoiceCategory().getSortIndex() == null && c1.getInvoiceCategory().getSortIndex() == null) {
                        if(c0.getInvoiceCategory().getDescription() == null || c1.getInvoiceCategory().getDescription() == null) {
                            return c0.getInvoiceCategory().getDescription() == null ? c1.getInvoiceCategory().getDescription() == null ? 0 : 1: -1;
                        }
                        return c0.getInvoiceCategory().getDescription().compareToIgnoreCase(c1.getInvoiceCategory().getDescription());
                    } else {
                        return c0.getInvoiceCategory().getSortIndex() == null ? c1.getInvoiceCategory().getSortIndex() == null ? 0 : 1 : -1;
                    }
                }

                if (c0.getInvoiceCategory().getSortIndex() != null && c1.getInvoiceCategory().getSortIndex() != null) {
                    return c0.getInvoiceCategory().getSortIndex().compareTo(c1.getInvoiceCategory().getSortIndex());
                }
                return 0;
            }
        };
    }

    /**
     * @return A comparator for sorting InvoiceCategory by sortIndex and Description.
     */
    public static Comparator<SubCategoryInvoiceAgregate> getInvoiceSubCategoryComparator() {
        return new Comparator<SubCategoryInvoiceAgregate>() {
            public int compare(SubCategoryInvoiceAgregate c0, SubCategoryInvoiceAgregate c1) {
                if (c0.getInvoiceSubCategory() == null || c1.getInvoiceSubCategory() == null) {
                    return c0.getInvoiceSubCategory() == null ? c1.getInvoiceSubCategory() == null ? 0 : 1 : -1;
                }

                if (c0.getInvoiceSubCategory().getSortIndex() == null || c1.getInvoiceSubCategory().getSortIndex() == null) {
                    if (c0.getInvoiceSubCategory().getSortIndex() == null && c1.getInvoiceSubCategory().getSortIndex() == null) {
                        if(c0.getInvoiceSubCategory().getDescription() == null || c1.getInvoiceSubCategory().getDescription() == null) {
                            return c0.getInvoiceSubCategory().getDescription() == null ? c1.getInvoiceSubCategory().getDescription() == null ? 0 : 1: -1;
                        }
                        return c0.getInvoiceSubCategory().getDescription().compareToIgnoreCase(c1.getInvoiceSubCategory().getDescription());
                    } else {
                        return c0.getInvoiceSubCategory().getSortIndex() == null ? c1.getInvoiceSubCategory().getSortIndex() == null ? 0 : 1 : -1;
                    }
                }

                if (c0.getInvoiceSubCategory().getSortIndex() != null && c1.getInvoiceSubCategory().getSortIndex() != null) {
                    return c0.getInvoiceSubCategory().getSortIndex().compareTo(c1.getInvoiceSubCategory().getSortIndex());
                }
                return 0;
            }
        };
    }

    /**
     * @return A comparator for sorting RatedTransaction by sortIndex and Description.
     */
    public static Comparator<RatedTransaction> getRatedTransactionComparator() {
        return new Comparator<RatedTransaction>() {
            public int compare(RatedTransaction c0, RatedTransaction c1) {

                if (c0.getSortIndex() == null || c1.getSortIndex() == null) {
                    if (c0.getSortIndex() == null && c1.getSortIndex() == null) {
                        if(c0.getDescription() == null || c1.getDescription() == null) {
                            return c0.getDescription() == null ? c1.getDescription() == null ? 0 : 1 : -1;
                        }
                        return c0.getDescription().compareToIgnoreCase(c1.getDescription());
                    } else {
                        return c0.getSortIndex() == null ? c1.getSortIndex() == null ? 0 : 1 : -1;
                    }
                }

                if (c0.getSortIndex() != null && c1.getSortIndex() != null) {
                    return c0.getSortIndex().compareTo(c1.getSortIndex());
                }
                return 0;
            }
        };
    }
}
