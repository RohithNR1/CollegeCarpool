package model;

import javafx.beans.property.*;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;

public class Trip {
    private final int tripId;
    private final StringProperty driverName;
    private final StringProperty origin;
    private final StringProperty destination;
    private final ObjectProperty<Date> tripDate;
    private final ObjectProperty<Time> tripTime;
    private final IntegerProperty seatsAvailable;
    private final DoubleProperty farePerSeat;

    public Trip(int tripId, String driverName, String origin, String destination, Date tripDate, Time tripTime, int seatsAvailable, double farePerSeat) {
        this.tripId = tripId;
        this.driverName = new SimpleStringProperty(driverName);
        this.origin = new SimpleStringProperty(origin);
        this.destination = new SimpleStringProperty(destination);
        this.tripDate = new SimpleObjectProperty<>(tripDate);
        this.tripTime = new SimpleObjectProperty<>(tripTime);
        this.seatsAvailable = new SimpleIntegerProperty(seatsAvailable);
        this.farePerSeat = new SimpleDoubleProperty(farePerSeat);
    }

    public int getTripId() { return tripId; }

    public StringProperty driverNameProperty() { return driverName; }
    public String getDriverName() { return driverName.get(); }
    
    public StringProperty originProperty() { return origin; }
    public String getOrigin() { return origin.get(); }
    
    public StringProperty destinationProperty() { return destination; }
    public String getDestination() { return destination.get(); }
    
    public ObjectProperty<Date> tripDateProperty() { return tripDate; }
    public Date getTripDate() { return tripDate.get(); }
    
    public ObjectProperty<Time> tripTimeProperty() { return tripTime; }
    public Time getTripTime() { return tripTime.get(); }
    
    public IntegerProperty seatsAvailableProperty() { return seatsAvailable; }
    public DoubleProperty farePerSeatProperty() { return farePerSeat; }

    public int getSeatsAvailable() { return seatsAvailable.get(); }
    public double getFarePerSeat() { return farePerSeat.get(); }
}
