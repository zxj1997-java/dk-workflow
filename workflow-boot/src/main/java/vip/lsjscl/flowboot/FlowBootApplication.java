package vip.lsjscl.flowboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Flow Boot 应用程序
 *
 * @author zhangxingju
 * @date 2025/02/14
 */
@SpringBootApplication
@EnableScheduling
public class FlowBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlowBootApplication.class, args);
    }

}
