#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.services;

import ${package}.domain.Example;
import ${package}.repositories.ExampleRepository;
import ${package}.validation.ResourceNotFoundException;
import ${package}.web.mappers.ExampleMapper;
import ${package}.web.model.ExampleDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Created by rslowik on $currentDate.
 */
@Slf4j
@Service
public class ExampleServiceImpl implements ExampleService {

    private final ExampleRepository exampleRepository;
    private final ExampleMapper mapper;

    public ExampleServiceImpl(ExampleRepository exampleRepository, ExampleMapper mapper) {
        this.exampleRepository = exampleRepository;
        this.mapper = mapper;
    }

    @Override
    public ExampleDto getExampleById(UUID exampleId) {
        return mapper.exampleToExampleDto(
                exampleRepository.findById(exampleId).orElseThrow(() ->
                        new ResourceNotFoundException("A requested resource not found!")
                )
        );
    }

    @Override
    public void updateExample(UUID exampleId, ExampleDto exampleDto) {
        Example example = exampleRepository.findById(exampleId).orElseThrow(() ->
                new ResourceNotFoundException("A requested resource not found!")
        );
        // add update fields
        exampleRepository.save(example);
    }

    @Override
    public ExampleDto addExample(ExampleDto exampleDto) {
        Example exampleCreated = exampleRepository.save(
                mapper.exampleDtoToExample(exampleDto)
        );
        log.debug("Created Example: {}", exampleCreated);
        return mapper.exampleToExampleDto(exampleCreated);
    }

    @Override
    public void deleteExample(UUID exampleId) {
        if (!exampleRepository.existsById(exampleId)) {
            throw new ResourceNotFoundException("A requested resource not found!");
        }
        exampleRepository.deleteById(exampleId);
    }
}
