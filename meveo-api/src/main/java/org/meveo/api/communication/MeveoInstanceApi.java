package org.meveo.api.communication;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.communication.MeveoInstanceDto;
import org.meveo.api.dto.communication.MeveoInstancesDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.model.admin.User;
import org.meveo.model.communication.MeveoInstance;
import org.meveo.model.crm.Provider;
import org.meveo.model.mediation.Access;
import org.meveo.service.communication.impl.MeveoInstanceService;

/**
 * 
 * @author Tyshan　Shi(tyshan@manaty.net)
 * @date Jun 3, 2016 7:11:03 AM
 *
 */
@Stateless
public class MeveoInstanceApi extends BaseApi{

	@Inject
	private MeveoInstanceService meveoInstanceService;
	public void create(MeveoInstanceDto meveoInstanceDto, User currentUser) throws MeveoApiException, BusinessException {
		log.debug("meveo instance api create by code {}",meveoInstanceDto.getCode());
        if (StringUtils.isNotEmpty(meveoInstanceDto.getCode())) {
        	MeveoInstance existedMeveoInstance=meveoInstanceService.findByCode(meveoInstanceDto.getCode(),currentUser.getProvider());
        	if(existedMeveoInstance!=null){
        		throw new EntityAlreadyExistsException(MeveoInstance.class, meveoInstanceDto.getCode());
        	}
            MeveoInstance meveoInstance=new MeveoInstance();
            meveoInstance.setCode(meveoInstanceDto.getCode());
    		meveoInstance.setDescription(meveoInstanceDto.getDescription());
    		meveoInstance.setProductName(meveoInstanceDto.getProductName());
    		meveoInstance.setProductVersion(meveoInstanceDto.getProductVersion());
    		meveoInstance.setOwner(meveoInstanceDto.getOwner());
    		meveoInstance.setMd5(meveoInstanceDto.getMd5());
    		meveoInstance.setStatus(meveoInstanceDto.getStatus());
    		meveoInstance.setCreationDate(meveoInstanceDto.getCreationDate());
    		meveoInstance.setUpdateDate(meveoInstanceDto.getUpdateDate());
    		meveoInstance.setKeyEntreprise(meveoInstanceDto.getKeyEntreprise());
    		meveoInstance.setMacAddress(meveoInstanceDto.getMacAddress());
    		meveoInstance.setMachineVendor(meveoInstanceDto.getMachineVendor());
    		meveoInstance.setInstallationMode(meveoInstanceDto.getInstallationMode());
    		meveoInstance.setNbCores(meveoInstanceDto.getNbCores());
    		meveoInstance.setMemory(meveoInstanceDto.getMemory());
    		meveoInstance.setHdSize(meveoInstanceDto.getHdSize());
    		meveoInstance.setOsName(meveoInstanceDto.getOsName());
    		meveoInstance.setOsVersion(meveoInstanceDto.getOsVersion());
    		meveoInstance.setOsArch(meveoInstanceDto.getOsArch());
    		meveoInstance.setJavaVmName(meveoInstanceDto.getJavaVmName());
    		meveoInstance.setJavaVmVersion(meveoInstanceDto.getJavaVmVersion());
    		meveoInstance.setAsVendor(meveoInstanceDto.getAsVendor());
    		meveoInstance.setAsVersion(meveoInstanceDto.getAsVersion());
    		meveoInstance.setUrl(meveoInstanceDto.getUrl());
    		meveoInstance.setAuthUsername(meveoInstanceDto.getAuthUsername());
    		meveoInstance.setAuthPassword(meveoInstanceDto.getAuthPassword());
           
            meveoInstanceService.create(meveoInstance, currentUser);
            meveoInstanceService.commit();
        } else {
            if (StringUtils.isBlank(meveoInstanceDto.getCode())) {
                missingParameters.add("code");
            }
            handleMissingParameters();
        }
    }

    public void update(MeveoInstanceDto meveoInstanceDto, User currentUser) throws MeveoApiException, BusinessException {
        if (meveoInstanceDto.getCode() != null) {
            Provider provider = currentUser.getProvider();


            MeveoInstance meveoInstance = meveoInstanceService.findByCode(meveoInstanceDto.getCode(), provider);
            if (meveoInstance == null) {
                throw new EntityDoesNotExistsException(Access.class, meveoInstanceDto.getCode());
            }

            meveoInstance.setDescription(meveoInstanceDto.getDescription());
    		meveoInstance.setProductName(meveoInstanceDto.getProductName());
    		meveoInstance.setProductVersion(meveoInstanceDto.getProductVersion());
    		meveoInstance.setOwner(meveoInstanceDto.getOwner());
    		meveoInstance.setMd5(meveoInstanceDto.getMd5());
    		meveoInstance.setStatus(meveoInstanceDto.getStatus());
    		meveoInstance.setCreationDate(meveoInstanceDto.getCreationDate());
    		meveoInstance.setUpdateDate(meveoInstanceDto.getUpdateDate());
    		meveoInstance.setKeyEntreprise(meveoInstanceDto.getKeyEntreprise());
    		meveoInstance.setMacAddress(meveoInstanceDto.getMacAddress());
    		meveoInstance.setMachineVendor(meveoInstanceDto.getMachineVendor());
    		meveoInstance.setInstallationMode(meveoInstanceDto.getInstallationMode());
    		meveoInstance.setNbCores(meveoInstanceDto.getNbCores());
    		meveoInstance.setMemory(meveoInstanceDto.getMemory());
    		meveoInstance.setHdSize(meveoInstanceDto.getHdSize());
    		meveoInstance.setOsName(meveoInstanceDto.getOsName());
    		meveoInstance.setOsVersion(meveoInstanceDto.getOsVersion());
    		meveoInstance.setOsArch(meveoInstanceDto.getOsArch());
    		meveoInstance.setJavaVmName(meveoInstanceDto.getJavaVmName());
    		meveoInstance.setJavaVmVersion(meveoInstanceDto.getJavaVmVersion());
    		meveoInstance.setAsVendor(meveoInstanceDto.getAsVendor());
    		meveoInstance.setAsVersion(meveoInstanceDto.getAsVersion());
    		meveoInstance.setUrl(meveoInstanceDto.getUrl());
    		meveoInstance.setAuthUsername(meveoInstanceDto.getAuthUsername());
    		meveoInstance.setAuthPassword(meveoInstanceDto.getAuthPassword());

            meveoInstanceService.update(meveoInstance, currentUser);

        } else {
            if (StringUtils.isEmpty(meveoInstanceDto.getCode())) {
                missingParameters.add("code");
            }

            handleMissingParameters();
        }
    }

    public MeveoInstanceDto find(String code,Provider provider) throws MeveoApiException {
        if (StringUtils.isEmpty(code)) {
            missingParameters.add("code");
        }
        handleMissingParameters();
        
        MeveoInstance meveoInstance=meveoInstanceService.findByCode(code,provider);

        if (meveoInstance == null) {
            throw new EntityDoesNotExistsException(MeveoInstance.class, code);
        }

        return new MeveoInstanceDto(meveoInstance);
    }

    public void remove(String code, Provider provider) throws MeveoApiException {
        if (StringUtils.isNotEmpty(code)) {
        	MeveoInstance meveoInstance=meveoInstanceService.findByCode(code,provider);

            if (meveoInstance == null) {
                throw new EntityDoesNotExistsException(MeveoInstance.class, code);
            }

            meveoInstanceService.remove(meveoInstance);
        } else {
            if (StringUtils.isEmpty(code)) {
                missingParameters.add("code");
            }
            handleMissingParameters();
        }
    }

    public MeveoInstancesDto list(Provider provider) throws MeveoApiException {

        MeveoInstancesDto result = new MeveoInstancesDto();
        List<MeveoInstance> meveoInstances = meveoInstanceService.list(provider);
        if (meveoInstances != null) {
            for (MeveoInstance meveoInstance : meveoInstances) {
                result.getMeveoInstances().add(new MeveoInstanceDto(meveoInstance));
            }
        }

        return result;
    }

    public void createOrUpdate(MeveoInstanceDto meveoInstanceDto, User currentUser) throws MeveoApiException, BusinessException {
    	MeveoInstance meveoInstance=meveoInstanceService.findByCode(meveoInstanceDto.getCode(),currentUser.getProvider());

        if (meveoInstance == null) {
            create(meveoInstanceDto, currentUser);
        } else {
            update(meveoInstanceDto, currentUser);
        }
    }
}

