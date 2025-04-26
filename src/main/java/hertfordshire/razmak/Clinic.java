package hertfordshire.razmak;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.HashMap;

public class Clinic {
    private List<Physiotherapist> physiotherapists;
    private List<Patient> patients;
    private List<Booking> bookings;
    private int nextPatientId;
    private int nextAppointmentId;
    private int nextBookingId;

    public Clinic() {
        physiotherapists = new ArrayList<>();
        patients = new ArrayList<>();
        bookings = new ArrayList<>();
        nextPatientId = 1;
        nextAppointmentId = 1;
        nextBookingId = 1;
        initializeData();
    }

    private void initializeData() {
        // Expertise Areas
        ExpertiseArea physio = new ExpertiseArea("Physiotherapy");
        ExpertiseArea rehab = new ExpertiseArea("Rehabilitation");
        ExpertiseArea osteo = new ExpertiseArea("Osteopathy");

        // Treatment Types
        TreatmentType neuralMobilisation = new TreatmentType("Neural mobilisation", physio);
        TreatmentType acupuncture = new TreatmentType("Acupuncture", physio);
        TreatmentType massage = new TreatmentType("Massage", physio);
        TreatmentType spineMobilisation = new TreatmentType("Mobilisation of the spine and joints", physio);
        TreatmentType poolRehab = new TreatmentType("Pool rehabilitation", rehab);

        // Physiotherapists
        Physiotherapist p1 = new Physiotherapist(1, "Dr. Alice Smith", "123 Main St", "1234567890", List.of(physio, rehab));
        Physiotherapist p2 = new Physiotherapist(2, "Dr. Bob Johnson", "456 Elm St", "0987654321", List.of(physio));
        Physiotherapist p3 = new Physiotherapist(3, "Dr. Carol Lee", "789 Oak St", "1122334455", List.of(osteo));
        physiotherapists.addAll(List.of(p1, p2, p3));

        // 4-week timetable (May 2025, weeks 1-4)
        LocalDateTime[][] times = {
                // Week 1
                { LocalDateTime.of(2025, 5, 1, 10, 0), LocalDateTime.of(2025, 5, 1, 11, 0), LocalDateTime.of(2025, 5, 2, 14, 0) },
                // Week 2
                { LocalDateTime.of(2025, 5, 8, 9, 0), LocalDateTime.of(2025, 5, 9, 15, 0), LocalDateTime.of(2025, 5, 10, 10, 0) },
                // Week 3
                { LocalDateTime.of(2025, 5, 15, 13, 0), LocalDateTime.of(2025, 5, 16, 11, 0), LocalDateTime.of(2025, 5, 17, 14, 0) },
                // Week 4
                { LocalDateTime.of(2025, 5, 22, 10, 0), LocalDateTime.of(2025, 5, 23, 12, 0), LocalDateTime.of(2025, 5, 24, 15, 0) }
        };

        TreatmentType[] p1Treatments = {massage, acupuncture, poolRehab};
        TreatmentType[] p2Treatments = {neuralMobilisation, spineMobilisation, massage};
        TreatmentType[] p3Treatments = {spineMobilisation};

        // Assign appointments
        for (int week = 0; week < 4; week++) {
            p1.addAppointment(new Appointment(nextAppointmentId++, p1, p1Treatments[week % 3], times[week][0]));
            p1.addAppointment(new Appointment(nextAppointmentId++, p1, p1Treatments[(week + 1) % 3], times[week][1]));
            p2.addAppointment(new Appointment(nextAppointmentId++, p2, p2Treatments[week % 3], times[week][0])); // Overlap with p1
            p2.addAppointment(new Appointment(nextAppointmentId++, p2, p2Treatments[(week + 1) % 3], times[week][2]));
            p3.addAppointment(new Appointment(nextAppointmentId++, p3, p3Treatments[0], times[week][1]));
        }

        // Patients
        for (int i = 0; i < 10; i++) {
            patients.add(new Patient(nextPatientId++, "Patient " + (i + 1), "Addr " + (i + 1), "555000" + i));
        }
    }


    // Add Patient
    public void addPatient(String name, String address, String telephone) {
        patients.add(new Patient(nextPatientId++, name, address, telephone));
        System.out.println("Patient added successfully");
    }


    // Remove Patient
    public void removePatient(int patientId) {
        Patient patient = findPatientById(patientId);
        if (patient == null) {
            System.out.println("Patient not found.");
            return;
        }
        bookings.removeIf(b -> {
            if (b.getPatient().getId() == patientId) {
                b.getAppointment().setBooking(null);
                return true;
            }
            return false;
        });
        patients.remove(patient);
        System.out.println("Patient removed and bookings cancelled.");
    }

    // Book Appointment
    public boolean bookAppointment(int patientId, int appointmentId) {
        Patient patient = findPatientById(patientId);
        Appointment appointment = findAppointmentById(appointmentId);
        if (patient == null || appointment == null || !appointment.isAvailable()) {
            return false;
        }
        if (hasTimeConflict(patient, appointment.getStartTime())) {
            return false;
        }
        Booking booking = new Booking(nextBookingId++, patient, appointment);
        bookings.add(booking);
        appointment.setBooking(booking);
        return true;
    }

    // Change Booking
    public boolean changeBooking(int bookingId, int newAppointmentId) {
        Booking booking = findBookingById(bookingId);
        Appointment newAppointment = findAppointmentById(newAppointmentId);
        if (booking == null || !booking.getStatus().equals("booked") || newAppointment == null || !newAppointment.isAvailable()) {
            return false;
        }
        Patient patient = booking.getPatient();
        if (hasTimeConflict(patient, newAppointment.getStartTime(), bookingId)) {
            return false;
        }
        Appointment oldAppointment = booking.getAppointment();
        oldAppointment.setBooking(null);
        booking.setAppointment(newAppointment);
        newAppointment.setBooking(booking);
        return true;
    }

    // Cancel Booking
    public boolean cancelBooking(int bookingId) {
        Booking booking = findBookingById(bookingId);
        if (booking == null || !booking.getStatus().equals("booked")) {
            return false;
        }
        booking.setStatus("canceled");
        booking.getAppointment().setBooking(null);
        return true;
    }

    // Attend Booking
    public boolean attendBooking(int bookingId) {
        Booking booking = findBookingById(bookingId);
        if (booking == null || !booking.getStatus().equals("booked")) {
            return false;
        }
        booking.setStatus("attended");
        return true;
    }

    public void printReport() {
        System.out.println("=== Treatment Appointments Report ===");
        // Group all appointments by physiotherapist
        Map<Physiotherapist, List<Appointment>> appointmentsByPhysio = physiotherapists.stream()
                .collect(Collectors.toMap(
                        p -> p,
                        Physiotherapist::getTimetable,
                        (a, b) -> a, // In case of duplicates, keep the first list
                        HashMap::new
                ));

        for (Physiotherapist p : physiotherapists) {
            System.out.println("\nPhysiotherapist: " + p.getName());
            List<Appointment> physioAppointments = appointmentsByPhysio.getOrDefault(p, new ArrayList<>());
            // Collect all bookings for this physiotherapist, including cancelled ones
            List<Booking> physioBookings = bookings.stream()
                    .filter(b -> b.getAppointment().getPhysiotherapist().equals(p))
                    .collect(Collectors.toList());

            // For each appointment, determine its status and patient
            for (Appointment a : physioAppointments) {
                String patientName = "None";
                String status = "available";
                // Check current booking
                if (a.getBooking() != null) {
                    patientName = a.getBooking().getPatient().getName();
                    status = a.getBooking().getStatus();
                } else {
                    // Check for a cancelled booking associated with this appointment
                    Optional<Booking> cancelledBooking = physioBookings.stream()
                            .filter(b -> b.getAppointment().equals(a) && b.getStatus().equals("canceled"))
                            .findFirst();
                    if (cancelledBooking.isPresent()) {
                        patientName = cancelledBooking.get().getPatient().getName();
                        status = "canceled";
                    }
                }
                System.out.printf("Treatment: %s, Time: %s, Patient: %s, Status: %s%n",
                        a.getTreatmentType().getName(), a.getStartTime(), patientName, status);
            }
        }

        System.out.println("\n=== Physiotherapists by Attended Appointments ===");
        physiotherapists.stream()
                .sorted(Comparator.comparingInt(this::countAttendedAppointments).reversed())
                .forEach(p -> System.out.println(p.getName() + ": " + countAttendedAppointments(p)));
    }
    // Helper Methods
    Patient findPatientById(int id) {
        return patients.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
    }

    private Appointment findAppointmentById(int id) {
        return physiotherapists.stream()
                .flatMap(p -> p.getTimetable().stream())
                .filter(a -> a.getId() == id)
                .findFirst()
                .orElse(null);
    }

    private Booking findBookingById(int id) {
        return bookings.stream().filter(b -> b.getId() == id).findFirst().orElse(null);
    }

    private boolean hasTimeConflict(Patient patient, LocalDateTime time, int... excludeBookingId) {
        return bookings.stream()
                .anyMatch(b -> b.getPatient().getId() == patient.getId() &&
                        b.getStatus().equals("booked") &&
                        b.getAppointment().getStartTime().equals(time) &&
                        (excludeBookingId.length == 0 || b.getId() != excludeBookingId[0]));
    }

    private int countAttendedAppointments(Physiotherapist p) {
        return (int) bookings.stream()
                .filter(b -> b.getAppointment().getPhysiotherapist().equals(p) && b.getStatus().equals("attended"))
                .count();
    }

    public List<Physiotherapist> getPhysiotherapistsByExpertise(String expertiseName) {
        return physiotherapists.stream()
                .filter(p -> p.getExpertiseAreas().stream().anyMatch(e -> e.getName().equals(expertiseName)))
                .toList();
    }

    public Physiotherapist findPhysiotherapistByName(String name) {
        // Normalize input: remove possessive 's, extra spaces, and make lowercase
        String normalizedInput = name.replaceAll("'s\\b", "").trim().toLowerCase();
        return physiotherapists.stream()
                .filter(p -> p.getName().toLowerCase().replace("dr. ", "").contains(normalizedInput))
                .findFirst()
                .orElse(null);
    }

    public List<Appointment> getAvailableAppointments(Physiotherapist p) {
        return p.getTimetable().stream()
                .filter(Appointment::isAvailable)
                .toList();
    }

    public List<Patient> getPatients() {
        return patients;
    }

    public List<Booking> getBookings() {
        return bookings;
    }
}