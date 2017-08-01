package stubs;

import db.DataSource;
import db.service.AirplaneService;
import db.service.AirportService;
import db.service.FlightPlaceService;
import pojo.Airplane;
import pojo.Airport;
import pojo.Flight;
import pojo.FlightPlace;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

public class StubUtils {
    //TODO: этот кусок вынести в DAOimpl можно!

    public static List<Airport> getAirports() {
        AirportService airportService = new AirportService();
        List<Airport> airports = airportService.getAll();
        return airports;
    }

    public static List<Airplane> getAirplanes() {
        AirplaneService airplaneService = new AirplaneService();
        List<Airplane> airplanes = airplaneService.getAll();
        return airplanes;
    }

    public static List<Flight> getFlights(List<Airport> airports, List<Airplane> airplanes) {
        Connection connection = DataSource.getConnection();

        List<Flight> flights = new ArrayList<>();
        try {
            String sql = "SELECT * FROM flight";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                Long id = result.getLong("id");
                Long airplaneId = result.getLong("airplane_id");
                String flightNumber = result.getString("flight_number");
                Long departureAirportId = result.getLong("departure_airport_id");
                Long arrivalAirportId = result.getLong("arrival_airport_id");
                Integer availableEconom = result.getInt("available_places_econom");
                Integer availableBusiness = result.getInt("available_places_business");
                Airport departureAirport = new Airport();
                Airport arrivalAirport = new Airport();
                for (Airport airport : airports) {
                    if (departureAirportId == airport.getAirportId())
                        departureAirport = airport;
                    if (arrivalAirportId == airport.getAirportId())
                        arrivalAirport = airport;
                }
                Airplane airplaneFlight = new Airplane();
                for (Airplane airplane : airplanes) {
                    if (airplaneId.equals(airplane.getAirplaneId())) {
                        airplaneFlight = airplane;
                    }
                }
                Double baseCost = result.getDouble("base_cost");
                LocalDateTime dateTime = result.getTimestamp("flight_datetime").toLocalDateTime();
                flights.add(new Flight(id, airplaneFlight, flightNumber, departureAirport, arrivalAirport, baseCost, availableEconom, availableBusiness, dateTime));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flights;
    }

    public static int randomSittingPlaceEconom(Flight flight) {
        int place = 0;
        FlightPlace flightPlace = null;
        FlightPlaceService flightPlaceService = new FlightPlaceService();
        Optional<FlightPlace> flightPlaceOptional = flightPlaceService.get((int)flight.getFlightId());
        if (flightPlaceOptional.isPresent()) {
            flightPlace = flightPlaceOptional.get();
            BitSet places = flightPlace.getBitPlacesEconom();
            int length = places.length();
            Random random = new Random(47);
            for (int i=0; i < length; i++) {
                place = random.nextInt(length);
                while (places.get(place)) {
                    if (places.get(place)) {
                        place = random.nextInt(length);
                    } else break;
                }
            }
        }
        System.out.println(place);
        return place;
    }
}