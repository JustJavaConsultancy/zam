package tech.justjava.zam.process.model;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;
import org.springframework.web.servlet.HandlerMapping;
import tech.justjava.zam.process.service.ProcessService;


/**
 * Validate that the processName value isn't taken yet.
 */
@Target({ FIELD, METHOD, ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = ProcessProcessNameUnique.ProcessProcessNameUniqueValidator.class
)
public @interface ProcessProcessNameUnique {

    String message() default "{Exists.process.processName}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class ProcessProcessNameUniqueValidator implements ConstraintValidator<ProcessProcessNameUnique, String> {

        private final ProcessService processService;
        private final HttpServletRequest request;

        public ProcessProcessNameUniqueValidator(final ProcessService processService,
                final HttpServletRequest request) {
            this.processService = processService;
            this.request = request;
        }

        @Override
        public boolean isValid(final String value, final ConstraintValidatorContext cvContext) {
            if (value == null) {
                // no value present
                return true;
            }
            @SuppressWarnings("unchecked") final Map<String, String> pathVariables =
                    ((Map<String, String>)request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE));
            final String currentId = pathVariables.get("id");
            if (currentId != null && value.equalsIgnoreCase(processService.get(Long.parseLong(currentId)).getProcessName())) {
                // value hasn't changed
                return true;
            }
            return !processService.processNameExists(value);
        }

    }

}
