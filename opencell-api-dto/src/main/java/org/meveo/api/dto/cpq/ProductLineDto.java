package org.meveo.api.dto.cpq;

import java.util.Objects;

/**
 * 
 * @author Khairi
 * @version 10.0
 */
public class ProductLineDto {
	
	private Long id;
	private String codeProductLine;
	private String label;
	private String codeSeller;
	private String longDescription;
	private Long idCodeParentLine;
	
	
	public ProductLineDto(String codeProductLine, String label, String codeSeller, String longDescription,
			Long idCodeParentLine) {
		this.codeProductLine = codeProductLine;
		this.label = label;
		this.codeSeller = codeSeller;
		this.longDescription = longDescription;
		this.idCodeParentLine = idCodeParentLine;
	}
	public ProductLineDto() {
	}
	/**
	 * @return the codeProductLine
	 */
	public String getCodeProductLine() {
		return codeProductLine;
	}
	/**
	 * @param codeProductLine the codeProductLine to set
	 */
	public void setCodeProductLine(String codeProductLine) {
		this.codeProductLine = codeProductLine;
	}
	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}
	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	/**
	 * @return the codeSeller
	 */
	public String getCodeSeller() {
		return codeSeller;
	}
	/**
	 * @param codeSeller the codeSeller to set
	 */
	public void setCodeSeller(String codeSeller) {
		this.codeSeller = codeSeller;
	}
	/**
	 * @return the longDescription
	 */
	public String getLongDescription() {
		return longDescription;
	}
	/**
	 * @param longDescription the longDescription to set
	 */
	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}
	/**
	 * @return the idCodeParentLine
	 */
	public Long getIdCodeParentLine() {
		return idCodeParentLine;
	}
	/**
	 * @param idCodeParentLine the idCodeParentLine to set
	 */
	public void setIdCodeParentLine(Long idCodeParentLine) {
		this.idCodeParentLine = idCodeParentLine;
	}
	@Override
	public int hashCode() {
		return Objects.hash(codeProductLine, codeSeller, idCodeParentLine, label, longDescription);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProductLineDto other = (ProductLineDto) obj;
		return Objects.equals(codeProductLine, other.codeProductLine) && Objects.equals(codeSeller, other.codeSeller)
				&& Objects.equals(idCodeParentLine, other.idCodeParentLine) && Objects.equals(label, other.label)
				&& Objects.equals(longDescription, other.longDescription);
	}
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	
	
}
