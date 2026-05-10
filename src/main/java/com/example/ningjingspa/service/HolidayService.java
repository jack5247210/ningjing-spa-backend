package com.example.ningjingspa.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.ningjingspa.dao.AppointmentDao;
import com.example.ningjingspa.dao.HolidayDao;
import com.example.ningjingspa.entity.Appointment;
import com.example.ningjingspa.entity.Holiday;

@Service
public class HolidayService {
    @Autowired
    private HolidayDao holidayDao;

    @Autowired
    private AppointmentDao appointmentDao;   // 新增

    public List<Holiday> getAllHolidays() {
        return holidayDao.findAllByOrderByHolidayDateAsc();
    }

    public Holiday addHoliday(Holiday holiday) {
        if (holidayDao.existsByHolidayDate(holiday.getHolidayDate())) {
            throw new RuntimeException("該日期已是公休日");
        }

        // 取消當天所有有效預約 (pending / confirmed)
        LocalDate date = holiday.getHolidayDate();
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);
        List<Appointment> activeAppointments = appointmentDao.findActiveBetween(start, end);
        for (Appointment apt : activeAppointments) {
            apt.setStatus("cancelled");
            appointmentDao.save(apt);
        }

        return holidayDao.save(holiday);
    }

    public void deleteHoliday(Integer id) {
        if (!holidayDao.existsById(id)) {
            throw new RuntimeException("公休日不存在");
        }
        holidayDao.deleteById(id);
    }

    public boolean isHoliday(LocalDate date) {
        return holidayDao.existsByHolidayDate(date);
    }
}