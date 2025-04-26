package hertfordshire.razmak;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ClinicSystemTests {

    private Clinic clinic;
    private Physiotherapist physiotherapist;
    private Patient patient;
    private TreatmentType treatmentType;
    private Appointment appointment;

    @BeforeEach
    void setUp() {
        clinic = new Clinic();
        ExpertiseArea expertise = new ExpertiseArea("Physiotherapy");
        physiotherapist = new Physiotherapist(100, "Dr. Test", "123 Test St", "1234567890", List.of(expertise));
        patient = new Patient(100, "Test Patient", "456 Test St", "0987654321");
        treatmentType = new TreatmentType("Test Treatment", expertise);
        appointment = new Appointment(100, physiotherapist, treatmentType, LocalDateTime.of(2025, 5, 1, 10, 0));
    }

    @Test
    void testAppointmentIsAvailable() {
        // Test the isAvailable method in Appointment class
        assertTrue(appointment.isAvailable(), "Appointment should be available when no booking is set");
        Booking booking = new Booking(1, patient, appointment);
        appointment.setBooking(booking);
        assertFalse(appointment.isAvailable(), "Appointment should not be available after booking");
    }

    @Test
    void testBookingSetStatus() {
        // Test the setStatus method in Booking class
        Booking booking = new Booking(1, patient, appointment);
        booking.setStatus("attended");
        assertEquals("attended", booking.getStatus(), "Booking status should be updated to attended");
    }

    @Test
    void testClinicBookAppointment() {
        // Use existing physiotherapist and patient from Clinic's initialized data
        Patient existingPatient = clinic.getPatients().get(0); // First pre-registered patient
        Physiotherapist existingPhysio = clinic.getPhysiotherapistsByExpertise("Physiotherapy").get(0); // First physio with Physiotherapy expertise

        // Add a new appointment to the existing physiotherapist
        Appointment newAppointment = new Appointment(100, existingPhysio, treatmentType, LocalDateTime.of(2025, 5, 1, 10, 0));
        existingPhysio.addAppointment(newAppointment);

        // Test booking the appointment
        boolean result = clinic.bookAppointment(existingPatient.getId(), newAppointment.getId());
        assertTrue(result, "Appointment should be booked successfully");
    }

    @Test
    void testPhysiotherapistAddAppointment() {
        // Test the addAppointment method in Physiotherapist class
        int initialSize = physiotherapist.getTimetable().size();
        physiotherapist.addAppointment(appointment);
        assertEquals(initialSize + 1, physiotherapist.getTimetable().size(), "Timetable should increase by 1 after adding appointment");
        assertTrue(physiotherapist.getTimetable().contains(appointment), "Timetable should contain the added appointment");
    }

    @Test
    void testTreatmentTypeGetExpertiseArea() {
        // Test the getExpertiseArea method in TreatmentType class
        ExpertiseArea expertise = treatmentType.getExpertiseArea();
        assertNotNull(expertise, "Expertise area should not be null");
        assertEquals("Physiotherapy", expertise.getName(), "Expertise area name should match");
    }
}