package org.meveo.commons.utils;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.MeveoJobCategoryEnum;

public class EnumDeserializeTest {

    @Test
    public void should_enum_deserialised_by_name() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(MeveoJobCategoryEnum.MEDIATION);
        System.out.println(json);
        JobCategoryEnum categoryEnum = mapper.readValue("\"MEDIATION\"", JobCategoryEnum.class);
        Assert.assertEquals(MeveoJobCategoryEnum.MEDIATION, categoryEnum);
    }
}