package org.meveo.apiv2.report;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;
import org.meveo.model.admin.User;
import org.meveo.model.report.query.QueryVisibilityEnum;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableQueryScheduler.class)
public interface QueryScheduler extends Resource {

	@Schema(description = "Query scheduler file format")
    String getFileFormat();

    @Schema(description = "Users to notify")
    List<User> getUsersToNotify();
    
    @Schema(description = "Emails to notify")
    List<String> getEmailsToNotify();
    
    @Schema(description = "Query scheduler year")
    String getYear();
    
    @Schema(description = "Query scheduler month")
    String getMonth();
    
    @Schema(description = "Query scheduler every month")
    boolean getEveryMonth();
    
    @Schema(description = "Query scheduler day of month")
    String getDayOfMonth();
    
    @Schema(description = "Query scheduler every day of month")
    boolean getEveryDayOfMonth();
    
    @Schema(description = "Query scheduler day of week")
    String getDayOfWeek();
    
    @Schema(description = "Query scheduler every day of week")
    boolean getEveryDayOfWeek();
    
    @Schema(description = "Query scheduler hour")
    String getHour();
    
    @Schema(description = "Query scheduler every hour")
    boolean getEveryHour();
    
    @Schema(description = "Query scheduler minute")
    String getMinute();
    
    @Schema(description = "Query scheduler every minute")
    boolean getEveryMinute();
    
    @Schema(description = "Query scheduler second")
    String getSecond();
    
    @Schema(description = "Query scheduler every second")
    boolean getEverySecond();
}