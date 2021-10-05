package org.meveo.apiv2.report;

import java.util.List;

import javax.annotation.Nullable;

import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Style;
import org.meveo.model.admin.User;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.v3.oas.annotations.media.Schema;

@Immutable
@Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableQuerySchedulerInput.class)
public interface QuerySchedulerInput {

    @Schema(description = "Query scheduler file format")
    String getFileFormat();

    @Nullable
    @Schema(description = "Users to notify")
    List<User> getUsersToNotify();
    
    @Nullable
    @Schema(description = "Emails to notify")
    List<String> getEmailsToNotify();
    
    @Nullable
    @Schema(description = "Query scheduler year")
    String getYear();
    
    @Nullable
    @Schema(description = "Query scheduler month")
    String getMonth();
    
    @Schema(description = "Query scheduler every month")
    boolean getEveryMonth();
    
    @Nullable
    @Schema(description = "Query scheduler day of month")
    String getDayOfMonth();
    
    @Schema(description = "Query scheduler every day of month")
    boolean getEveryDayOfMonth();
    
    @Nullable
    @Schema(description = "Query scheduler day of week")
    String getDayOfWeek();
    
    @Schema(description = "Query scheduler every day of week")
    boolean getEveryDayOfWeek();
    
    @Nullable
    @Schema(description = "Query scheduler hour")
    String getHour();
    
    @Schema(description = "Query scheduler every hour")
    boolean getEveryHour();
    
    @Nullable
    @Schema(description = "Query scheduler minute")
    String getMinute();
    
    @Schema(description = "Query scheduler every minute")
    boolean getEveryMinute();
    
    @Nullable
    @Schema(description = "Query scheduler second")
    String getSecond();
    
    @Schema(description = "Query scheduler every second")
    boolean getEverySecond();

    @Schema(description = "Query scheduler is activated")
    boolean getIsQueryScheduler();
}