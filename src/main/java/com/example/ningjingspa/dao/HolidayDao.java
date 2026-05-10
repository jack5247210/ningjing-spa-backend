package com.example.ningjingspa.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ningjingspa.entity.Holiday;

public interface HolidayDao extends JpaRepository<Holiday, Integer> {

	boolean existsByHolidayDate(LocalDate date);
	List<Holiday> findAllByOrderByHolidayDateAsc();
	
	
}
