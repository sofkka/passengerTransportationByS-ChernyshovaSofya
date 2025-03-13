package com.example.passengertransportation.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "routes")
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idroute; // Идентификатор маршрута
    private String transporttype; // Тип транспорта (авиа, ж/д, автобус и т.д.)
    private String departurepoint; // Пункт отправления
    private String destinationpoint; // Пункт назначения
    private String departuretime; // Время и дата отправления
    private String arrivaltime; // Время и дата прибытия
    private int availableseats; // Количество доступных мест

    @OneToMany(mappedBy = "idroute", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore // Игнорируем поле bookings при сериализации
    private List<BookingSeats> bookings;

    // Конструкторы, геттеры и сеттеры
    public Route() {
        // Конструктор по умолчанию
    }

    // Конструктор с параметрами
    public Route(String transporttype, String departurepoint, String destinationpoint, String departuretime, String arrivaltime, int availableseats) {
        this.transporttype = transporttype;
        this.departurepoint = departurepoint;
        this.destinationpoint = destinationpoint;
        this.departuretime = departuretime;
        this.arrivaltime = arrivaltime;
        this.availableseats = availableseats;
    }

    // Геттеры и сеттеры
    public Long getIdRoute() {
        return idroute;
    }

    public void setIdRoute(Long id) {
        this.idroute = id;
    }

    public String getTransporttype() {
        return transporttype;
    }

    public void setTransporttype(String transportType) {
        this.transporttype = transportType;
    }

    public String getDeparturepoint() {
        return departurepoint;
    }

    public void setDeparturepoint(String departurePoint) {
        this.departurepoint = departurePoint;
    }

    public String getDestinationpoint() {
        return destinationpoint;
    }

    public void setDestinationpoint(String destinationPoint) {
        this.destinationpoint = destinationPoint;
    }

    public String getDeparturetime() {
        return departuretime;
    }

    public void setDeparturetime(String departureTime) {
        this.departuretime = departureTime;
    }

    public String getArrivaltime() {
        return arrivaltime;
    }

    public void setArrivaltime(String arrivalTime) {
        this.arrivaltime = arrivalTime;
    }

    public int getAvailableseats() {
        return availableseats;
    }

    public void setAvailableseats(int availableSeats) {
        this.availableseats = availableSeats;
    }

    public List<BookingSeats> getBookings() {
        return bookings;
    }

    public void setBookings(List<BookingSeats> bookings) {
        this.bookings = bookings;
    }
}