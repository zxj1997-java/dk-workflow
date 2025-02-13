package vip.lsjscl.flowboot.flow.model;

import lombok.Data;

import java.util.List;

/**
 * 节点(活动)数据
 *
 * @author zhangxingju
 * @date 2025/02/13
 */
@Data
public class NodeData {
    private String name;
    private String code;
    private String pageUrl;
    private String afterClass;
    private List<String> approvers;
    private List<String> departments;
    private List<String> operations;
    // 如果有必要记录 activity 类型
    private String type;
} 