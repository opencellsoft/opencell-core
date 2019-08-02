package org.meveo.service.base;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NativePersistenceServiceTest {

    private NativePersistenceService sut = new NativePersistenceService();

    @Test
    public void should_request_only_select_id_and_order_by_id_when_there_is_no_field_present() {
        //Given
        String tableName = "TABLE_1";
        StringBuffer findIdFields = new StringBuffer();
        //When
        StringBuffer request = sut.buildSqlInsertionRequest(tableName, findIdFields);
        //Then
        assertThat(request.toString()).isEqualTo("select id from TABLE_1 order by id desc");
    }
}