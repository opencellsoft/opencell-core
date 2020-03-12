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

package org.meveo.service.script.payment;

import java.util.List;
import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.sepa.DDRejectFileInfos;
import org.meveo.model.payments.AccountOperation;
import org.meveo.service.script.Script;

/**
 * The Class DDRequestBuilderScript.
 * 
 * @author anasseh
 * @author Said Ramli
 * @lastModifiedVersion 5.3
 */
public abstract class DDRequestBuilderScript extends Script implements DDRequestBuilderScriptInterface {

    private static final long serialVersionUID = 504213309864822236L;

    /** The Constant DD_REQUEST_LOT. */
    public static final String DD_REQUEST_LOT = "DD_REQUEST_LOT";

    /** The Constant DD_REQUEST_LIST_AO. */
    public static final String DD_REQUEST_LIST_AO = "DD_REQUEST_LIST_AO";

    /** The Constant PROVIDER. */
    public static final String PROVIDER = "PROVIDER";

    /** The Constant DD_REJECT_FILE. */
    public static final String DD_REJECT_FILE = "DD_REJECT_FILE";

    /** The Constant DD_REJECT_FILE_INFOS. */
    public static final String DD_REJECT_FILE_INFOS = "DD_REJECT_FILE_INFOS";

    @Override
    public void generateDDRequestLotFile(Map<String, Object> methodContext) throws BusinessException {
    }

    @Override
    public String getDDFileName(Map<String, Object> methodContext) throws BusinessException {
        return null;
    }

    @Override
    public DDRejectFileInfos processSDDRejectedFile(Map<String, Object> methodContext) throws BusinessException {
        return null;
    }

    @Override
    public DDRejectFileInfos processSCTRejectedFile(Map<String, Object> methodContext) throws BusinessException {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<AccountOperation> findListAoToPay(Map<String, Object> methodContext) throws BusinessException {
        return (List<AccountOperation>) methodContext.get(DD_REQUEST_LIST_AO);
    }
}