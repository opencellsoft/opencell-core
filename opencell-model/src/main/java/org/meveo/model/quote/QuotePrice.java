package org.meveo.model.quote;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.cpq.enums.OneShotTypeEnum;
import org.meveo.model.cpq.enums.PriceTypeEnum;

@SuppressWarnings("serial")
@Entity
@Table(name = "cpq_quote_tarif", uniqueConstraints = @UniqueConstraint(columnNames = { "code"}))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_quote_tarif_seq"), })
public class QuotePrice extends BusinessEntity  {


    /**
     * quote item
     */
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "quote_item_id", nullable = false, referencedColumnName = "id")
	@NotNull
    private QuoteItem quoteItem;
    
    @Column(name = "charge_code", nullable = false, length = 20)
    @Size(max = 20)
    private String chargeCode;
    
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "price_type", nullable = false)
    @NotNull
    private PriceTypeEnum priceType;

    @Column(name = "recurence_duration")
    private int recurenceDuration;

    @Column(name = "recurence_periodicity")
    private int recurencePeriodicity;

    @Column(name = "overcharge")
    private boolean overCharge;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "oneshot_type")
    private OneShotTypeEnum oneShotType;

    @Column(name = "param1", length = 50)
    @Size(max = 50)
    private String param1;

    @Column(name = "param2", length = 50)
    @Size(max = 50)
    private String param2;

    @Column(name = "param3", length = 50)
    @Size(max = 50)
    private String param3;

    @Column(name = "param4", length = 50)
    @Size(max = 50)
    private String param4;

    @Column(name = "price_matrix", nullable = false)
    private Boolean priceMatrix;

    @Column(name = "dim1_matrix", length = 50)
    @Size(max = 50)
    private String dim1Matrix;

    @Column(name = "dim2_matrix", length = 50)
    @Size(max = 50)
    private String dim2Matrix;

    @Column(name = "dim3_matrix", length = 50)
    @Size(max = 50)
    private String dim3Matrix;

    @Column(name = "dim3_matrix", length = 20, nullable = false)
    @Size(max = 20)
    private String usageCode;
    

    @Column(name = "quantity", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal quantity = BigDecimal.ONE;
    

    @Column(name = "unite_price_without_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal unitePriceWithoutTax;

    @Column(name = "price_without_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal priceWithoutTax;

    @Column(name = "tax_code", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal taxCode;

    @Column(name = "tax_rate")
    private int taxRate;

    @Column(name = "price_with_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal priceWithTax;
}
