package com.forex.forexpredictor.repository;

import com.forex.forexpredictor.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {

    User findByEmail(String email);

}

