package com.microservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * @author zw
 * @date 2020-12-04
 * <p>
 */
@RestController
public class IndexController {

    @GetMapping(value = "/index")
    public Mono<String> index() {
        return Mono.justOrEmpty("index.....");
    }
}
