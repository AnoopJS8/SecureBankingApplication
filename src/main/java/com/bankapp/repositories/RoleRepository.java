package com.bankapp.repositories;

import org.springframework.data.repository.CrudRepository;

import com.bankapp.models.Role;

public interface RoleRepository extends CrudRepository<Role, Long> {

    Role findByName(String name);

}