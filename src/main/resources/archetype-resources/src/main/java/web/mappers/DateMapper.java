#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.web.mappers;

import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * Created by rslowik on 27/04/2022.
 */
@Component
public class DateMapper {
    public OffsetDateTime asOffsetDateTime(Timestamp ts) {
        if (ts != null) {
            return OffsetDateTime.of(
                    ts.toLocalDateTime().getYear(),
                    ts.toLocalDateTime().getMonthValue(),
                    ts.toLocalDateTime().getDayOfMonth(),
                    ts.toLocalDateTime().getHour(),
                    ts.toLocalDateTime().getMinute(),
                    ts.toLocalDateTime().getSecond(),
                    ts.toLocalDateTime().getNano(),
                    ZoneOffset.UTC
            );
        }
        return null;
    }

    public Timestamp asTimestamp(OffsetDateTime offsetDateTime) {
        if (offsetDateTime != null) {
            return Timestamp.valueOf(
                    offsetDateTime
                            .atZoneSameInstant(ZoneOffset.UTC)
                            .toLocalDateTime()
            );
        }
        return null;
    }
}
