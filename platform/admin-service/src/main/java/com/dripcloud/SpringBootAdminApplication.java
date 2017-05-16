package com.dripcloud;

import de.codecentric.boot.admin.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;


@EnableAdminServer
@SpringCloudApplication
public class SpringBootAdminApplication {
	public static void main(String[] args) {
		SpringApplication.run(SpringBootAdminApplication.class, args);
	}
}