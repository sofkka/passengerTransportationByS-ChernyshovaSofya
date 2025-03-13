package com.example.passengertransportation.service;

import com.example.passengertransportation.model.BookingSeats;
import com.example.passengertransportation.model.Route;
import com.example.passengertransportation.repository.BookingSeatsRepository;
import com.example.passengertransportation.repository.RouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class BookingService {

    @Autowired
    private BookingSeatsRepository bookingRepository; // Репозиторий для работы с бронированиями

    @Autowired
    private RouteRepository routeRepository; // Репозиторий для работы с маршрутами

    // Получение всех бронирований
    public List<BookingSeats> getAllBookings() {
        return bookingRepository.findAll();
    }

    // Получение бронирования по ID
    public BookingSeats getBookingById(Long idbooking) {
        return bookingRepository.findById(idbooking).orElse(null);
    }

    // Создание нового бронирования
    public BookingSeats createBooking(Long routeId, String passengerName, String bookingDate) {
        // Проверка, существует ли маршрут с указанным ID
        Route route = routeRepository.findById(routeId).orElse(null);
        if (route == null) {
            throw new IllegalArgumentException("Маршрут с таким ID не найден.");
        }

        // Проверка, есть ли доступные места
        if (route.getAvailableseats() <= 0) {
            throw new IllegalStateException("Нет доступных мест для бронирования.");
        }

        // Уменьшение количество доступных мест
        route.setAvailableseats(route.getAvailableseats() - 1);
        routeRepository.save(route);

        // Создание нового бронирования
        BookingSeats booking = new BookingSeats(route, passengerName, bookingDate);
        return bookingRepository.save(booking);
    }


    // Удаление бронирования по ID
    public void deleteBooking(Long idbooking) {
        BookingSeats booking = bookingRepository.findById(idbooking).orElse(null);
        if (booking == null) {
            throw new IllegalArgumentException("Бронирования с таким ID не найдено.");
        }

        // Получаем маршрут перед удалением
        Route route = booking.getRouteid();

        // Увеличиваем количество доступных мест
        route.setAvailableseats(route.getAvailableseats() + 1);
        routeRepository.save(route); // Сохраняем перед удалением

        // Теперь удаляем бронирование
        bookingRepository.deleteById(idbooking);
    }

        // Метод поиска бронирований по ФИО
        public List<BookingSeats> getBookingsByFullName(String fullName) {
            return bookingRepository.findByPassengername(fullName);
        }
}
