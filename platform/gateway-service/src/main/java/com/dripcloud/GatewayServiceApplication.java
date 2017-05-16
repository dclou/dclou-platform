package com.dripcloud;

import com.dripcloud.prefilters.SimpleLoggingPreFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;

@EnableZuulProxy //Acts as reverse proxy, forwarding requests to other services based on routes.
@SpringCloudApplication
public class GatewayServiceApplication {

    @Bean
    public SimpleLoggingPreFilter simplePreFilter() {
        return new SimpleLoggingPreFilter();
    }

    public static void main(String[] args) {
        SpringApplication.run(GatewayServiceApplication.class, args);
    }
}
