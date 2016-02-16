package org.dataglow.v1.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.chrono.ChronoZonedDateTime;

public class DateUtil {

    public static long getMillis(LocalDateTime time) {
        if (time == null) {
            return 0;
        }
        Instant instant = ((ChronoZonedDateTime) time.atZone(ZoneId.systemDefault())).toInstant();
        return instant.toEpochMilli();
    }

}
