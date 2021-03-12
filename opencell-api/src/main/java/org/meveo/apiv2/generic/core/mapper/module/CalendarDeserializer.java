package org.meveo.apiv2.generic.core.mapper.module;

import java.io.IOException;
import java.util.Date;

import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.CalendarBanking;
import org.meveo.model.catalog.CalendarDaily;
import org.meveo.model.catalog.CalendarFixed;
import org.meveo.model.catalog.CalendarInterval;
import org.meveo.model.catalog.CalendarJoin;
import org.meveo.model.catalog.CalendarPeriod;
import org.meveo.model.catalog.CalendarYearly;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

class CalendarDeserializer extends JsonDeserializer<Calendar> {

	@Override
	public Calendar deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		ObjectCodec codec = jp.getCodec();
		JsonNode node = codec.readTree(jp);
		if (node.get("id") != null) {
			Long id = node.get("id").longValue();
			Calendar calendar = new Calendar() {
				@Override
				public Date previousPeriodEndDate(Date date) {
					return null;
				}

				@Override
				public Date previousCalendarDate(Date date) {
					return null;
				}

				@Override
				public Date nextPeriodStartDate(Date date) {
					return null;
				}

				@Override
				public Date nextCalendarDate(Date date) {
					return null;
				}
			};
			calendar.setId(id);
			return Calendar.class.cast(calendar);
		} else if (node.get("calendarType") != null) {
			JsonParser p = codec.treeAsTokens(node);
			switch (node.get("calendarType").asText()) {
			case "BANKING":
				return codec.readValue(p, CalendarBanking.class);
			case "DAILY":
				return codec.readValue(p, CalendarDaily.class);
			case "FIXED":
				return codec.readValue(p, CalendarFixed.class);
			case "INTERVAL":
				return codec.readValue(p, CalendarInterval.class);
			case "JOIN":
				return codec.readValue(p, CalendarJoin.class);
			case "PERIOD":
				return codec.readValue(p, CalendarPeriod.class);
			case "YEARLY":
				return codec.readValue(p, CalendarYearly.class);

			}
		}
		return null;
	}
}
