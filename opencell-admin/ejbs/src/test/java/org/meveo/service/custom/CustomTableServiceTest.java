package org.meveo.service.custom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CustomTableServiceTest {

	@Spy
	@InjectMocks
	private CustomTableService sut;

	@Mock
	private CustomFieldTemplateService cftService;

	@Mock
	EntityManagerWrapper emWrapper;

	@Mock
	private QueryBuilder queryBuilder;

	@Mock
	private SQLQuery sqlQuery;

	@Before
	public void init() {
		Map<String, CustomFieldTemplate> cfts = new HashMap<String, CustomFieldTemplate>() {
			{
				CustomFieldTemplate cft = mock(CustomFieldTemplate.class);
				when(cft.getGUIFieldPosition()).thenReturn(0);
				when(cft.getDbFieldname()).thenReturn("test");
				put("id", cft);
			}
		};
		when(cftService.findCFTsByDbTbleName(any())).thenReturn(cfts);
	}

	@Test
	public void should_convert_table_data_to_records_holding_values_as_map_and_having_id() {
		// Given
		List<Map<String, Object>> givenData = buildListMap(3);
		// When
		String tableName = "flirtikit";
		String wildCode = "";
		doReturn(givenData).when(sut).extractMapListByFields(eq(tableName), eq(wildCode), any());
		List<CustomTableRecordDto> convertedData = sut.selectAllRecordsOfATableAsRecord(tableName, wildCode);
		// Then
		assertThat(convertedData).isNotNull();
		assertThat(convertedData).hasSize(3);
		CustomTableRecordDto customTableRecordDto = convertedData.get(0);
		assertThat(customTableRecordDto.getId()).isNotNull();
	}

	@Test
	public void should_remove_empty_keys() {
		// Given
		Map<String, Object> queryValues = new HashMap<String, Object>() {
			{
				put("", 23);
				put("flirtikit", true);
				put("bidlidez", LocalDate.now());
			}
		};
		// When
		sut.removeEmptyKeys(queryValues);
		// Then
		assertThat(queryValues.size()).isEqualTo(2);
		assertThat(queryValues).containsKeys("flirtikit", "bidlidez");
		assertThat(queryValues).doesNotContainKeys("");

	}

	private List<Map<String, Object>> buildListMap(int size) {
		return IntStream.range(0, size).mapToObj(index -> buildMApData(index)).collect(Collectors.toList());
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