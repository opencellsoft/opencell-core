package org.meveo.api.dto.cpq;


import java.util.Objects;
import javax.validation.constraints.NotNull;
import org.meveo.model.cpq.ProductVersionAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
/**
 * @author Tarik FAKHOURI
 *
 */
public class ProductVersionAttributeDTO {
    @Schema(description = "Id of product version", required = true)
    @NotNull
    private Long productVersionId;
    @Schema(description = "Code of attribute", required = true)
    @NotNull
    private AttributeDTO attributeDto;
    @Schema(description = "attribute order in the GUI")
    private Integer sequence = 0;

    @Schema(description = "Indicate if the attribute has a mandatory EL")
    private String mandatoryWithEl;
    
    
    public ProductVersionAttributeDTO() {
        super();
    }
    public ProductVersionAttributeDTO(ProductVersionAttribute pva) {
        this.productVersionId = pva.getProductVersion().getId();
        this.attributeDto = new AttributeDTO(pva.getAttribute());
        this.sequence = pva.getSequence();
        this.mandatoryWithEl = pva.getMandatoryWithEl();
    }
    /**
     * @return the productVersionId
     */
    public Long getProductVersionId() {
        return productVersionId;
    }
    /**
     * @param productVersionId the productVersionId to set
     */
    public void setProductVersionId(Long productVersionId) {
        this.productVersionId = productVersionId;
    }
    /**
     * @return the sequence
     */
    public Integer getSequence() {
        return sequence;
    }
    /**
     * @param sequence the sequence to set
     */
    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }
    @Override
    public int hashCode() {
        return Objects.hash(attributeDto, productVersionId, sequence);
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ProductVersionAttributeDTO other = (ProductVersionAttributeDTO) obj;
        return Objects.equals(attributeDto, other.attributeDto)
                && Objects.equals(productVersionId, other.productVersionId) && Objects.equals(sequence, other.sequence);
    }
    /**
     * @return the attributeDto
     */
    public AttributeDTO getAttributeDto() {
        return attributeDto;
    }
    /**
     * @param attributeDto the attributeDto to set
     */
    public void setAttributeDto(AttributeDTO attributeDto) {
        this.attributeDto = attributeDto;
    }
	/**
	 * @return the mandatoryWithEl
	 */
	public String getMandatoryWithEl() {
		return mandatoryWithEl;
	}
	/**
	 * @param mandatoryWithEl the mandatoryWithEl to set
	 */
	public void setMandatoryWithEl(String mandatoryWithEl) {
		this.mandatoryWithEl = mandatoryWithEl;
	}
}