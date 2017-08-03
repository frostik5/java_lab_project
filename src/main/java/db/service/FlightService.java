package db.service;

import db.DataSource;
import db.dao.FlightDAO;
import lombok.SneakyThrows;
import pojo.Airplane;
import pojo.Airport;
import pojo.Flight;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FlightService implements FlightDAO {
    private static final String SELECT_ALL =
            "SELECT\n" +
            "  f.id, airplane_id, p.name, p.capacity_econom, p.capacity_business, flight_number, " +
            "  departure_airport_id, d.code, d.city, d.airport_name, d.latitude, d.longitude, " +
                    "arrival_airport_id, a.code, a.city, a.airport_name, a.latitude, a.longitude, " +
            "  base_cost, available_places_econom, available_places_business, flight_datetime " +
            "FROM Flight f\n" +
            "  JOIN Airplane p ON p.id = f.airplane_id\n" +
            "  JOIN Airport  d ON d.id = f.departure_airport_id\n" +
            "  JOIN Airport  a ON a.id = f.arrival_airport_id\n";
    private static final String ORDER_BY_DATETIME_AND_BASECOST = "ORDER BY flight_datetime, base_cost";

    @Override
    @SneakyThrows
    public long create(Flight flight) {
        String sql = "INSERT INTO Flight (airplane_id, flight_number, departure_airport_id, arrival_airport_id, " +
            "base_cost, available_places_econom, available_places_business, flight_datetime) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try(Connection connection = DataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong      (1, flight.getAirplane().getAirplaneId());
            ps.setString    (2, flight.getFlightNumber());
            ps.setLong      (3, flight.getDepartureAirport().getAirportId());
            ps.setLong      (4, flight.getArrivalAirport().getAirportId());
            ps.setDouble    (5, flight.getBaseCost());
            ps.setInt       (6, flight.getAvailablePlacesEconom());
            ps.setInt       (7, flight.getAvailablePlacesBusiness());
            ps.setTimestamp (8, Timestamp.valueOf(flight.getDateTime()));

            ps.executeUpdate();

            try (ResultSet generetedKeys = ps.getGeneratedKeys()) {
                if (generetedKeys.next()) {
                    flight.setFlightId(generetedKeys.getLong(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return flight.getFlightId();
    }

    @Override
    @SneakyThrows
    public Optional<Flight> get(int id) {
        String sql = SELECT_ALL + "WHERE f.id = ?\n" + ORDER_BY_DATETIME_AND_BASECOST;

        try(Connection connection = DataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);

            ResultSet rs = ps.executeQuery();

            Flight flight = null;
            while (rs.next()) {
                flight = createNewFlight(rs);
            }

            return Optional.ofNullable(flight);
        }
    }

    @Override
    @SneakyThrows
    public void update(Flight flight) {
        String sql = "UPDATE Flight SET airplane_id = ?, flight_number = ?, departure_airport_id = ?, arrival_airport_id =?, " +
                "base_cost = ?, available_places_econom = ?, available_places_business = ?, flight_datetime = ? WHERE id = ?";

        try(Connection connection = DataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong      (1, flight.getAirplane().getAirplaneId());
            ps.setString    (2, flight.getFlightNumber());
            ps.setLong      (3, flight.getDepartureAirport().getAirportId());
            ps.setLong      (4, flight.getArrivalAirport().getAirportId());
            ps.setDouble    (5, flight.getBaseCost());
            ps.setInt       (6, flight.getAvailablePlacesEconom());
            ps.setInt       (7, flight.getAvailablePlacesBusiness());
            ps.setTimestamp (8, Timestamp.valueOf(flight.getDateTime()));
            ps.setLong      (9, flight.getFlightId());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    @SneakyThrows
    public void remove(Flight flight) {
        String sql = "DELETE FROM Flight WHERE id = ?";

        try(Connection connection = DataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, flight.getFlightId());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    @SneakyThrows
    public List<Flight> getAll() {
        List<Flight> flights = new ArrayList<>();
        try(Connection connection = DataSource.getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(SELECT_ALL + ORDER_BY_DATETIME_AND_BASECOST)) {
            while (rs.next()) {
                flights.add(createNewFlight(rs));
            }

            return flights;
        }
    }

    @SneakyThrows
    private Flight createNewFlight(ResultSet rs) {
        return new Flight(
                rs.getLong("id"),
                new Airplane(
                        rs.getLong  ("airplane_id"),
                        rs.getString("p.name"),
                        rs.getInt   ("p.capacity_econom"),
                        rs.getInt   ("p.capacity_business")
                ),
                rs.getString("flight_number"),
                new Airport(
                        rs.getLong  ("departure_airport_id"),
                        rs.getString("d.code"),
                        rs.getString("d.city"),
                        rs.getString("d.airport_name"),
                        rs.getDouble("d.latitude"),
                        rs.getDouble("d.longitude")
                ),
                new Airport(
                        rs.getLong  ("arrival_airport_id"),
                        rs.getString("a.code"),
                        rs.getString("a.city"),
                        rs.getString("a.airport_name"),
                        rs.getDouble("a.latitude"),
                        rs.getDouble("a.longitude")
                ),
                rs.getDouble    ("base_cost"),
                rs.getInt       ("available_places_econom"),
                rs.getInt       ("available_places_business"),
                rs.getTimestamp ("flight_datetime").toLocalDateTime()
        );
    }
}
