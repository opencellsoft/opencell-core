package org.meveo.service.custom;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.persistence.EntityManager;

import org.hibernate.SQLQuery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.api.dto.custom.CustomTableRecordDto;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.jpa.EntityManagerWrapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CustomTableServiceTest {
    
    @Spy
    @InjectMocks
    private CustomTableService sut;
    
    @Mock
    EntityManagerWrapper emWrapper;
    
    @Mock
    private QueryBuilder queryBuilder;
    
    @Mock
    private SQLQuery sqlQuery;
    
    @Before
    public void init() {
        doReturn(queryBuilder).when(sut).getQuery(anyString(), eq(null));
        doReturn(sqlQuery).when(queryBuilder).getNativeQuery(any(EntityManager.class), anyBoolean());
        when(emWrapper.getEntityManager()).thenReturn(mock(EntityManager.class));
    }
    
    @Test
    public void should_convert_table_data_to_records_holding_values_as_map_and_having_id() {
        //Given
        List<Map<String, Object>> givenData = buildListMap(3);
        when(sqlQuery.list()).thenReturn(givenData);
        //When
        List<CustomTableRecordDto> convertedData = sut.selectAllRecordsOfATableAsRecord("flirtikit",null);
        //Then
        assertThat(convertedData).isNotNull();
        assertThat(convertedData).hasSize(3);
        CustomTableRecordDto customTableRecordDto = convertedData.get(0);
        assertThat(customTableRecordDto.getId()).isNotNull();
    }

    @Test
    public void should_remove_empty_keys() {
        //Given
        Map<String, Object> queryValues = new HashMap<String, Object>(){{
            put("", 23);
            put("flirtikit", true);
            put("bidlidez", LocalDate.now());
        }};
        //When
        sut.removeEmptyKeys(queryValues);
        //Then
        assertThat(queryValues.size()).isEqualTo(2);
        assertThat(queryValues).containsKeys("flirtikit", "bidlidez");
        assertThat(queryValues).doesNotContainKeys("");

    }

    private List<Map<String, Object>> buildListMap(int size) {
        return IntStream.range(0, size)
                .mapToObj(index -> buildMApData(index))
                .collect(Collectors.toList());
    }
    
    private Map<String, Object> buildMApData(Integer index) {
        return new HashMap<String, Object>() {
            {
                put("id", index.longValue());
                put("code", String.format("code (%d)", index));
                put("name", String.format("name (%d)", index));
                put("prename", String.format("prename (%d)", index));
                put("isCool", false);
            }
        };
    }
   
    
  
}