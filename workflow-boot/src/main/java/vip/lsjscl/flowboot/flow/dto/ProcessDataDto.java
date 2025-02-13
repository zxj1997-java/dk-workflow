package vip.lsjscl.flowboot.flow.dto;

import lombok.Data;
import vip.lsjscl.flowboot.flow.dict.TaskDecision;

@Data
public class ProcessDataDto {
    Long id;

    TaskDecision action;

    String comment;
}
