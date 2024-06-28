package com.example.springbootkafkarabbitmqpostgres.repo;

import com.example.springbootkafkarabbitmqpostgres.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
