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
    
    /** The absolute paths. */
    private boolean absolutePaths;
    
    /** if true an internal member will be added to the procedure with internal member. */
    private boolean withInternalMember;
    
    /** List of files to be signed. it will be used to created the procedure */
    private List<SignFileRequestDto> filesToSign;
    
    /** The Signature procedure. */
    private SignProcedureDto procedure;
    
    /**
     * Gets the procedure.
     *
     * @return the procedure
     */
    public SignProcedureDto getProcedure() {
        return procedure;
    }

    /**
     * Sets the procedure.
     *
     * @param procedure the procedure to set
     */
    public void setProcedure(SignProcedureDto procedure) {
        this.procedure = procedure;
    }

    /**
     * Gets the files to sign.
     *
     * @return the filesToSign
     */
    public List<SignFileRequestDto> getFilesToSign() {
        return filesToSign;
    }

    /**
     * Sets the files to sign.
     *
     * @param filesToSign the filesToSign to set
     */
    public void setFilesToSign(List<SignFileRequestDto> filesToSign) {
        this.filesToSign = filesToSign;
    }

    /**
     * Checks if is with internal member.
     *
     * @return the withInternalMember
     */
    public boolean isWithInternalMember() {
        return withInternalMember;
    }

    /**
     * Sets the with internal member.
     *
     * @param withInternalMember the withInternalMember to set
     */
    public void setWithInternalMember(boolean withInternalMember) {
        this.withInternalMember = withInternalMember;
    }

    /**
     * @return the absolutePaths
     */
    public boolean isAbsolutePaths() {
        return absolutePaths;
    }

    /**
     * @param absolutePaths the absolutePaths to set
     */
    public void setAbsolutePaths(boolean absolutePaths) {
        this.absolutePaths = absolutePaths;
    }
}
