package org.meveo.apiv2.article.impl;

import org.meveo.apiv2.article.*;
import org.meveo.apiv2.ordering.*;

public class AccountingCodeMappingMapper
        extends ResourceMapper<AccountingCodeMapping, org.meveo.model.accountingScheme.AccountingCodeMapping> {

    @Override
    protected AccountingCodeMapping toResource(org.meveo.model.accountingScheme.AccountingCodeMapping entity) {
        return ImmutableAccountingCodeMapping
                        .builder()
                        .id(entity.getId())
                        // .accountingCode(entity.getAccountingCode()) TODO change to AccountingCode
                        .build();
    }

    @Override
    protected org.meveo.model.accountingScheme.AccountingCodeMapping toEntity(AccountingCodeMapping resource) {
        return null;
    }
}
