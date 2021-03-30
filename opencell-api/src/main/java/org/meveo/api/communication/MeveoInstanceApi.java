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

package org.meveo.api.communication;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.communication.MeveoInstanceDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.model.admin.User;
import org.meveo.model.communication.MeveoInstance;
import org.meveo.model.crm.Customer;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.communication.impl.MeveoInstanceService;
import org.meveo.service.crm.impl.CustomerService;

/**
 * 
 * @author Tyshan　Shi(tyshan@manaty.net)
 * @since Jun 3, 2016 7:11:03 AM
 *
 */
@Stateless
public class MeveoInstanceApi extends BaseApi{

	@Inject
	private MeveoInstanceService meveoInstanceService;
	
	@Inject
	private CustomerService customerService;
	
	@Inject
	private UserService userService;
	
	public void create(MeveoInstanceDto postData) throws MeveoApiException, BusinessException {
		log.debug("meveo instance api create by code {}",postData.getCode());
		if (StringUtils.isBlank(postData.getCode())) {
			addGenericCodeIfAssociated(MeveoInstance.class.getName(), postData);
        }
		if (StringUtils.isBlank(postData.getUrl())) {
            missingParameters.add("url");
        }
    	handleMissingParametersAndValidate(postData);
		  
		MeveoInstance meveoInstance=meveoInstanceService.findByCode(postData.getCode());
      	if(meveoInstance!=null){
      		throw new EntityAlreadyExistsException(MeveoInstance.class, postData.getCode());
      	}
      	
        meveoInstance=new MeveoInstance();
        meveoInstance.setCode(postData.getCode());
  		meveoInstance.setDescription(postData.getDescription());
  		meveoInstance.setProductName(postData.getProductName());
  		meveoInstance.setProductVersion(postData.getProductVersion());
  		meveoInstance.setOwner(postData.getOwner());
  		meveoInstance.setMd5(postData.getMd5());
  		meveoInstance.setStatus(postData.getStatus());
  		meveoInstance.setCreationDate(postData.getCreationDate());
  		meveoInstance.setUpdateDate(postData.getUpdateDate());
  		meveoInstance.setKeyEntreprise(postData.getKeyEntreprise());
  		meveoInstance.setMacAddress(postData.getMacAddress());
  		meveoInstance.setMachineVendor(postData.getMachineVendor());
  		meveoInstance.setInstallationMode(postData.getInstallationMode());
  		meveoInstance.setNbCores(postData.getNbCores());
  		meveoInstance.setMemory(postData.getMemory());
  		meveoInstance.setHdSize(postData.getHdSize());
  		meveoInstance.setOsName(postData.getOsName());
  		meveoInstance.setOsVersion(postData.getOsVersion());
  		meveoInstance.setOsArch(postData.getOsArch());
  		meveoInstance.setJavaVmName(postData.getJavaVmName());
  		meveoInstance.setJavaVmVersion(postData.getJavaVmVersion());
  		meveoInstance.setAsVendor(postData.getAsVendor());
  		meveoInstance.setAsVersion(postData.getAsVersion());
  		meveoInstance.setUrl(postData.getUrl());
  		meveoInstance.setAuthUsername(postData.getAuthUsername());
  		meveoInstance.setAuthPassword(postData.getAuthPassword());
  		if(!StringUtils.isBlank(postData.getCustomer())){
  			Customer customer=customerService.findByCode(postData.getCustomer());
  			if(customer==null){
  				throw new EntityDoesNotExistsException(Customer.class, postData.getCustomer());
  			}
  			meveoInstance.setCustomer(customer);
  		}
  		if(!StringUtils.isBlank(postData.getUser())){
  			User user=userService.findByUsername(postData.getUser());
  			if(user==null){
  				throw new EntityDoesNotExistsException(User.class, postData.getUser());
  			}
  			meveoInstance.setUser(user);
  		}
        meveoInstanceService.create(meveoInstance);
    }

    public void update(MeveoInstanceDto postData) throws MeveoApiException, BusinessException {
    	if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
    	handleMissingParametersAndValidate(postData);
		  
		MeveoInstance meveoInstance=meveoInstanceService.findByCode(postData.getCode());
      	if(meveoInstance==null){
      		throw new EntityDoesNotExistsException(MeveoInstance.class, postData.getCode());
      	}
      	meveoInstance.setCode(StringUtils.isBlank(postData.getUpdatedCode()) ? postData.getCode() : postData.getUpdatedCode());
  		meveoInstance.setDescription(postData.getDescription());
  		meveoInstance.setProductName(postData.getProductName());
  		meveoInstance.setProductVersion(postData.getProductVersion());
  		meveoInstance.setOwner(postData.getOwner());
  		meveoInstance.setMd5(postData.getMd5());
  		meveoInstance.setStatus(postData.getStatus());
  		meveoInstance.setCreationDate(postData.getCreationDate());
  		meveoInstance.setUpdateDate(postData.getUpdateDate());
  		meveoInstance.setKeyEntreprise(postData.getKeyEntreprise());
  		meveoInstance.setMacAddress(postData.getMacAddress());
  		meveoInstance.setMachineVendor(postData.getMachineVendor());
  		meveoInstance.setInstallationMode(postData.getInstallationMode());
  		meveoInstance.setNbCores(postData.getNbCores());
  		meveoInstance.setMemory(postData.getMemory());
  		meveoInstance.setHdSize(postData.getHdSize());
  		meveoInstance.setOsName(postData.getOsName());
  		meveoInstance.setOsVersion(postData.getOsVersion());
  		meveoInstance.setOsArch(postData.getOsArch());
  		meveoInstance.setJavaVmName(postData.getJavaVmName());
  		meveoInstance.setJavaVmVersion(postData.getJavaVmVersion());
  		meveoInstance.setAsVendor(postData.getAsVendor());
  		meveoInstance.setAsVersion(postData.getAsVersion());
  		if(!StringUtils.isBlank(postData.getUrl())){
  			meveoInstance.setUrl(postData.getUrl());
  		}
  		meveoInstance.setAuthUsername(postData.getAuthUsername());
  		meveoInstance.setAuthPassword(postData.getAuthPassword());
  		if(!StringUtils.isBlank(postData.getCustomer())){
  			Customer customer=customerService.findByCode(postData.getCustomer());
  			if(customer==null){
  				throw new EntityDoesNotExistsException(Customer.class, postData.getCustomer());
  			}
  			meveoInstance.setCustomer(customer);
  		}
  		if(!StringUtils.isBlank(postData.getUser())){
  			User user=userService.findByUsername(postData.getUser());
  			if(user==null){
  				throw new EntityDoesNotExistsException(User.class, postData.getUser());
  			}
  			meveoInstance.setUser(user);
  		}
        meveoInstanceService.update(meveoInstance);
    }

    public MeveoInstanceDto find(String meveoInstanceCode) throws MeveoApiException {
        if (StringUtils.isEmpty(meveoInstanceCode)) {
            missingParameters.add("meveoInstanceCode");
        }
        handleMissingParameters();
        
        MeveoInstance meveoInstance=meveoInstanceService.findByCode(meveoInstanceCode);

        if (meveoInstance == null) {
            throw new EntityDoesNotExistsException(MeveoInstance.class, meveoInstanceCode);
        }

        return new MeveoInstanceDto(meveoInstance);
    }

    public void remove(String meveoInstanceCode) throws MeveoApiException, BusinessException {
    	if (StringUtils.isBlank(meveoInstanceCode)) {
            missingParameters.add("meveoInstanceCode");
        }
        handleMissingParameters();
        MeveoInstance meveoInstance=meveoInstanceService.findByCode(meveoInstanceCode);

        if (meveoInstance == null) {
            throw new EntityDoesNotExistsException(MeveoInstance.class, meveoInstanceCode);
        }

        meveoInstanceService.remove(meveoInstance);
    }

    public List<MeveoInstanceDto> list() throws MeveoApiException {

        List<MeveoInstanceDto> result = new ArrayList<MeveoInstanceDto>();
        List<MeveoInstance> meveoInstances = meveoInstanceService.list();
        if (meveoInstances != null) {
            for (MeveoInstance meveoInstance : meveoInstances) {
                result.add(new MeveoInstanceDto(meveoInstance));
            }
        }

        return result;
    }

    public void createOrUpdate(MeveoInstanceDto meveoInstanceDto) throws MeveoApiException, BusinessException {
        if (!StringUtils.isBlank(meveoInstanceDto.getCode())
				&& meveoInstanceService.findByCode(meveoInstanceDto.getCode()) != null) {
			update(meveoInstanceDto);
		} else {
			create(meveoInstanceDto);
        }
    }
}

