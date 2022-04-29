#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import ${package}.config.JsonMapperConfiguration;
import ${package}.services.ExampleService;
import ${package}.web.mappers.ExampleMapper;
import ${package}.web.model.ExampleDto;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.StringUtils;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.matchesRegex;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by rslowik on 22/04/2022.
 */

@ExtendWith({RestDocumentationExtension.class, MockitoExtension.class})
@AutoConfigureRestDocs
@Import(JsonMapperConfiguration.class)
@WebMvcTest(controllers = {ExampleController.class})
@ComponentScan("${package}.web.mappers")
class ExampleControllerTest {

    private ExampleDto exampleDto;

    @MockBean
    private ExampleService exampleService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ExampleMapper mapper;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        exampleDto = ExampleDto.builder().build();
    }

    @SneakyThrows
    @Test
    void getExampleById() {
        exampleDto.setId(UUID.randomUUID());
        exampleDto.setCreatedDate(OffsetDateTime.now());
        exampleDto.setModifiedDate(OffsetDateTime.now());
        given(exampleService.getExampleById(exampleDto.getId())).willReturn(exampleDto);

        mockMvc.perform(
                        get("/api/v1/examples/{exampleId}", exampleDto.getId())
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(
                        document(
                                "v1/examples/get/",
                                pathParameters(
                                        parameterWithName("exampleId")
                                                .description("UUID of desired example to get.")
                                ), responseFields(
                                        fieldWithPath("id").description("Id of Example"),
                                        fieldWithPath("createdDate").description("Date Created"),
                                        fieldWithPath("modifiedDate").description("Date Modified")
                                )
                        )
                ).andDo(print());
    }

    @SneakyThrows
    @Test
    void updateExample() {
        ConstrainedFields fields = new ConstrainedFields(ExampleDto.class);
        mockMvc.perform(
                        put("/api/v1/examples/{exampleId}", UUID.randomUUID().toString())
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(exampleDto))
                ).andExpect(status().isNoContent())
                .andDo(
                        document(
                                "v1/examples/put/",
                                pathParameters(
                                        parameterWithName("exampleId")
                                                .description("UUID of example to update.")
                                ), requestFields()
                        )
                )
                .andDo(print());
    }

    @SneakyThrows
    @Test
    void createExample() {
        ExampleDto dtoToReturn = ExampleDto.builder().build();
        dtoToReturn.setCreatedDate(OffsetDateTime.now());
        dtoToReturn.setModifiedDate(OffsetDateTime.now());
        dtoToReturn.setVersion(0);
        dtoToReturn.setId(UUID.randomUUID());

        given(exampleService.addExample(exampleDto)).willReturn(dtoToReturn);

        ConstrainedFields fields = new ConstrainedFields(ExampleDto.class);
        mockMvc.perform(
                        post("/api/v1/examples")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(exampleDto))
                ).andExpect(status().isCreated())
                .andExpect(header().string("Location", matchesRegex("[/a-z1]*/[-0-9a-f]{36}${symbol_dollar}")))
                .andDo(
                        document(
                                "v1/examples/post/",
                                requestFields()
                        )
                )
                .andDo(print());
    }

    @SneakyThrows
    @Test
    void createExample400() {
        exampleDto.setId(UUID.randomUUID());
        mockMvc.perform(
                        post("/api/v1/examples")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(exampleDto))
                ).andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
                .andExpect(jsonPath("${symbol_dollar}.type", is("https://zalando.github.io/problem/constraint-violation")))
                .andExpect(jsonPath("${symbol_dollar}.violations[0].field", is("id")))
                .andExpect(jsonPath("${symbol_dollar}.violations[0].message", is("must be null")))
                .andDo(print());
    }

    private static class ConstrainedFields {

        private final ConstraintDescriptions constraintDescriptions;

        ConstrainedFields(Class<?> input) {
            this.constraintDescriptions = new ConstraintDescriptions(input);
        }

        private FieldDescriptor withPath(String path) {
            return fieldWithPath(path).attributes(key("constraints").value(StringUtils
                    .collectionToDelimitedString(this.constraintDescriptions
                            .descriptionsForProperty(path), ". ")));
        }
    }
}