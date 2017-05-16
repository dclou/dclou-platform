package com.dripcloud;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SleuthController {

    @RequestMapping(value = "/track/{name}")
    public String trackHello(@PathVariable(value = "name") String name) {
        return "Hello : " + name;
    }

}
