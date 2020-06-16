package com.forex.forexpredictor.repository;

import com.forex.forexpredictor.domain.UnconfirmedUser;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UnconfirmedUserRepository extends MongoRepository<UnconfirmedUser, String> {

    UnconfirmedUser findByEmail(String email);

}
