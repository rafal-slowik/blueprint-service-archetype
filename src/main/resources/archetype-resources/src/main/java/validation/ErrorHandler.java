#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.spring.web.advice.ProblemHandling;

/**
 * Created by rslowik on $currentDate.
 */
@Slf4j
@ControllerAdvice
public class ErrorHandler implements ProblemHandling {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Problem> notFound(ResourceNotFoundException resourceNotFoundException,
                                                              NativeWebRequest request) {
        return create(Status.NOT_FOUND, resourceNotFoundException, request);
    }
}
