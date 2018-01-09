package org.meveo.api.rest.communication.impl;

import javax.inject.Inject;

import org.meveo.api.communication.MeveoInstanceApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.communication.MeveoInstanceDto;
import org.meveo.api.dto.response.communication.MeveoInstanceResponseDto;
import org.meveo.api.dto.response.communication.MeveoInstancesResponseDto;
import org.meveo.api.rest.communication.MeveoInstanceRs;
import org.meveo.api.rest.impl.BaseRs;

/**
 * 
 * @author Tyshan Shi(tyshan@manaty.net)
 * @since Jun 4, 2016 4:08:58 AM
 *
 */
public class MeveoInstanceRsImpl extends BaseRs implements MeveoInstanceRs {

    @Inject
    private MeveoInstanceApi meveoInstanceApi;

    @Override
    public ActionStatus create(MeveoInstanceDto meveoInstanceDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            meveoInstanceApi.create(meveoInstanceDto);
        } catch (Exception e) {
            processException(e, result);
        }
        return result;
    }

    @Override
    public ActionStatus update(MeveoInstanceDto meveoInstanceDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            meveoInstanceApi.update(meveoInstanceDto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public MeveoInstanceResponseDto find(String code) {
        MeveoInstanceResponseDto result = new MeveoInstanceResponseDto();
        try {
            result.setMeveoInstance(meveoInstanceApi.find(code));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus remove(String code) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            meveoInstanceApi.remove(code);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public MeveoInstancesResponseDto list() {
        MeveoInstancesResponseDto result = new MeveoInstancesResponseDto();

        try {
            result.setMeveoInstances(meveoInstanceApi.list());
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdate(MeveoInstanceDto meveoInstanceDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            meveoInstanceApi.createOrUpdate(meveoInstanceDto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }
}
