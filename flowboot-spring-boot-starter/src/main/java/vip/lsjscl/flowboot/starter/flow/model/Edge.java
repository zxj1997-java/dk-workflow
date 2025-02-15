package vip.lsjscl.flowboot.starter.flow.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * 连线(变迁)
 * 定义流程图中的转换连线路径及相关数据
 *
 * @author zhangxingju
 * @date 2025/02/13
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Edge {
    private String source;
    private String target;
    private List<Object> vertices;
    private EdgeData data;

    // 新增字段：router 和 connector
    private Router router;
    private Connector connector;

    // 内部静态类，用于映射 JSON 中 router 对象
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Router {
        private String name;
    }

    // 内部静态类，用于映射 JSON 中 connector 对象
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Connector {
        private String name;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class EdgeData {
        private String name;
        private String code;
        private String conditionClass;
        private String afterClass;
        private String type;
    }
} 