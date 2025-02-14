package vip.lsjscl.flowboot.flow.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import vip.lsjscl.flowboot.flow.dict.TaskStatus;

/**
 * 任务状态转换器
 *
 * @author zhangxingju
 * @date 2025/02/14
 */
@Converter(autoApply = true)
public class TaskStatusConverter implements AttributeConverter<TaskStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(TaskStatus attribute) {
        return attribute != null ? attribute.getCode() : null;
    }

    @Override
    public TaskStatus convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return null;
        }
        return TaskStatus.fromCode(dbData);
    }
} 