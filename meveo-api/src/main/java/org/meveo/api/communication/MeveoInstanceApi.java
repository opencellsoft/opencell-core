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
import org.meveo.model.crm.Provider;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.communication.impl.MeveoInstanceService;
import org.meveo.service.crm.impl.CustomerService;

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
	
	@Inject
	private CustomerService customerService;
	
	@Inject
	private UserService userService;
	
	public void create(MeveoInstanceDto meveoInstanceDto, User currentUser) throws MeveoApiException, BusinessException {
		log.debug("meveo instance api create by code {}",meveoInstanceDto.getCode());
		if (StringUtils.isBlank(meveoInstanceDto.getCode())) {
            missingParameters.add("code");
        }
		if (StringUtils.isBlank(meveoInstanceDto.getUrl())) {
            missingParameters.add("url");
        }
    	this.handleMissingParameters();
		  
		MeveoInstance meveoInstance=meveoInstanceService.findByCode(meveoInstanceDto.getCode(),currentUser.getProvider());
      	if(meveoInstance!=null){
      		throw new EntityAlreadyExistsException(MeveoInstance.class, meveoInstanceDto.getCode());
      	}
      	
        meveoInstance=new MeveoInstance();
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
  		if(!StringUtils.isBlank(meveoInstanceDto.getCustomer())){
  			Customer customer=customerService.findByCode(meveoInstanceDto.getCustomer(), currentUser.getProvider());
  			if(customer==null){
  				throw new EntityDoesNotExistsException(Customer.class, meveoInstanceDto.getCustomer());
  			}
  			meveoInstance.setCustomer(customer);
  		}
  		if(!StringUtils.isBlank(meveoInstanceDto.getUser())){
  			User user=userService.findByUsername(meveoInstanceDto.getUser());
  			if(user==null){
  				throw new EntityDoesNotExistsException(User.class, meveoInstanceDto.getUser());
  			}
  			meveoInstance.setUser(user);
  		}
        meveoInstanceService.create(meveoInstance, currentUser);
    }

    public void update(MeveoInstanceDto meveoInstanceDto, User currentUser) throws MeveoApiException, BusinessException {
    	if (StringUtils.isBlank(meveoInstanceDto.getCode())) {
            missingParameters.add("code");
        }
    	this.handleMissingParameters();
		  
		MeveoInstance meveoInstance=meveoInstanceService.findByCode(meveoInstanceDto.getCode(),currentUser.getProvider());
      	if(meveoInstance==null){
      		throw new EntityDoesNotExistsException(MeveoInstance.class, meveoInstanceDto.getCode());
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
  		if(!StringUtils.isBlank(meveoInstanceDto.getUrl())){
  			meveoInstance.setUrl(meveoInstanceDto.getUrl());
  		}
  		meveoInstance.setAuthUsername(meveoInstanceDto.getAuthUsername());
  		meveoInstance.setAuthPassword(meveoInstanceDto.getAuthPassword());
  		if(!StringUtils.isBlank(meveoInstanceDto.getCustomer())){
  			Customer customer=customerService.findByCode(meveoInstanceDto.getCustomer(), currentUser.getProvider());
  			if(customer==null){
  				throw new EntityDoesNotExistsException(Customer.class, meveoInstanceDto.getCustomer());
  			}
  			meveoInstance.setCustomer(customer);
  		}
  		if(!StringUtils.isBlank(meveoInstanceDto.getUser())){
  			User user=userService.findByUsername(meveoInstanceDto.getUser());
  			if(user==null){
  				throw new EntityDoesNotExistsException(User.class, meveoInstanceDto.getUser());
  			}
  			meveoInstance.setUser(user);
  		}
        meveoInstanceService.update(meveoInstance, currentUser);
    }

    public MeveoInstanceDto find(String meveoInstanceCode,Provider provider) throws MeveoApiException {
        if (StringUtils.isEmpty(meveoInstanceCode)) {
            missingParameters.add("meveoInstanceCode");
        }
        handleMissingParameters();
        
        MeveoInstance meveoInstance=meveoInstanceService.findByCode(meveoInstanceCode,provider);

        if (meveoInstance == null) {
            throw new EntityDoesNotExistsException(MeveoInstance.class, meveoInstanceCode);
        }

        return new MeveoInstanceDto(meveoInstance);
    }

    public void remove(String meveoInstanceCode, User currentUser) throws MeveoApiException, BusinessException {
    	if (StringUtils.isBlank(meveoInstanceCode)) {
            missingParameters.add("meveoInstanceCode");
        }
        handleMissingParameters();
        MeveoInstance meveoInstance=meveoInstanceService.findByCode(meveoInstanceCode,currentUser.getProvider());

        if (meveoInstance == null) {
            throw new EntityDoesNotExistsException(MeveoInstance.class, meveoInstanceCode);
        }

        meveoInstanceService.remove(meveoInstance, currentUser);
    }

    public List<MeveoInstanceDto> list(Provider provider) throws MeveoApiException {

        List<MeveoInstanceDto> result = new ArrayList<MeveoInstanceDto>();
        List<MeveoInstance> meveoInstances = meveoInstanceService.list(provider);
        if (meveoInstances != null) {
            for (MeveoInstance meveoInstance : meveoInstances) {
                result.add(new MeveoInstanceDto(meveoInstance));
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

