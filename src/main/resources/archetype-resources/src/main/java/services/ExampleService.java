#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.services;

import ${package}.web.model.ExampleDto;

import java.util.UUID;

/**
 * Created by rslowik on 27/04/2022.
 */
public interface ExampleService {

    ExampleDto getExampleById(UUID exampleId);

    void updateExample(UUID exampleId, ExampleDto exampleDto);

    ExampleDto addExample(ExampleDto exampleDto);

    void deleteExample(UUID exampleId);
}
