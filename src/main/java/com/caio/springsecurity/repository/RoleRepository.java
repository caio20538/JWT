package com.caio.springsecurity.repository;

import com.caio.springsecurity.entities.RoleEntity;
import com.caio.springsecurity.entities.enums.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    RoleEntity findByName(RoleEnum name);
}
