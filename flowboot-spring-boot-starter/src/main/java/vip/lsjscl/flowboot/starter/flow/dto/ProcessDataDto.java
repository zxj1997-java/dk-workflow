package vip.lsjscl.flowboot.starter.flow.dto;

import lombok.Data;
import vip.lsjscl.flowboot.starter.flow.dict.TaskDecision;

/**
 * 处理数据 DTO
 *
 * @author zhangxingju
 * @date 2025/02/14
 */
@Data
public class ProcessDataDto {
    String id;

    TaskDecision action;

    String comment;
}
