package vip.lsjscl.flowboot.starter.flow.model;

import lombok.Data;

import java.util.List;

/**
 * 流程图
 * 存储流程图中所有节点和连线的数据
 *
 * @author zhangxingju
 * @date 2025/02/13
 */
@Data
public class FlowDiagram {
    private List<Node> nodes;
    private List<Edge> edges;
} 