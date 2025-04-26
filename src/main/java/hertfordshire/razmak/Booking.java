package hertfordshire.razmak;

public class Booking {
    private int id;
    private Patient patient;
    private Appointment appointment;
    private String status; // "booked", "attended", "canceled"

    public Booking(int id, Patient patient, Appointment appointment) {
        this.id = id;
        this.patient = patient;
        this.appointment = appointment;
        this.status = "booked";
    }

    public int getId() {
        return id;
    }

    public Patient getPatient() {
        return patient;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }
}