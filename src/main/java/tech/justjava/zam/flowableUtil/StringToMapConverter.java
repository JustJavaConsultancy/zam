package tech.justjava.zam.flowableUtil;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.impl.el.FixedValue;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class StringToMapConverter implements JavaDelegate {
    private final ObjectMapper objectMapper;
    private FixedValue variableToConvertToMap;

    public StringToMapConverter(ObjectMapper objectMapper) {

        this.objectMapper = objectMapper;
    }

    @Override
    public void execute(DelegateExecution execution) {

        System.out.println(" The Execution Variables===="+execution.getVariables());

        System.out.println(" The variableToConvertToMap.getExpressionText()===="+variableToConvertToMap.getExpressionText());

        //Map<String, Object> payload = new HashMap<>();
        String variableName= (String) execution.getVariable(variableToConvertToMap.getExpressionText());

        //variableName = variableName.replace("```json","").replace("```","");

        System.out.println(" The String to be converted here======"+variableName);

        try {
            Map<String,Object> map = objectMapper.readValue(variableName,Map.class);
            execution.setVariable(variableToConvertToMap.getExpressionText(), map);
            //System.out.println(" The JSON going to thymeleaf generation==="+json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }
}
