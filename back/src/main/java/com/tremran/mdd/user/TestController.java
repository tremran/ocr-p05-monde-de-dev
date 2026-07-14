package com.tremran.mdd.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/me")
    public ResponseEntity<String> me() {
        return ResponseEntity.ok("authenticated");
    }
}
