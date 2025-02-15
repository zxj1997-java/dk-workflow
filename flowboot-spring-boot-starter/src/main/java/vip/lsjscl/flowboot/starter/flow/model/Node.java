package vip.lsjscl.flowboot.starter.flow.model;

import lombok.Data;

/**
 * 节点(活动)
 * 定义流程图中每个节点的基本属性以及扩展数据
 * @author 15331
 */
@Data
public class Node {
    private String id;
    private String shape;
    private int x;
    private int y;
    private int width;
    private int height;
    private String label;
    // 节点类型，如 "start", "activity", "end"
    private String type;
    // 节点附加数据，非activity节点时可能为 null
    private NodeData data;
} 