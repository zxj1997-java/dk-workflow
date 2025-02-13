package vip.lsjscl.flowboot.flow.model;

import lombok.Data;

import java.util.List;

/**
 * 流程图
 *
 * @author zhangxingju
 * @date 2025/02/13
 */
@Data
public class FlowDiagram {
    private List<Node> nodes;
    private List<Edge> edges;
} 