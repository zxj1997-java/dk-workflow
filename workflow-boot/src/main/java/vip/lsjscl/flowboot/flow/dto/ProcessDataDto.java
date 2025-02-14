package vip.lsjscl.flowboot.flow.dto;

import lombok.Data;
import vip.lsjscl.flowboot.flow.dict.TaskDecision;

/**
 * 处理数据 DTO
 *
 * @author zhangxingju
 * @date 2025/02/14
 */
@Data
public class ProcessDataDto {
    Long id;

    TaskDecision action;

    String comment;
}
