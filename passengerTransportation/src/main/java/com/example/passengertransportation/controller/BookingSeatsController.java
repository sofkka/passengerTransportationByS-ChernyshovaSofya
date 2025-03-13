package com.example.passengertransportation.controller;

import com.example.passengertransportation.model.BookingSeats;
import com.example.passengertransportation.model.Route;
import com.example.passengertransportation.repository.BookingSeatsRepository;
import com.example.passengertransportation.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingSeatsController {

    @Autowired
    private BookingService bookingService; // Сервис для работы с бронированиями

    // Получение всех бронирований
    @Operation(
            summary = "Получение всех бронирований",
            description = "Возвращает список всех доступных бронирований")
    @GetMapping("")
    public List<BookingSeats> getAllBookings() {
        return bookingService.getAllBookings();
    }

    // Получение бронирования по ID
    @Operation(
            summary = "Получение бронирования по ID",
            description = "Возвращает информацию о бронировании по указанному идентификатору")
    @GetMapping("/{id}")
    public BookingSeats getBookingById(@PathVariable Long id) {
        return bookingService.getBookingById(id);
    }

    // Получение бронирований по ФИО
    @Operation(
            summary = "Поиск бронирований по ФИО",
            description = "Возвращает список бронирований для указанного пассажира")
    // Эндпоинт поиска бронирований по ФИО
    @GetMapping("/search")
    public ResponseEntity<?> getBookingsByFullName(@RequestParam String fullName) {
        List<BookingSeats> bookings = bookingService.getBookingsByFullName(fullName);

        if (bookings.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Бронирования не найдены.");
        }

        return ResponseEntity.ok(bookings);
    }

    // Создание нового бронирования
    @Operation(
            summary = "Добавление брони",
            description = "Позволяет добавить бронь")
    @PostMapping("/createNewBooking")
    public ResponseEntity<?> createBooking(@RequestParam int routeId, @RequestParam String passengerName, @RequestParam String bookingDate) {
        try {
            Long routeId2 = (long) routeId;

            // Создаем новое бронирование
            BookingSeats booking = bookingService.createBooking(routeId2, passengerName, bookingDate);
            return ResponseEntity.ok(booking);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    // Удаление бронирования по ID
    @Operation(
            summary = "Удаление бронирования",
            description = "Удаляет бронирование по указанному идентификатору")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBooking(@PathVariable Long id) {
        try {
            bookingService.deleteBooking(id);
            return ResponseEntity.ok("Бронирование успешно удалено.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
