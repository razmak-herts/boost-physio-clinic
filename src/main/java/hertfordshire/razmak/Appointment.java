package hertfordshire.razmak;

import java.time.LocalDateTime;

public class Appointment {
    private int id;
    private Physiotherapist physiotherapist;
    private TreatmentType treatmentType;
    private LocalDateTime startTime;
    private Booking booking; // null if available

    public Appointment(int id, Physiotherapist physiotherapist, TreatmentType treatmentType, LocalDateTime startTime) {
        this.id = id;
        this.physiotherapist = physiotherapist;
        this.treatmentType = treatmentType;
        this.startTime = startTime;
        this.booking = null;
    }

    public int getId() {
        return id;
    }

    public Physiotherapist getPhysiotherapist() {
        return physiotherapist;
    }

    public TreatmentType getTreatmentType() {
        return treatmentType;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public boolean isAvailable() {
        return booking == null;
    }
}