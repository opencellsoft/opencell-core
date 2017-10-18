package org.tmf.dsmapi.catalog.resource.order;

import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.meveo.admin.exception.BusinessException;
import org.tmf.dsmapi.catalog.resource.product.ProductOffering;
import org.tmf.dsmapi.quote.ProductQuoteItem;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@XmlRootElement(name="OrderItem", namespace="http://www.tmforum.org")
@XmlType(name = "OrderItem", namespace = "http://www.tmforum.org")
@JsonInclude(value = Include.NON_NULL)
public class ProductOrderItem implements Serializable {

    private static final long serialVersionUID = 2224931518265750159L;
    private String id;
    private String action;
    private String state;
    private String appointment;
    private List<BillingAccount> billingAccount;
    private ProductOffering productOffering;
    private Product product;

    private static Unmarshaller umar ;
    private static Marshaller mar;
    
	static {
		try {
			
			umar = JAXBContext.newInstance(ProductOrderItem.class).createUnmarshaller();
			mar = JAXBContext.newInstance(ProductOrderItem.class).createMarshaller();
			mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAppointment() {
        return appointment;
    }

    public void setAppointment(String appointment) {
        this.appointment = appointment;
    }

    public List<BillingAccount> getBillingAccount() {
        return billingAccount;
    }

    public void setBillingAccount(List<BillingAccount> billingAccount) {
        this.billingAccount = billingAccount;
    }

    public ProductOffering getProductOffering() {
        return productOffering;
    }

    public void setProductOffering(ProductOffering productOffering) {
        this.productOffering = productOffering;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    /**
     * Serialize orderItem DTO into a string
     * 
     * @param productOrderItem Order item to serialize
     * @return String in XML format
     * 
     * @throws BusinessException
     */
    public static String serializeOrderItem(ProductOrderItem productOrderItem) throws BusinessException {
        try {
            //Marshaller m = JAXBContext.newInstance(ProductOrderItem.class).createMarshaller();
            //m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            StringWriter w = new StringWriter();
            mar.marshal(productOrderItem, w);

            return w.toString();

        } catch (JAXBException e) {
            throw new BusinessException(e);
        }
    }

    /**
     * Deserialize order item from a string
     * 
     * @param orderItemSource Serialized orderItem dto
     * @return Order item object
     * 
     * @throws BusinessException
     */
    public static ProductOrderItem deserializeOrderItem(String orderItemSource) throws BusinessException {
        // Store orderItem DTO into DB to be retrieved for full information
        try {
            //Unmarshaller m = JAXBContext.newInstance(ProductOrderItem.class).createUnmarshaller();
            // m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            ProductOrderItem productOrderItem = (ProductOrderItem) umar.unmarshal(new StringReader(orderItemSource));

            return productOrderItem;

        } catch (JAXBException e) {
            throw new BusinessException(e);
        }
    }
}