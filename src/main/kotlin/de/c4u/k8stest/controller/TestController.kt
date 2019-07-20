package de.c4u.k8stest.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * @author Thomas Uhlig
 */
@RestController
@RequestMapping("/hello")
class TestController {

    @GetMapping
    fun message(): String {
        return "Hello Kubernetes!"
    }

    @GetMapping("/{name}")
    fun message(@PathVariable name: String): String {
        return "Hello $name!"
    }
}
