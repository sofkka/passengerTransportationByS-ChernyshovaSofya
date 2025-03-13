package com.example.passengertransportation.repository;

import com.example.passengertransportation.model.BookingSeats;
import com.example.passengertransportation.model.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
// интерфейс BookingSeatsRepository наследует функциональность от JpaRepository
public interface BookingSeatsRepository extends JpaRepository<BookingSeats, Long> {
    boolean existsByIdroute(Route route);
    // Добавляем метод поиска бронирований по ФИО
    List<BookingSeats> findByPassengername(String passengername);
}