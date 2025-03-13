package com.example.passengertransportation.repository;

import com.example.passengertransportation.model.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
// интерфейс RouteRepository наследует функциональность от JpaRepository
public interface RouteRepository extends JpaRepository<Route, Long> {
    List<Route> findByTransporttype(String transporttype);
    List<Route> findByDeparturepointAndDestinationpoint(String departurepoint, String destinationpoint);
}