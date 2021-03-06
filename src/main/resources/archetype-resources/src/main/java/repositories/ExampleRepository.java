#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.repositories;

import ${package}.domain.Example;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Created by rslowik on $currentDate.
 */
@Repository
public interface ExampleRepository extends PagingAndSortingRepository<Example, UUID> {
}
