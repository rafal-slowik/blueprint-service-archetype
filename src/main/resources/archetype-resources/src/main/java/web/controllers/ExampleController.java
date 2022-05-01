#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.web.controllers;

import ${package}.services.ExampleService;
import ${package}.web.model.ExampleDto;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.UUID;

/**
 * Created by rslowik on $currentDate.
 */
@Validated
@RequestMapping("/api/v1/examples")
@RestController
public class ExampleController {

    private final ExampleService exampleService;

    public ExampleController(ExampleService exampleService) {
        this.exampleService = exampleService;
    }

    @GetMapping(path = "/{exampleId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ExampleDto> getExampleById(@PathVariable("exampleId") UUID exampleId) {
        return ResponseEntity.ok(
                exampleService.getExampleById(exampleId)
        );
    }

    @PutMapping(path = "/{exampleId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateExample(@PathVariable("exampleId") UUID exampleId, @Valid @NotNull @RequestBody ExampleDto exampleDto) {
        exampleService.updateExample(exampleId, exampleDto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createExample(@Valid @RequestBody ExampleDto exampleDto) {
        ExampleDto newExampleDto = exampleService.addExample(exampleDto);
        return ResponseEntity.created(
                URI.create("/api/v1/examples/" + newExampleDto.getId())
        ).build();
    }

    @DeleteMapping(path = "/{exampleId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteExample(@PathVariable("exampleId") UUID exampleId) {
        exampleService.deleteExample(exampleId);
        return ResponseEntity.noContent().build();
    }
}
