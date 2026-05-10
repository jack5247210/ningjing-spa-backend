package com.example.ningjingspa.dao;

import com.example.ningjingspa.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentDao extends JpaRepository<Appointment, Integer> {

    // 查詢某使用者的特定狀態預約（例如 pending 或 confirmed）
    List<Appointment> findByUser_UserIdAndStatusOrderByAppointmentTimeAsc(Integer userId, String status);

    // 查詢某時間區間內的有效預約數量（pending 或 confirmed）
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.appointmentTime BETWEEN :start AND :end AND a.status IN ('pending', 'confirmed')")
    int countActiveByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // 查詢某時間區間內的所有預約（不限制狀態，通常用於衝突檢查）
    List<Appointment> findByAppointmentTimeBetween(LocalDateTime start, LocalDateTime end);

    // 查詢某時間區間內且狀態為特定值的預約
    List<Appointment> findByAppointmentTimeBetweenAndStatus(LocalDateTime start, LocalDateTime end, String status);

    // 查詢某時間區間內且狀態為 pending 或 confirmed 的預約（用於時段衝突）
    @Query("SELECT a FROM Appointment a WHERE a.appointmentTime BETWEEN :start AND :end AND a.status IN ('pending', 'confirmed')")
    List<Appointment> findActiveBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // 查詢某使用者的預約，並帶出產品資訊（避免 LazyInitializationException）
    @Query("SELECT a FROM Appointment a JOIN FETCH a.product WHERE a.user.userId = :userId AND a.status = :status ORDER BY a.appointmentTime ASC")
    List<Appointment> findByUserUserIdAndStatusWithProduct(@Param("userId") Integer userId, @Param("status") String status);

    // 查詢所有預約，並帶出使用者與產品資訊（管理者用）
    @Query("SELECT a FROM Appointment a JOIN FETCH a.user JOIN FETCH a.product ORDER BY a.appointmentTime DESC")
    List<Appointment> findAllWithUserAndProduct();
    
 // AppointmentDao.java
    List<Appointment> findByStatusAndAppointmentTimeBetween(String status, LocalDateTime start, LocalDateTime end);
}