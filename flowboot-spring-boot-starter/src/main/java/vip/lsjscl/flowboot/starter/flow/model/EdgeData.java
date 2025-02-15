package vip.lsjscl.flowboot.starter.flow.model;

import lombok.Data;

/**
 * 连线(变迁)数据
 * 用于封装与变迁相关的数据属性
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
    // transition 的类型，如"顺序"、"并行"等
    private String type;
} 