#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.web.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Null;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Created by rslowik on $currentDate.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExampleDto {

    @Null
    private UUID id;
    @Null
    private Integer version;
    @Null
    private OffsetDateTime createdDate;
    @Null
    private OffsetDateTime modifiedDate;
}
