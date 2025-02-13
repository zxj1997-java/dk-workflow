package vip.lsjscl.flowboot.flow.model;

import lombok.Data;

/**
 * 连线(变迁)数据
 *
 * @author zhangxingju
 * @date 2025/02/13
 */
@Data
public class EdgeData {
    private String name;
    private String code;
    private String conditionClass;
    private String afterClass;
    // transition 的类型
    private String type;
} 