package org.meveo.service.base.expression;

import org.junit.Before;
import org.junit.Test;
import org.meveo.commons.utils.InnerJoin;
import org.meveo.commons.utils.JoinWrapper;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.Invoice;

import java.util.List;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

public class QueryBuilderTest {

    private QueryBuilder queryBuilder;

    @Before
    public void setUp() throws Exception {
        queryBuilder = new QueryBuilder(Invoice.class, "I", List.of());
    }

    @Test
    public void join_has_name_and_alias() {
        InnerJoin innerJoin = new InnerJoin("ab");
        assertThat(innerJoin.getName()).isEqualTo("ab");
        assertThat(innerJoin.getAlias()).startsWith("ab_");
    }

    @Test
    public void join_may_point_to_list_of_joins() {
        InnerJoin innerJoin = new InnerJoin("ab");
        InnerJoin acInnerJoin = new InnerJoin("ac");
        innerJoin.next(acInnerJoin);
        InnerJoin aeInnerJoin = new InnerJoin("ae");
        innerJoin.next(aeInnerJoin);

        assertThat(innerJoin.getNextInnerJoins()).containsExactly(acInnerJoin, aeInnerJoin);
    }

    @Test
    public void can_format_one_join() {
        InnerJoin innerJoin = new InnerJoin("ab");
        String joinString = queryBuilder.format("", innerJoin);

        assertThat(joinString).isEqualTo(format("inner join ab %s ", innerJoin.getAlias()));
    }

    @Test
    public void query_builder_can_format_joins() {

        InnerJoin abInnerJoin = new InnerJoin("ab");
        InnerJoin bcInnerJoin = new InnerJoin("bc");
        abInnerJoin.next(bcInnerJoin);

        String joinString = queryBuilder.format("", abInnerJoin);

       assertThat(joinString).isEqualTo(format("inner join ab %s inner join %s.bc %s", abInnerJoin.getAlias(), abInnerJoin.getAlias(), bcInnerJoin.getAlias()));
    }

    @Test
    public void joins_may_have_n_deep() {
        InnerJoin abInnerJoin = new InnerJoin("ab");
        InnerJoin acInnerJoin = new InnerJoin("ac");
        InnerJoin adInnerJoin = new InnerJoin("ad");
        abInnerJoin.next(acInnerJoin);
        acInnerJoin.next(adInnerJoin);

        String joinString = queryBuilder.format("", abInnerJoin);

        assertThat(joinString).isEqualTo(format("inner join ab %s inner join %s.ac %s inner join %s.ad %s", abInnerJoin.getAlias(), abInnerJoin.getAlias(), acInnerJoin.getAlias(), acInnerJoin.getAlias(), adInnerJoin.getAlias()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void builder_could_not_parse_one_field() {
        JoinWrapper joinWrapper = queryBuilder.parse("a");
        InnerJoin rootJoin = joinWrapper.getRootInnerJoin();

        assertThat(rootJoin.getName()).isEqualTo("a");
        assertThat(rootJoin.getNextInnerJoins()).isEmpty();

        assertThat(joinWrapper.getJoinAlias()).isEqualTo(rootJoin.getAlias());

    }

    @Test
    public void builder_can_parse_two_fields() {
        JoinWrapper joinWrapper = queryBuilder.parse("a.b");
        InnerJoin rootJoin = joinWrapper.getRootInnerJoin();

        assertThat(rootJoin.getName()).isEqualTo("a");
        assertThat(rootJoin.getNextInnerJoins()).isEmpty();

        assertThat(joinWrapper.getJoinAlias()).isEqualTo(rootJoin.getAlias() + ".b");

    }

    @Test
    public void builder_can_parse_fields() {
        JoinWrapper joinWrapper = queryBuilder.parse("a.b.c");
        InnerJoin rootJoin = joinWrapper.getRootInnerJoin();

        assertThat(rootJoin.getName()).isEqualTo("a");
        assertThat(rootJoin.getNextInnerJoins()).hasSize(1);
        assertThat(rootJoin.getNextInnerJoins().get(0).getName()).isEqualTo("b");
        assertThat(rootJoin.getNextInnerJoins().get(0).getNextInnerJoins()).isEmpty();


        assertThat(joinWrapper.getJoinAlias()).isEqualTo(rootJoin.getNextInnerJoins().get(0).getAlias() + ".c");

    }
}
