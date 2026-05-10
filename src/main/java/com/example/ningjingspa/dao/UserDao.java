package com.example.ningjingspa.dao;

import com.example.ningjingspa.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserDao extends JpaRepository<User, Integer> {

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO user (name, age, email, password) VALUES (?1, ?2, ?3, ?4)", nativeQuery = true)
    void insert(String name, int age, String email, String password);

    @Query(value = "SELECT COUNT(email) FROM user WHERE email = ?1", nativeQuery = true)
    int getEmailCount(String email);

    @Query(value = "SELECT * FROM user WHERE email = ?1", nativeQuery = true)
    User getByEmail(String email);

    @Query(value = "SELECT * FROM user WHERE reset_token = ?1", nativeQuery = true)
    User findByResetToken(String token);
}