package com.forex.forexpredictor.repository;

import com.forex.forexpredictor.domain.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoleRepository extends MongoRepository<Role, String> {

    Role findByRole(String role);
}

