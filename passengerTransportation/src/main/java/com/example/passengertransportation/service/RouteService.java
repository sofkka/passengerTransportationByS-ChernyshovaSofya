package com.example.passengertransportation.service;

import com.example.passengertransportation.model.Route;
import com.example.passengertransportation.repository.BookingSeatsRepository;
import com.example.passengertransportation.repository.RouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RouteService {

    @Autowired
    private RouteRepository routeRepository; // Репозиторий для работы с маршрутами

    @Autowired
    private BookingSeatsRepository bookingSeatsRepository; // Репозиторий для работы с бронированиями

    // Получение всех маршрутов
    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }

    // Получение маршрута по его ID
    public Route getRouteById(Long idroute) {
        return routeRepository.findById(idroute).orElse(null);
    }

    // Создание нового маршрута
    public Route createRoute(Route route) {
        return routeRepository.save(route);
    }

    // Обновление существующего маршрута
    public Route updateRoute(Long idroute, Route route) {
        Route existingRoute = routeRepository.findById(idroute).orElse(null);
        if (existingRoute != null) {
            // Обновление данных маршрута
            existingRoute.setTransporttype(route.getTransporttype());
            existingRoute.setDeparturepoint(route.getDeparturepoint());
            existingRoute.setDestinationpoint(route.getDestinationpoint());
            existingRoute.setDeparturetime(route.getDeparturetime());
            existingRoute.setArrivaltime(route.getArrivaltime());
            existingRoute.setAvailableseats(route.getAvailableseats());
            return routeRepository.save(existingRoute);
        }
        return null; // Возвращает null, если маршрут с таким ID не найден
    }

    // Удаление маршрута по ID
    public void deleteRoute(Long idroute) {
        Route route = routeRepository.findById(idroute).orElse(null);
        if (route == null) {
            throw new IllegalArgumentException("Маршрут с таким ID не найден.");
        }

        // Проверка наличия связанных бронирований
        if (bookingSeatsRepository.existsByIdroute(route)) {
            throw new IllegalStateException("Нельзя удалить маршрут, так как на него есть бронирования.");
        }

        routeRepository.deleteById(idroute);
    }

    public List<Route> getRoutesByTransportType(String transporttype) {
        List<Route> routes = routeRepository.findByTransporttype(transporttype);
        if (routes.isEmpty()) {
            throw new IllegalStateException("Маршруты с указанным типом транспорта не найдены.");
        }
        return routes;
    }


    // Метод для поиска маршрутов на определенную дату
    public List<Route> getRoutesByDepartureDate(String startDate) {
        List<Route> routes = new ArrayList<>();
        DateFormat inputFormatter = new SimpleDateFormat("dd.MM.yyyy"); // Формат входной строки
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date date = inputFormatter.parse(startDate);

            for (Route route : routeRepository.findAll()) {
                Date date2 = formatter.parse(route.getDeparturetime().substring(0, 10));
                if (date.equals(date2)) {
                    routes.add(route);
                }
            }

            if (routes.isEmpty()) {
                throw new IllegalStateException("Маршруты с указанной датой не найдены.");
            }

            return routes;
        } catch (ParseException e) {
            throw new IllegalStateException("Маршруты с такой датой не найдены.");
        }
    }



    // Метод для поиска маршрутов по промежутку дат отправления
    public List<Route> getRoutesByIntervalOfDates(String dateOne, String dateTwo) {
        List<Route> routes = new ArrayList<>();
        DateFormat inputFormatter = new SimpleDateFormat("dd.MM.yyyy"); // Формат входной строки
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date firstDate = inputFormatter.parse(dateOne);
            Date secondDate = inputFormatter.parse(dateTwo);

            for (Route route : routeRepository.findAll()) {
                Date date = formatter.parse(route.getDeparturetime().substring(0, 10));

                if (date.after(firstDate) && date.before(secondDate)) {
                    routes.add(route);
                }
            }

            if (routes.isEmpty()) {
                throw new IllegalStateException("Маршруты с указанным промежутком дат не найдены.");
            }

            return routes;
        } catch (ParseException e) {
            throw new IllegalStateException("Маршруты с указанным промежутком не найдены.");
        }
    }

    public List<Route> getFilteredRoutes(String departurepoint, String destinationpoint, String transporttype, String startDate, String dateOne, String dateTwo) {
        List<Route> routes = routeRepository.findAll();

        // Фильтрация по пункту отправления и назначения
        if (departurepoint != null && !departurepoint.isEmpty() &&
                destinationpoint != null && !destinationpoint.isEmpty()) {
            routes = routes.stream()
                    .filter(route -> route.getDeparturepoint().equals(departurepoint) &&
                            route.getDestinationpoint().equals(destinationpoint))
                    .collect(Collectors.toList());
        }

        // Фильтрация по типу транспорта (если не пусто)
        if (transporttype != null && !transporttype.isEmpty()) {
            routes = routes.stream()
                    .filter(route -> route.getTransporttype().equals(transporttype))
                    .collect(Collectors.toList());
        }

        // Фильтрация по конкретной дате отправления
        if (startDate != null && !startDate.isEmpty()) {
            DateFormat inputFormatter = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date date = inputFormatter.parse(startDate);
                routes = routes.stream()
                        .filter(route -> {
                            try {
                                Date routeDate = inputFormatter.parse(route.getDeparturetime().substring(0, 10));
                                return date.equals(routeDate);
                            } catch (ParseException e) {
                                throw new RuntimeException("Ошибка при парсинге даты маршрута", e);
                            }
                        })
                        .collect(Collectors.toList());
            } catch (ParseException e) {
                throw new RuntimeException("Ошибка при парсинге даты фильтра", e);
            }
        }

        // Фильтрация по интервалу дат (если оба параметра не пусты)
        if (dateOne != null && !dateOne.isEmpty() &&
                dateTwo != null && !dateTwo.isEmpty()) {
            DateFormat inputFormatter = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date firstDate = inputFormatter.parse(dateOne);
                Date secondDate = inputFormatter.parse(dateTwo);

                routes = routes.stream()
                        .filter(route -> {
                            try {
                                Date routeDate = inputFormatter.parse(route.getDeparturetime().substring(0, 10));
                                return !routeDate.before(firstDate) && !routeDate.after(secondDate);
                            } catch (ParseException e) {
                                throw new RuntimeException("Ошибка при парсинге даты маршрута", e);
                            }
                        })
                        .collect(Collectors.toList());
            } catch (ParseException e) {
                throw new RuntimeException("Ошибка при парсинге дат интервала", e);
            }
        }

        return routes;
    }


    public List<Route> getRoutesByDepartureAndDestinationPoint(String departurepoint, String destinationpoint) {
        if (departurepoint.isEmpty() || destinationpoint.isEmpty())
            throw new IllegalStateException("Введите пункт отправления и назначения!");
        List<Route> routes = routeRepository.findByDeparturepointAndDestinationpoint(departurepoint, destinationpoint);
        if (routes.isEmpty()) {
            throw new IllegalStateException("Маршруты с указанным пунктом отправления и назначения не найдены.");
        }
        return routes;
    }
}
