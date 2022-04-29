#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.web.mappers;

import ${package}.domain.Example;
import ${package}.web.model.ExampleDto;
import org.mapstruct.Mapper;

/**
 * Created by rslowik on 27/04/2022.
 */
@Mapper(uses = DateMapper.class)
public interface ExampleMapper {

    Example exampleDtoToExample(ExampleDto exampleDto);

    ExampleDto exampleToExampleDto(Example example);
}
