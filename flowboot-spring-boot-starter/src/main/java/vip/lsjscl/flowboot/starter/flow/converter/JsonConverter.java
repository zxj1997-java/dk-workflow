package vip.lsjscl.flowboot.starter.flow.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JSON 转换器
 *
 * @author zhangxingju
 * @date 2025/02/15
 */
@Converter
public class JsonConverter implements AttributeConverter<Object, String> {
    private static final Logger log = LoggerFactory.getLogger(JsonConverter.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Object attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            log.error("Error converting object to JSON string", e);
            return null;
        }
    }

    @Override
    public Object convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        try {
            return objectMapper.readTree(dbData);
        } catch (JsonProcessingException e) {
            log.error("Error converting JSON string to Object", e);
            return null;
        }
    }
} 