package vip.lsjscl.flowboot.flow.model;

import lombok.Data;

/**
 * 节点(活动)
 *
 * @author zhangxingju
 * @date 2025/02/13
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
    // "start", "activity", "end"
    private String type;
    // 非 activity 节点 data 可能为 null
    private NodeData data;
} 