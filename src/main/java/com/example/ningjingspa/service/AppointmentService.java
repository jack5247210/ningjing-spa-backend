package com.example.ningjingspa.service;

import com.example.ningjingspa.dao.AppointmentDao;
import com.example.ningjingspa.dao.EssentialOilDao;
import com.example.ningjingspa.dao.HolidayDao;
import com.example.ningjingspa.dao.ProductDao;
import com.example.ningjingspa.dao.UserDao;
import com.example.ningjingspa.entity.Appointment;
import com.example.ningjingspa.entity.Product;
import com.example.ningjingspa.entity.User;
import com.example.ningjingspa.req.AppointmentReq;
import com.example.ningjingspa.res.AppointmentRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class AppointmentService {

    @Autowired
    private AppointmentDao appointmentdao;

    @Autowired
    private UserDao userdao;

    @Autowired
    private ProductDao productdao;

    @Autowired
    private EssentialOilDao essentialOildao;

    @Autowired
    private HolidayDao holidaydao;

    // ✅ 注入 LINE 推播服務
    @Autowired
    private LineMessagingService lineMessagingService;

    // 預約
    public AppointmentRes create(AppointmentReq req) {
        LocalDateTime appointmentTime = req.getAppointmentTime();
        LocalDate appointmentDate = appointmentTime.toLocalDate();

        if (holidaydao.existsByHolidayDate(appointmentDate)) {
            throw new RuntimeException("公休日無法預約");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime maxTime = now.plusDays(21);
        if (appointmentTime.toLocalDate().isBefore(now.toLocalDate()) ||
            appointmentTime.isAfter(maxTime)) {
            throw new RuntimeException("只能預約今天起未來三週內的日期");
        }
        if (appointmentTime.toLocalDate().isEqual(now.toLocalDate()) &&
            appointmentTime.isBefore(now)) {
            throw new RuntimeException("不能預約已經過去的時間");
        }

        checkTimeSlotConflict(appointmentTime);

        LocalDateTime startOfDay = appointmentTime.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = appointmentTime.toLocalDate().atTime(23, 59, 59);
        int count = appointmentdao.countActiveByDateRange(startOfDay, endOfDay);
        if (count >= 2) {
            throw new RuntimeException("此日期預約已滿，請選擇其他日期");
        }

        User user = userdao.findById(req.getUserId())
                .orElseThrow(() -> new RuntimeException("使用者不存在"));
        Product product = productdao.findById(req.getProductId())
                .orElseThrow(() -> new RuntimeException("產品不存在"));

        Appointment appointment = new Appointment();
        appointment.setUser(user);
        appointment.setProduct(product);
        appointment.setAppointmentTime(appointmentTime);
        appointment.setStatus("pending");

        if (req.getOilId() != null) {
            appointment.setOilId(req.getOilId());
        }

        Appointment saved = appointmentdao.save(appointment);
        return convertToRes(saved);
    }

    // 取得使用者的預約列表（pending + confirmed）
    public List<AppointmentRes> getByUser(Integer userId) {
        List<Appointment> pending = appointmentdao.findByUserUserIdAndStatusWithProduct(userId, "pending");
        List<Appointment> confirmed = appointmentdao.findByUserUserIdAndStatusWithProduct(userId, "confirmed");
        List<Appointment> all = new ArrayList<>();
        all.addAll(pending);
        all.addAll(confirmed);
        all.sort((a, b) -> a.getAppointmentTime().compareTo(b.getAppointmentTime()));
        return all.stream().map(this::convertToRes).collect(Collectors.toList());
    }

    // 取消預約
    public void cancel(Integer appointmentId, Integer userId) {
        Appointment appointment = appointmentdao.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("預約不存在"));
        if (appointment.getUser().getUserId() != userId) {
            throw new RuntimeException("您沒有權限取消此預約");
        }
        if (!"pending".equals(appointment.getStatus()) && !"confirmed".equals(appointment.getStatus())) {
            throw new RuntimeException("此預約無法取消");
        }
        appointment.setStatus("cancelled");
        appointmentdao.save(appointment);
    }

    // 取得指定日期區間內每天的預約人數（pending + confirmed）
    public Map<LocalDate, Integer> getDailyCounts(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);
        List<Appointment> appointments = appointmentdao.findActiveBetween(start, end);

        Map<LocalDate, Integer> counts = new HashMap<>();
        for (Appointment a : appointments) {
            LocalDate date = a.getAppointmentTime().toLocalDate();
            counts.put(date, counts.getOrDefault(date, 0) + 1);
        }

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            counts.putIfAbsent(date, 0);
        }
        return counts;
    }

    // 管理者確認預約（✅ 已加入即時 LINE 推播）
    public void confirmAppointment(Integer appointmentId) {
        Appointment appointment = appointmentdao.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("預約不存在"));
        if (!"pending".equals(appointment.getStatus())) {
            throw new RuntimeException("只有待確認的預約可以確認");
        }
        appointment.setStatus("confirmed");
        appointmentdao.save(appointment);

        // ✅ 即時發送 LINE 提醒
        User user = appointment.getUser();
        if (user.getLineUserId() != null && !user.getLineUserId().isEmpty()) {
            String productName = appointment.getProduct().getTitle();
            String time = appointment.getAppointmentTime().toString();
            String message = String.format(
                "【寧境芳療】預約已確認\n\n療程：%s\n時間：%s\n\n我們期待為您服務！",
                productName, time
            );
            try {
                lineMessagingService.pushMessage(user.getLineUserId(), message);
            } catch (Exception e) {
                System.err.println("LINE 推播失敗：" + e.getMessage());
            }
        }
    }

    // 管理者拒絕預約
    public void rejectAppointment(Integer appointmentId) {
        Appointment appointment = appointmentdao.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("預約不存在"));
        if (!"pending".equals(appointment.getStatus())) {
            throw new RuntimeException("只有待確認的預約可以拒絕");
        }
        appointment.setStatus("cancelled");
        appointmentdao.save(appointment);
    }

    // 取得所有預約（管理者用）
    public List<AppointmentRes> getAllAppointments() {
        List<Appointment> appointments = appointmentdao.findAllWithUserAndProduct();
        return appointments.stream()
                .map(this::convertToRes)
                .collect(Collectors.toList());
    }

    // 檢查同一時段是否已有 pending 或 confirmed 預約
    private void checkTimeSlotConflict(LocalDateTime appointmentTime) {
        LocalDate date = appointmentTime.toLocalDate();
        int hour = appointmentTime.getHour();
        LocalDateTime slotStart, slotEnd;

        if (hour >= 9 && hour < 11) {
            slotStart = date.atTime(9, 0);
            slotEnd = date.atTime(11, 0);
        } else if (hour >= 14 && hour < 16) {
            slotStart = date.atTime(14, 0);
            slotEnd = date.atTime(16, 0);
        } else if (hour >= 16 && hour < 18) {
            slotStart = date.atTime(16, 0);
            slotEnd = date.atTime(18, 0);
        } else if (hour >= 18 && hour < 20) {
            slotStart = date.atTime(18, 30);
            slotEnd = date.atTime(20, 0);
        } else {
            throw new RuntimeException("無效的預約時段");
        }

        List<Appointment> existing = appointmentdao.findActiveBetween(slotStart, slotEnd);
        if (!existing.isEmpty()) {
            throw new RuntimeException("該時段已被預約，請選擇其他時段");
        }
    }

    // 轉換 Entity -> Response
    private AppointmentRes convertToRes(Appointment a) {
        AppointmentRes res = new AppointmentRes();
        res.setAppointmentId(a.getAppointmentId());
        res.setUserId(a.getUser().getUserId());
        res.setUserName(a.getUser().getName());
        res.setAppointmentTime(a.getAppointmentTime());
        res.setStatus(a.getStatus());
        res.setProductName(a.getProduct().getTitle());

        if (a.getOilId() != null) {
            res.setOilId(a.getOilId());
            essentialOildao.findById(a.getOilId()).ifPresent(oil -> res.setOilName(oil.getName()));
        }
        return res;
    }
    
    public boolean isHoliday(LocalDate date) {
        if (date.getDayOfWeek() == DayOfWeek.THURSDAY) {
            return true;
        }
        return holidaydao.existsByHolidayDate(date);
    }
}