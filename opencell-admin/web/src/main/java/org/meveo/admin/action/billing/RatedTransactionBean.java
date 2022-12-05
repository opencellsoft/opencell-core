/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */
package org.meveo.admin.action.billing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.enterprise.inject.Produces;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.billing.WalletOperation;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.WalletOperationService;

/**
 * Standard backing bean for {@link RatedTransaction} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their
 * create, edit, view, delete operations). It works with Manaty custom JSF components.
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 7.0
 */
@Named
@ViewScoped
public class RatedTransactionBean extends BaseBean<RatedTransaction> {

    private static final long serialVersionUID = 1L;

    /**
     * Injected @{link RatedTransaction} service. Extends {@link PersistenceService}.
     */
    @Inject
    private RatedTransactionService ratedTransactionService;

    @Inject
    private WalletOperationService walletOperationService;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public RatedTransactionBean() {
        super(RatedTransaction.class);
    }

    /**
     * Factory method for entity to edit. If objectId param set load that entity from database, otherwise create new.
     * 
     * @return rated transaction
     */
    @Produces
    @Named("ratedTransaction")
    public RatedTransaction init() {
        return initEntity();
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<RatedTransaction> getPersistenceService() {
        return ratedTransactionService;
    }

    public String getWalletOperationCode(Long walletOperationId) {
        if (walletOperationId == null) {
            return null;
        }
        try {
            WalletOperation walletOperation = walletOperationService.findById(walletOperationId);
            return walletOperation != null ? walletOperation.getCode() : null;
        } catch (Exception e) {
            log.error("failed to get wallet operation", e);
            return null;
        }
    }

    public List<String> getWalletOperationCodes(Long ratedTransactionId) {
        if (ratedTransactionId == null) {
            return null;
        }

        List<String> result = new ArrayList<>();
        List<WalletOperation> wos = walletOperationService.listByRatedTransactionId(ratedTransactionId);
        if (wos != null && !wos.isEmpty()) {
            wos.stream().forEach(e -> result.add(e.getCode()));
        }

        return result;
    }

    /**
     * #1661 [GUI](#349) ratedTransactionDetail ; make all fields on readOnly if status == BILLED;
     * 
     * @author mhammam
     * @return true if RatedTransaction STATUS equal BILLED
     */
    public boolean isBilled() {
        if (entity != null && entity.getStatus() == RatedTransactionStatusEnum.BILLED) {
            return true;
        }
        return false;
    }
}