package org.meveo.model.billing;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * Aggregation Matrix.
 *
 * @author Khalid HORRI
 * @lastModifiedVersion 9.0
 */
@Entity
@Cacheable
@Table(name = "wo_aggregation_matrix")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "wo_aggregation_matrix_seq"), })
public class WalletOperationAggregationMatrix extends BusinessEntity {

    @OneToMany(mappedBy = "aggregationMatrix", fetch = FetchType.EAGER)
    private List<WalletOperationAggregationLine> aggregationLines = new ArrayList<>();

    public List<WalletOperationAggregationLine> getAggregationLines() {
        return aggregationLines;
    }

    public void setAggregationLines(List<WalletOperationAggregationLine> aggregationLines) {
        this.aggregationLines = aggregationLines;
    }
}
