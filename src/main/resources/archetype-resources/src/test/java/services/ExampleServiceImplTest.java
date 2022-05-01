#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.services;

import ${package}.domain.Example;
import ${package}.repositories.ExampleRepository;
import ${package}.validation.ResourceNotFoundException;
import ${package}.web.mappers.ExampleMapper;
import ${package}.web.model.ExampleDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * Created by rslowik on $currentDate.
 */
@ExtendWith(MockitoExtension.class)
@SpringBootTest
class ExampleServiceImplTest {

    private Example example;

    @Autowired
    private ExampleService exampleService;

    @Autowired
    private ExampleMapper mapper;

    @MockBean
    private ExampleRepository exampleRepository;

    @Captor
    private ArgumentCaptor<Example> exampleArgumentCaptor;

    @Captor
    private ArgumentCaptor<UUID> uuidArgumentCaptor;

    @BeforeEach
    void setUp() {
        example = Example.builder()
                .id(UUID.randomUUID())
                .createdDate(new Timestamp(1651160725212L))
                .modifiedDate(new Timestamp(1651160725212L))
                .version(0)
                .build();
    }

    @Test
    void getExampleByIdFound() {
        given(exampleRepository.findById(example.getId())).willReturn(Optional.of(example));
        ExampleDto returnedExampleDto = exampleService.getExampleById(example.getId());
        verify(exampleRepository, times(1)).findById(uuidArgumentCaptor.capture());
        assertEquals(example.getId(), uuidArgumentCaptor.getValue());
        assertEqualsExample(example, returnedExampleDto);
    }

    @Test
    void getExampleByIdNotFound() {
        given(exampleRepository.findById(example.getId())).willReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> {
            exampleService.getExampleById(example.getId());
        }, "ResourceNotFoundException error was expected");
        verify(exampleRepository, times(1)).findById(uuidArgumentCaptor.capture());
        assertEquals(example.getId(), uuidArgumentCaptor.getValue());
    }

    @Test
    void updateExampleNotFound() {
        ExampleDto dto = mapper.exampleToExampleDto(example);

        // repository returns empty
        given(exampleRepository.findById(example.getId())).willReturn(Optional.empty());

        // so the ResourceNotFoundException exceptions expected in the service
        assertThrows(
                ResourceNotFoundException.class, () -> exampleService.updateExample(example.getId(), dto),
                "ResourceNotFoundException error expected!"
        );
        verify(exampleRepository, times(1)).findById(uuidArgumentCaptor.capture());
        verify(exampleRepository, never()).save(example);

        assertEquals(example.getId(), uuidArgumentCaptor.getValue());
    }

    @Test
    void updateExampleSuccess() {
        ExampleDto dto = mapper.exampleToExampleDto(example);

        given(exampleRepository.findById(example.getId())).willReturn(Optional.of(example));

        given(exampleRepository.save(example)).willReturn(example);
        exampleService.updateExample(example.getId(), dto);
        verify(exampleRepository, times(1)).findById(uuidArgumentCaptor.capture());
        verify(exampleRepository, times(1)).save(exampleArgumentCaptor.capture());

        assertEquals(example.getId(), uuidArgumentCaptor.getValue());
        assertEquals(example, exampleArgumentCaptor.getValue());
        assertEqualsExample(example, dto);
    }

    @Test
    void addExample() {
        ExampleDto dto = mapper.exampleToExampleDto(example);
        dto.setId(null);
        dto.setVersion(null);
        dto.setModifiedDate(null);
        dto.setCreatedDate(null);

        given(exampleRepository.save(any())).willReturn(example);
        ExampleDto returned = exampleService.addExample(dto);

        verify(exampleRepository, times(1)).save(any());
        assertEqualsExample(example, returned);
    }

    @Test
    void deleteExampleNotFound() {
        given(exampleRepository.existsById(example.getId())).willReturn(false);
        assertThrows(ResourceNotFoundException.class, () ->
                exampleService.deleteExample(example.getId()), "ResourceNotFoundException error was expected!");
        verify(exampleRepository, times(1)).existsById(uuidArgumentCaptor.capture());
        verify(exampleRepository, never()).deleteById(example.getId());
        assertEquals(example.getId(), uuidArgumentCaptor.getValue());
    }

    @Test
    void deleteExample() {
        given(exampleRepository.existsById(example.getId())).willReturn(true);
        exampleService.deleteExample(example.getId());
        verify(exampleRepository, times(1)).existsById(uuidArgumentCaptor.capture());
        verify(exampleRepository, times(1)).deleteById(uuidArgumentCaptor.capture());

        List<UUID> capturedUuids = uuidArgumentCaptor.getAllValues();
        assertEquals(2, capturedUuids.size());
        capturedUuids.forEach(uuid -> assertEquals(example.getId(), uuid));
    }

    private void assertEqualsExample(Example example, ExampleDto exampleDto) {
        assertEquals(example.getId(), exampleDto.getId());
        assertEquals(example.getCreatedDate().toLocalDateTime(), exampleDto.getCreatedDate().toLocalDateTime());
        assertEquals(example.getModifiedDate().toLocalDateTime(), exampleDto.getModifiedDate().toLocalDateTime());
        assertEquals(example.getVersion(), exampleDto.getVersion());
    }
}