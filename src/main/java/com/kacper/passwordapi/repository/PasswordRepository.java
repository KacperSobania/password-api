package com.kacper.passwordapi.repository;

import com.kacper.passwordapi.entity.Password;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordRepository extends JpaRepository<Password, Long> {

    Password findFirstByPassword(String password);

    void deleteByPassword(String password);
}
