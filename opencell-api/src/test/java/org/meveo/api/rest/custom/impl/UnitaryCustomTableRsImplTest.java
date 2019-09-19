package org.meveo.api.rest.custom.impl;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import javax.ws.rs.core.Link;
import org.junit.Test;

public class UnitaryCustomTableRsImplTest {
    private UnitaryCustomTableRsImpl sut = new UnitaryCustomTableRsImpl();

    @Test
    public void should_return_all_methods_as_links() {
        List<Link> links = sut.asHeatoeas("TABLE_1", 15L);

        assertThat(links.size()).isEqualTo(5);
        assertThat(links.toString()).isEqualTo("[</unitaryCustomTable/TABLE_1/15>; rel=\"DELETE\"; type=\"application/json\", </unitaryCustomTable/TABLE_1/15/enable>; rel=\"POST\"; type=\"application/json\", </unitaryCustomTable/TABLE_1/15/disable>; rel=\"POST\"; type=\"application/json\", </unitaryCustomTable/>; rel=\"POST\"; title=\"create\"; type=\"application/json\", </unitaryCustomTable/>; rel=\"POST\"; title=\"update\"; type=\"application/json\"]");
    }
}
