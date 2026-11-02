package com.greencredits.actionintakeservice.action;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ActionRepository extends MongoRepository<Action, String> {
    List<Action> findByPublishedFalse();
}
