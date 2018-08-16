package org.meveo.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.ejb.Stateless;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.meveo.api.dto.document.sign.CreateProcedureRequestDto;
import org.meveo.api.dto.document.sign.SignFileObjectRequestDto;
import org.meveo.api.dto.document.sign.SignFileRequestDto;
import org.meveo.api.dto.document.sign.SignFileResponseDto;
import org.meveo.api.dto.document.sign.SignMemberRequestDto;
import org.meveo.api.dto.document.sign.SignProcedureDto;
import org.meveo.api.dto.document.sign.SignProcedureResponseDto;
import org.meveo.api.exception.MeveoApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * API encapsulating interactions with Yousign platform.
 * @author Said Ramli
 */
@Stateless
public class YouSignApi extends BaseApi {
    
    /** The Constant LOG. */
    private static final Logger LOG = LoggerFactory.getLogger(YouSignApi.class);
    
    /** The Constant YOUSIGN_API_TOKEN_PROPERTY_KEY. */
    private static final String YOUSIGN_API_TOKEN_PROPERTY_KEY = "yousign.api.token";
    
    /** The Constant YOUSIGN_API_URL_PROPERTY_KEY. */
    private static final String YOUSIGN_API_URL_PROPERTY_KEY = "yousign.api.url";
    
    /** The Constant SIGN_OBJECT_POSITION_PATTERN. */
    private static final Pattern SIGN_OBJECT_POSITION_PATTERN = Pattern.compile("[0-9]+,[0-9]+,[0-9]+,[0-9]+");
    
    /** The Constant EXT_MEMBER_PHONE_PATTERN. */
    private static final Pattern EXT_MEMBER_PHONE_PATTERN = Pattern.compile("(\\+)([0-9]+)");
 

    /**
     * Creates the procedure.
     *
     * @param postData the post data
     * @return the sign procedure response dto
     * @throws MeveoApiException the meveo api exception
     */
    public SignProcedureResponseDto createProcedure(CreateProcedureRequestDto postData) throws MeveoApiException {
       
        SignProcedureResponseDto result = new SignProcedureResponseDto();
        
        final String YOU_SIGN_REST_URL = this.getMondatoryYousignParam(YOUSIGN_API_URL_PROPERTY_KEY);
        final String YOU_SIGN_AUTH_TOKEN = this.getMondatoryYousignParam(YOUSIGN_API_TOKEN_PROPERTY_KEY);
        
        try {
            boolean withInternalMember = postData.isWithInternalMember();
            
            // Checking procedure body :
            SignProcedureDto procedure = postData.getProcedure();
            if (procedure == null) {
                throw new MeveoApiException(" Error : procedure body is missing !");
            }
            
            // Checking members :
            this.checkMembers(procedure.getMembers(), withInternalMember);
            
            // The list of files to sign cannot be empty :  
            List<SignFileRequestDto> filesToSign = postData.getFilesToSign();
            this.checkFilesToSign(filesToSign, withInternalMember);
           
            
            // Uploading files to Yousign platform :
            this.uploadFilesToSign(filesToSign, YOU_SIGN_REST_URL, YOU_SIGN_AUTH_TOKEN);
            
            // Preparing members :
            this.prepareMembers(filesToSign, procedure.getMembers(), withInternalMember);
            
            // Creating procedureusing  Yousign platform API :
            ResteasyClient client = new ResteasyClientBuilder().build();
            ResteasyWebTarget target = client.target(YOU_SIGN_REST_URL.concat("/procedures"));
            Response response = target.request().header(HttpHeaders.AUTHORIZATION, "Bearer " + YOU_SIGN_AUTH_TOKEN).post(Entity.json(postData.getProcedure()));
            
            if (isSuccessResponse(response)) {
                // reading results :
                result = response.readEntity(SignProcedureResponseDto.class);
            } else {
                throw new MeveoApiException(" [Yousign Error] [" + response.getStatus() +"] : " + response.getStatusInfo().getReasonPhrase());
            }

        } catch (MeveoApiException mve) {
            LOG.error(" Error on createProcedure : {} ", mve.getMessage());
            throw mve;
        } catch (Exception e) {
            LOG.error(" Error on createProcedure : {} ", e.getMessage(), e);
            throw new MeveoApiException(" Error on createProcedure " + e.getMessage());
        }
        return result;
    }
    
    /**
     * Gets the file by id.
     *
     * @param id the id
     * @return the file by id
     * @throws MeveoApiException the meveo api exception
     */
    public SignFileResponseDto downloadFileById(String id) throws MeveoApiException {
        
        SignFileResponseDto result = new SignFileResponseDto();
        try {
            final String YOU_SIGN_REST_URL = this.getMondatoryYousignParam(YOUSIGN_API_URL_PROPERTY_KEY);
            final String YOU_SIGN_AUTH_TOKEN = this.getMondatoryYousignParam(YOUSIGN_API_TOKEN_PROPERTY_KEY);
            
            ResteasyClient client = new ResteasyClientBuilder().build();
            ResteasyWebTarget target = client.target(YOU_SIGN_REST_URL.concat("/files/".concat(id).concat("/download")));
            Response response = target.request().header(HttpHeaders.AUTHORIZATION, "Bearer " + YOU_SIGN_AUTH_TOKEN).get();
            
            if (isSuccessResponse(response)) {
                byte[] fileContent = response.readEntity(byte[].class); 
                result = new SignFileResponseDto(id, fileContent);
            } else {
                throw new MeveoApiException(" [Yousign Error] [" + response.getStatus() +"] : " + response.getStatusInfo().getReasonPhrase());
            }

        } catch (MeveoApiException mve) {
            LOG.error(" Error on downloadFileById : {} , id = {}", mve.getMessage(), id);
            throw mve;
        } catch (Exception e) {
            LOG.error(" Error on downloadFileById : {} , id = {} ", e.getMessage(), id);
            throw new MeveoApiException(" Error on downloadFileById, id =  {} " + id + " : " +  e.getMessage());
        }
        return result;
    }

    /**
     * Gets the procedure by id.
     *
     * @param id the id
     * @return the procedure by id
     * @throws MeveoApiException the meveo api exception
     */
    public SignProcedureResponseDto getProcedureById(String id) throws MeveoApiException {
        
        SignProcedureResponseDto result = new SignProcedureResponseDto();
        try {
            final String YOU_SIGN_REST_URL = this.getMondatoryYousignParam(YOUSIGN_API_URL_PROPERTY_KEY);
            final String YOU_SIGN_AUTH_TOKEN = this.getMondatoryYousignParam(YOUSIGN_API_TOKEN_PROPERTY_KEY);
            
            ResteasyClient client = new ResteasyClientBuilder().build();
            ResteasyWebTarget target = client.target( YOU_SIGN_REST_URL.concat("/procedures/".concat(id)) );
            Response response = target.request().header(HttpHeaders.AUTHORIZATION, "Bearer " + YOU_SIGN_AUTH_TOKEN).get();
            
            if (isSuccessResponse(response)) {
                result = response.readEntity(SignProcedureResponseDto.class); 
            } else {
                throw new MeveoApiException(" [Yousign Error] [" + response.getStatus() +"] : " + response.getStatusInfo().getReasonPhrase());
            }

        } catch (MeveoApiException mve) {
            LOG.error(" Error on getProcedureById : {} , id = {}", mve.getMessage(), id);
            throw mve;
        } catch (Exception e) {
            LOG.error(" Error on getProcedureById : {} , id = {} ", e.getMessage(), id);
            throw new MeveoApiException(" Error on getProcedureById, id =  {} " + id + " : " +  e.getMessage());
        }
        return result;
    }
    
    /**
     * Gets the procedure status by id.
     *
     * @param id the id
     * @return the procedure status by id
     * @throws MeveoApiException the meveo api exception
     */
    public String getProcedureStatusById(String id) throws MeveoApiException {
        SignProcedureResponseDto responseDto = this.getProcedureById(id);
        if (responseDto != null) {
            return responseDto.getStatus();
        } else {
            throw new MeveoApiException(" Error on getProcedureStatusById , id =  {} " + id + " : Procedure Not Found ! ");
        }
    }
    
    /**
     * Gets the mondatory yousign param.
     *
     * @param paramKey the param key
     * @return the mondatory yousign param
     * @throws MeveoApiException the meveo api exception
     */
    private String getMondatoryYousignParam (String paramKey) throws MeveoApiException {
        String paramValue = this.paramBeanFactory.getInstance().getProperty(paramKey, null);
        if (StringUtils.isEmpty(paramValue)) {
            throw new MeveoApiException(" Mondatory Yousign param not configured : " + paramKey); 
        }
        return paramValue;
    }
    
    /**
     * Check members.
     *
     * @param filesToSign the files to sign
     * @param members the members
     * @param withInternalMember the with internal member
     * @throws MeveoApiException the meveo api exception
     */
    private void checkMembers(List<SignMemberRequestDto> members, boolean withInternalMember) throws MeveoApiException {

        if (CollectionUtils.isEmpty(members)) { 
            throw new MeveoApiException(" members cannot be empty !"); 
        } 
        if (withInternalMember) {
            members.add(this.getInternalMember());
        }
        
        for (SignMemberRequestDto member : members) {
            if (!BooleanUtils.isTrue(member.getInternal())) {
                String phone = member.getPhone();
                if (StringUtils.isEmpty(phone)) { 
                    throw new MeveoApiException(" Phone of external member cannot be empty !"); 
                } else if (!EXT_MEMBER_PHONE_PATTERN.matcher(phone).matches()) {
                    throw new MeveoApiException(" Phone of external member format is not valid !");
                } 
            }
        }
    }

    /**
     * Check and prepare members.
     *
     * @param filesToSign the files to sign
     * @param members the members
     * @param withInternalMember the with internal member
     * @throws MeveoApiException the meveo api exception
     */
    private void prepareMembers(List<SignFileRequestDto> filesToSign, List<SignMemberRequestDto> members, boolean withInternalMember) throws MeveoApiException {
        
        for (SignMemberRequestDto member : members) {
           List<SignFileObjectRequestDto> fileObjects = new ArrayList<>();
           for (SignFileRequestDto fileToSign : filesToSign) {
               
               SignFileObjectRequestDto signFileObject = new SignFileObjectRequestDto(fileToSign.getId());
               
               if (BooleanUtils.isTrue(member.getInternal())) {
                   signFileObject.setPosition(fileToSign.getInternalPosition());
                   signFileObject.setPage(fileToSign.getInternalPage());
               } else {
                   signFileObject.setPosition(fileToSign.getExternalPosition());
                   signFileObject.setPage(fileToSign.getExternalPage());
               }
               fileObjects.add(signFileObject);
           }
           member.setFileObjects(fileObjects);
        }
    }

    /**
     * Gets the internal member.
     *
     * @return the internal member
     * @throws MeveoApiException the meveo api exception
     */
    private SignMemberRequestDto getInternalMember() throws MeveoApiException {
        String internalMemberId = this.getMondatoryYousignParam("yousign.api.user");
        return new SignMemberRequestDto(internalMemberId);
    }

    /**
     * Upload files to sign.
     * We assume that filesToSign is not empty.
     *
     * @param filesToSign the files to sign
     * @param url the url
     * @param token the token
     * @throws MeveoApiException the meveo api exception
     */
    @SuppressWarnings("unchecked")
    private void uploadFilesToSign(List<SignFileRequestDto> filesToSign, String url,String token) throws MeveoApiException {
        
        try {
            
            ResteasyClient client = new ResteasyClientBuilder().build();
            ResteasyWebTarget target = client.target(url.concat("/files"));
            Invocation.Builder resBuilder = target.request().header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            
            LOG.debug(" Start uploading files to Yousign ... ");
            for (SignFileRequestDto file :filesToSign ) {
                
                Response response =  resBuilder.post(Entity.json(file)); 
                
                if (isSuccessResponse(response)) {
                    String json =  response.readEntity(String.class);
                    
                    ObjectMapper mapper = new ObjectMapper();
                    Map<String, String> fielFields = mapper.readValue(json, HashMap.class);
                    
                    String signableFileId = fielFields.get("id");
                    if (signableFileId == null) {
                        throw new MeveoApiException(" Error creating file on Yousign " + file.getName());
                    }
                    file.setId(signableFileId);
                } else {
                    throw new MeveoApiException(" [Yousign Error] [" + response.getStatus() +"] : " + response.getStatusInfo().getReasonPhrase());
                }
            }
            LOG.debug(" End uploading files to Yousign ... ");
            
        } catch (MeveoApiException mve) {
            LOG.error(" Error on uploadFilesToSign : {} ", mve.getMessage());
            throw mve;
        } catch (Exception e) {
            LOG.error(" Error on uploadFilesToSign : {} ", e.getMessage(), e);
            throw new MeveoApiException(" Error on uploadFilesToSign " + e.getMessage());
        }
    }

    /**
     * Check files to sign.
     *
     * @param filesToSign the files to sign
     * @param withInternalMember the with internal member
     * @throws MeveoApiException the meveo api exception
     * @throws FileNotFoundException the file not found exception
     */
    private void checkFilesToSign(List<SignFileRequestDto> filesToSign, boolean withInternalMember) throws MeveoApiException, FileNotFoundException {

        if (CollectionUtils.isEmpty(filesToSign)) { 
            throw new MeveoApiException(" filesToSign cannot be empty !"); 
        } 
        for (SignFileRequestDto file : filesToSign) {
            
            // internal position of the signature object is mandatory just if withInternalMember = true
            if (withInternalMember) {
                String internalPosition = file.getInternalPosition();
                if (StringUtils.isEmpty(internalPosition)) {
                    this.missingParameters.add("fileToSign -> internalPosition");
                } else if (!SIGN_OBJECT_POSITION_PATTERN.matcher(internalPosition).matches()) {
                    this.missingParameters.add("fileToSign -> internalPosition");
                }
            }
            
            String externalPosition = file.getExternalPosition();
            if (StringUtils.isEmpty(externalPosition)) {
                this.missingParameters.add("fileToSign -> externalPosition");
            } else if (!SIGN_OBJECT_POSITION_PATTERN.matcher(externalPosition).matches()) {
                this.missingParameters.add("fileToSign -> externalPosition");
            }
            
            this.handleMissingParameters();
            
            byte [] fileContent =  file.getContent();
            String filePath = file.getFilePath();
            // Each file should have either fileContent or filePath !
            if (ArrayUtils.isEmpty(fileContent) && StringUtils.isEmpty(filePath)) {
                throw new MeveoApiException(" .Each file should have either fileContent or filePath !"); 
            }
            // Creating byte file content if empty :
            if (ArrayUtils.isEmpty(fileContent)) {
                file.setContent(getFileAsBytes(filePath)); 
            }
        }
    } 
    
    /**
     * Checks if is success response.
     *
     * @param response the response
     * @return true, if is success response
     */
    private boolean isSuccessResponse(Response response) {
        return Response.Status.Family.SUCCESSFUL == response.getStatusInfo().getFamily();
    } 
    
    /**
     * TODO : Externalize this method to a generic  helper
     * 
     * Gets the file as bytes.
     *
     * @param filePath the file path
     * @return the file as bytes
     * @throws FileNotFoundException the file not found exception
     */
    private byte[] getFileAsBytes(String filePath) throws FileNotFoundException { 
         
        File pdfFile = new File(filePath); 
        if (!pdfFile.exists()) { 
            throw new FileNotFoundException(" File not found ! -> filePath :  " + filePath); 
        } 
        try (FileInputStream fileInputStream = new FileInputStream(pdfFile)) { 
            long fileSize = pdfFile.length(); 
            if (fileSize > Integer.MAX_VALUE) { 
                throw new IllegalArgumentException("File is too big to put it to buffer in memory."); 
            } 
            byte[] fileBytes = new byte[(int) fileSize]; 
            fileInputStream.read(fileBytes); 
            return fileBytes; 
        } catch (Exception e) { 
            LOG.error("Error reading file {} contents", filePath, e); 
        }  
        return null; 
    }

}
