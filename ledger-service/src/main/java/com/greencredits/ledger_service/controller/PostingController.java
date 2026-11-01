package com.greencredits.ledger_service.controller;

import com.greencredits.ledger_service.posting.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/postings")
public class PostingController {
    private final PostingService postingService;

    public PostingController(PostingService postingService) {
        this.postingService = postingService;
    }

    @PostMapping
    public ResponseEntity<PostingResponse> create(@RequestHeader("Idempotency-Key") String idempotencyKey, @Valid @RequestBody PostingRequest req){
        PostingResult result = postingService.post(idempotencyKey,req);
        HttpStatus status=result.created() ? HttpStatus.CREATED : HttpStatus.OK;
        return ResponseEntity.status(status).body(result.body());
    }

}
