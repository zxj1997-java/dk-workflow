package vip.lsjscl.flowboot.starter.exception;

/**
 * 业务异常
 *
 * @author zhangxingju
 * @date 2025/02/14
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
} 