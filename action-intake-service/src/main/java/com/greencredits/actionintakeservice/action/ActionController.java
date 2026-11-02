package com.greencredits.actionintakeservice.action;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/actions")
public class ActionController {
    private final ActionService service;

    public ActionController(ActionService service) {
        this.service=service;
    }

    @PostMapping
    public ResponseEntity<ActionResponse> submit(@RequestBody @Valid SubmitActionRequest req){
        return ResponseEntity.status(HttpStatus.CREATED).body(ActionResponse.from(service.submit(req)));
    }

    @GetMapping("/{id}")
    public ActionResponse get(@PathVariable String id){
       return ActionResponse.from(service.get(id));
    }
    
}
