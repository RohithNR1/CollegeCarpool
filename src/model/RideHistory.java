package model;

import javafx.beans.property.*;
import java.sql.Date;
import java.sql.Time;

public class RideHistory {

    private final IntegerProperty tripId;
    private final StringProperty origin;
    private final StringProperty destination;
    private final ObjectProperty<Date> tripDate;
    private final ObjectProperty<Time> tripTime;
    private final IntegerProperty seatsBooked;
    private final DoubleProperty totalFare;
    private final StringProperty role;

    public RideHistory(int tripId, String origin, String destination,
                       Date tripDate, Time tripTime, int seatsBooked, double totalFare, String role) {
        this.tripId = new SimpleIntegerProperty(tripId);
        this.origin = new SimpleStringProperty(origin);
        this.destination = new SimpleStringProperty(destination);
        this.tripDate = new SimpleObjectProperty<>(tripDate);
        this.tripTime = new SimpleObjectProperty<>(tripTime);
        this.seatsBooked = new SimpleIntegerProperty(seatsBooked);
        this.totalFare = new SimpleDoubleProperty(totalFare);
        this.role = new SimpleStringProperty(role);
    }

    public IntegerProperty tripIdProperty() { return tripId; }
    public StringProperty originProperty() { return origin; }
    public StringProperty destinationProperty() { return destination; }
    public ObjectProperty<Date> tripDateProperty() { return tripDate; }
    public ObjectProperty<Time> tripTimeProperty() { return tripTime; }
    public IntegerProperty seatsBookedProperty() { return seatsBooked; }
    public DoubleProperty totalFareProperty() { return totalFare; }
    public StringProperty roleProperty() { return role; }
}
