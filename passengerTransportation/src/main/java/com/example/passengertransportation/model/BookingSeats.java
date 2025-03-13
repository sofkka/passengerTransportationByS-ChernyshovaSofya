package com.example.passengertransportation.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "bookings")
public class BookingSeats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idbooking; // Идентификатор бронирования

    // Отношение многие к одному
    @ManyToOne
    @JoinColumn(name = "routeid", nullable = false)
    @JsonBackReference // Указываем, что это обратная ссылка
    private Route idroute; // Связь с маршрутом

    private String passengername; // ФИО пассажира
    private String bookingdate; // Дата бронирования

    // Конструкторы, геттеры и сеттеры
    public BookingSeats() {
        // Конструктор по умолчанию
    }

    // Конструктор с параметрами
    public BookingSeats(Route routeid, String passengername, String bookingdate) {
        this.idroute = routeid;
        this.passengername = passengername;
        this.bookingdate = bookingdate;
    }

    public Long getIdBooking() {
        return idbooking;
    }

    public void setIdBooking(Long id) {
        this.idbooking = id;
    }

    public Route getRouteid() {
        return idroute;
    }

    public void setRouteid(Route route) {
        this.idroute = route;
    }

    public String getPassengername() {
        return passengername;
    }

    public void setPassengername(String passengerName) {
        this.passengername = passengerName;
    }

    public String getBookingdate() {
        return bookingdate;
    }

    public void setBookingdate(String bookingDate) {
        this.bookingdate = bookingDate;
    }
}