#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.web.model;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by rslowik on $currentDate.
 */
public class ExamplePagedList extends PageImpl<ExampleDto> {

    public ExamplePagedList(List<ExampleDto> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    public ExamplePagedList(List<ExampleDto> content) {
        super(content);
    }
}
