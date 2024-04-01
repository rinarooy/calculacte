package com.example.calculacte.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Controller
public class CalculacteController {
    @GetMapping("/calculacte")
    public String thyTest(){
        return "index";
    }
    @PostMapping("/calculacte")
    public String processData(@RequestParam Integer salary, @RequestParam String stdate, @RequestParam String enddate, Model model) {
        // Обработка данных
        Date stDate = Date.valueOf(stdate);
        Date enDate = Date.valueOf(enddate);

        if (stDate.after(enDate)){
            String msg = "Дата конца отпуска раньше даты начала";
            model.addAttribute("message", msg);
        }
        else{
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            LocalDate startDate = LocalDate.parse(stdate, formatter);
            LocalDate endDate = LocalDate.parse(enddate, formatter);
            long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1; // +1 для включения последнего дня
            long weekends = 0;

            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                if (date.getDayOfWeek() == DayOfWeek.SUNDAY || date.getDayOfWeek() == DayOfWeek.SATURDAY) {
                    weekends++;
                }
            }

            long daysWithoutWeekends = totalDays - weekends;

            int totalDaysInMonth = LocalDate.of(startDate.getYear(), startDate.getMonth(), 1).lengthOfMonth();
            int workingDays = 0;

            for (int day = 1; day <= totalDaysInMonth; day++) {
                LocalDate date = LocalDate.of(startDate.getYear(), startDate.getMonth(), day);
                DayOfWeek dayOfWeek = date.getDayOfWeek();

                if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
                    workingDays++;
                }
            }
            double totalVacationPay = (double) salary /workingDays*daysWithoutWeekends;
            String msg = String.format("Сумма отпускных %f", totalVacationPay);
            model.addAttribute("message", msg);
        }

        return "index";
    }
}
