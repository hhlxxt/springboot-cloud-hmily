package com.zoro.tcc.hmily.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class SpringbootCloudHmilyEurekaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootCloudHmilyEurekaApplication.class, args);
    }

}
