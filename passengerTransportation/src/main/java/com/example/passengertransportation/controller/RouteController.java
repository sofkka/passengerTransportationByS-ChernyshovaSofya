package com.example.passengertransportation.controller;

import com.example.passengertransportation.model.Route;
import com.example.passengertransportation.service.RouteService;
import io.swagger.v3.oas.annotations.Operation;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
public class RouteController {

    @Autowired
    private RouteService routeService; // Сервис для работы с маршрутами

    // Получение всех маршрутов
    @Operation(
            summary = "Получение всех маршрутов",
            description = "Возвращает список всех доступных маршрутов")
    @GetMapping("/routes")
    public List<Route> getAllRoutes() {
        return routeService.getAllRoutes();
    }

    // Получение маршрута по ID
    @Operation(
            summary = "Получение маршрута по ID",
            description = "Возвращает маршрут по указанному идентификатору")
    @GetMapping("/{id}")
    public Route getRouteById(@PathVariable Long id) {
        return routeService.getRouteById(id);
    }

    // Получение маршрута по типу транспорта
    @Operation(
            summary = "Получение маршрута по типу транспорта",
            description = "Возвращает маршрут по указанному типу транспорта")
    @GetMapping("/transport/{transporttype}")
    public ResponseEntity<List<Route>> getRoutesByTransportType(@PathVariable String transporttype) {
        try {
            List<Route> routes = routeService.getRoutesByTransportType(transporttype);
            if (routes.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(routes);
            }
            return ResponseEntity.ok(routes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    // Получение маршрута на определенную дату отправления
    @Operation(
            summary = "Получение маршрута на определенную дату отправления",
            description = "Возвращает маршруты на определенную дату отправления")
    @GetMapping("/startDate")
    public ResponseEntity<List<Route>> getRoutesByDepartureDate(@RequestParam String startDate) {
        try {
            List<Route> routes = routeService.getRoutesByDepartureDate(startDate);
            if (routes.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(routes);
            }
            return ResponseEntity.ok(routes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }


    // Получение маршрутов по промежутку дат отправления
    @Operation(
            summary = "Получение маршрутов по промежутку дат отправления",
            description = "Возвращает маршруты, подходящие по промежутку дат отправления")
    @GetMapping("/intervalDates")
    public ResponseEntity<?> getRoutesByIntervalOfDates(@RequestParam String dateOne, @RequestParam String dateTwo) {
        try {
            List<Route> routes = routeService.getRoutesByIntervalOfDates(dateOne, dateTwo);
            return ResponseEntity.ok(routes);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    // Получение маршрута по пункту отправления и назначения
    @Operation(
            summary = "Получение маршрута по пункту отправления и назначения",
            description = "Возвращает маршрут по указанному пункту отправления и назначения")
    @GetMapping("/points")
    public ResponseEntity<List<Route>> getRoutesByDepartureAndDestinationPoint(
            @RequestParam String departurepoint,
            @RequestParam String destinationpoint) {
        try {
            List<Route> routes = routeService.getRoutesByDepartureAndDestinationPoint(departurepoint, destinationpoint);
            if (routes.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(routes);
            }
            return ResponseEntity.ok(routes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @Operation(
            summary = "Фильтрация маршрутов по нескольким параметрам",
            description = "Возвращает маршруты, отфильтрованные по указанным параметрам")
    @GetMapping("/filteredRoutes")
    public ResponseEntity<List<Route>> getFilteredRoutes(
            @RequestParam(required = false) String departurepoint,
            @RequestParam(required = false) String destinationpoint,
            @RequestParam(required = false) String transporttype,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String dateOne,
            @RequestParam(required = false) String dateTwo) {
        try {
            List<Route> routes = routeService.getFilteredRoutes(departurepoint, destinationpoint, transporttype, startDate, dateOne, dateTwo);
            if (routes.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(routes);
            }
            return ResponseEntity.ok(routes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }


    // Обновление существующего маршрута по ID
    @Operation(
            summary = "Обновление маршрута",
            description = "Обновляет информацию о маршруте по указанному идентификатору")
    @PutMapping("/{id}")
    public Route updateRoute(@PathVariable Long id, @RequestBody Route route) {
        return routeService.updateRoute(id, route);
    }


    // Создание нового маршрута
    @Operation(
            summary = "Добавление маршрута",
            description = "Позволяет добавить новый маршрут")
    @PostMapping("")
    public Route createRoute(@RequestBody Route route) {
        return routeService.createRoute(route);
    }



    // Удаление маршрута по ID
    @Operation(
            summary = "Удаление маршрута",
            description = "Удаляет маршрут по указанному идентификатору")
    @OnDelete(action= OnDeleteAction.NO_ACTION)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRoute(@PathVariable Long id) {
        try {
            routeService.deleteRoute(id);
            return ResponseEntity.ok("Маршрут успешно удален.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
