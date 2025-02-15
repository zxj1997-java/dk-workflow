package vip.lsjscl.flowboot.starter.flow.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import vip.lsjscl.flowboot.starter.flow.dto.WorkflowDiagram;

/**
 * 基于 Jackson 的 JSON 解析工具，将流程图 JSON 转换为 WorkflowDiagram 对象
 *
 * @author zhangxingju
 * @date 2025/02/14
 */
public class WorkflowDiagramParser {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 解析流程图 JSON 字符串
     *
     * @param jsonContent 流程图 JSON 内容，格式请参考 data.json 文件
     * @return WorkflowDiagram 对象
     */
    public static WorkflowDiagram parse(String jsonContent) {
        try {
            return objectMapper.readValue(jsonContent, WorkflowDiagram.class);
        }
        catch (Exception e) {
            throw new RuntimeException("解析流程图JSON失败", e);
        }
    }
} 