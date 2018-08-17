package org.meveo.api.dto.document.sign;

import java.util.List;

import org.meveo.api.dto.BaseEntityDto;

/** 
 * A Dto holding inputs to create a document signature procedure from OC side.
 * 
 * @author Said Ramli 
 */ 
public class CreateProcedureRequestDto  extends BaseEntityDto {

    /** The Constant serialVersionUID. */ 
    private static final long serialVersionUID = 1L; 
    
    /** if true an internal member will be added to the procedure with internal member. */
    private boolean withInternalMember;
    
    /** List of files to be signed. it will be used to created the procedure */
    private List<SignFileRequestDto> filesToSign;
    
    /** The Signature procedure. */
    private SignProcedureDto procedure;
    
    /**
     * @return the procedure
     */
    public SignProcedureDto getProcedure() {
        return procedure;
    }

    /**
     * @param procedure the procedure to set
     */
    public void setProcedure(SignProcedureDto procedure) {
        this.procedure = procedure;
    }

    /**
     * @return the filesToSign
     */
    public List<SignFileRequestDto> getFilesToSign() {
        return filesToSign;
    }

    /**
     * @param filesToSign the filesToSign to set
     */
    public void setFilesToSign(List<SignFileRequestDto> filesToSign) {
        this.filesToSign = filesToSign;
    }

    /**
     * @return the withInternalMember
     */
    public boolean isWithInternalMember() {
        return withInternalMember;
    }

    /**
     * @param withInternalMember the withInternalMember to set
     */
    public void setWithInternalMember(boolean withInternalMember) {
        this.withInternalMember = withInternalMember;
    }
}
