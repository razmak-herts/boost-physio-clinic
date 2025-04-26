package hertfordshire.razmak;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Clinic clinic = new Clinic();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            displayMenu();
            int option = getIntInput(scanner, "Select option: ");
            switch (option) {
                case 1: // Add Patient
                    System.out.print("Enter name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter address: ");
                    String address = scanner.nextLine();
                    System.out.print("Enter telephone: ");
                    String telephone = scanner.nextLine();
                    clinic.addPatient(name, address, telephone);
                    break;

                case 2: // Remove Patient
                    listPatients(clinic);
                    int patientId = getIntInput(scanner, "Enter patient ID to remove: ");
                    clinic.removePatient(patientId);
                    break;

                case 3: // Book Appointment
                    listPatients(clinic);
                    patientId = getIntInput(scanner, "Enter patient ID: ");
                    Patient patient = clinic.findPatientById(patientId);
                    if (patient == null) {
                        System.out.println("Patient not found.");
                        break;
                    }
                    System.out.println("1. By expertise\n2. By physiotherapist name");
                    int method = getIntInput(scanner, "Select lookup method: ");
                    if (method == 1) {
                        System.out.print("Enter expertise (e.g., Physiotherapy, Rehabilitation, Osteopathy): ");
                        String expertise = scanner.nextLine();
                        List<Physiotherapist> physios = clinic.getPhysiotherapistsByExpertise(expertise);
                        if (physios.isEmpty()) {
                            System.out.println("No physiotherapists found.");
                            break;
                        }
                        bookFromPhysiotherapists(scanner, clinic, patientId, physios);
                    } else if (method == 2) {
                        System.out.print("Enter physiotherapist name: ");
                        String physioName = scanner.nextLine().trim();
                        Physiotherapist physio = clinic.findPhysiotherapistByName(physioName);
                        if (physio == null) {
                            System.out.println("Physiotherapist not found.");
                            break;
                        }
                        bookFromPhysiotherapists(scanner, clinic, patientId, List.of(physio));
                    }
                    break;

                case 4: // Change Booking
                    listBookings(clinic);
                    int bookingId = getIntInput(scanner, "Enter booking ID to change: ");
                    System.out.println("Select new appointment:");
                    method = getIntInput(scanner, "1. By expertise\n2. By physiotherapist name: ");
                    if (method == 1) {
                        System.out.print("Enter expertise: ");
                        String expertise = scanner.nextLine();
                        List<Physiotherapist> physios = clinic.getPhysiotherapistsByExpertise(expertise);
                        if (physios.isEmpty()) {
                            System.out.println("No physiotherapists found.");
                            break;
                        }
                        changeBookingFromPhysiotherapists(scanner, clinic, bookingId, physios);
                    } else if (method == 2) {
                        System.out.print("Enter physiotherapist name: ");
                        String physioName = scanner.nextLine();
                        Physiotherapist physio = clinic.findPhysiotherapistByName(physioName);
                        if (physio == null) {
                            System.out.println("Physiotherapist not found.");
                            break;
                        }
                        changeBookingFromPhysiotherapists(scanner, clinic, bookingId, List.of(physio));
                    }
                    break;

                case 5: // Cancel Booking
                    listBookings(clinic);
                    bookingId = getIntInput(scanner, "Enter booking ID to cancel: ");
                    if (clinic.cancelBooking(bookingId)) {
                        System.out.println("Booking cancelled.");
                    } else {
                        System.out.println("Cancellation failed.");
                    }
                    break;

                case 6: // Attend Booking
                    listBookings(clinic);
                    bookingId = getIntInput(scanner, "Enter booking ID to attend: ");
                    if (clinic.attendBooking(bookingId)) {
                        System.out.println("Booking marked as attended.");
                    } else {
                        System.out.println("Failed to mark as attended.");
                    }
                    break;

                case 7: // Print Report
                    clinic.printReport();
                    break;

                case 8: // Exit
                    System.out.println("******** GoodBye ***********");
                    System.out.println("Exiting...");
                    scanner.close();
                    System.exit(0);

                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private static void displayMenu() {
        System.out.println("\n=== Boost Physio Clinic Booking System ===");
        System.out.println("1. Add patient");
        System.out.println("2. Remove patient");
        System.out.println("3. Book appointment");
        System.out.println("4. Change booking");
        System.out.println("5. Cancel booking");
        System.out.println("6. Attend booking");
        System.out.println("7. Print report");
        System.out.println("8. Exit");
    }

    private static int getIntInput(Scanner scanner, String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }

    private static void listPatients(Clinic clinic) {
        System.out.println("\nPatients:");
        clinic.getPatients().forEach(p -> System.out.println(p.getId() + ": " + p.getName()));
    }

    private static void listBookings(Clinic clinic) {
        System.out.println("\nBookings:");
        clinic.getBookings().forEach(b -> System.out.printf("ID: %d, Patient: %s, Treatment: %s, Time: %s, Status: %s%n",
                b.getId(), b.getPatient().getName(), b.getAppointment().getTreatmentType().getName(),
                b.getAppointment().getStartTime(), b.getStatus()));
    }

    private static void bookFromPhysiotherapists(Scanner scanner, Clinic clinic, int patientId, List<Physiotherapist> physios) {
        for (Physiotherapist p : physios) {
            List<Appointment> available = clinic.getAvailableAppointments(p);
            if (!available.isEmpty()) {
                System.out.println("\n" + p.getName() + "'s Available Appointments:");
                available.forEach(a -> System.out.printf("ID: %d, Treatment: %s, Time: %s%n",
                        a.getId(), a.getTreatmentType().getName(), a.getStartTime()));
            }
        }
        int appointmentId = getIntInput(scanner, "Enter appointment ID to book: ");
        if (clinic.bookAppointment(patientId, appointmentId)) {
            System.out.println("Booking successful.");
        } else {
            System.out.println("Booking failed (unavailable or time conflict).");
        }
    }

    private static void changeBookingFromPhysiotherapists(Scanner scanner, Clinic clinic, int bookingId, List<Physiotherapist> physios) {
        for (Physiotherapist p : physios) {
            List<Appointment> available = clinic.getAvailableAppointments(p);
            if (!available.isEmpty()) {
                System.out.println("\n" + p.getName() + "'s Available Appointments:");
                available.forEach(a -> System.out.printf("ID: %d, Treatment: %s, Time: %s%n",
                        a.getId(), a.getTreatmentType().getName(), a.getStartTime()));
            }
        }
        int newAppointmentId = getIntInput(scanner, "Enter new appointment ID: ");
        if (clinic.changeBooking(bookingId, newAppointmentId)) {
            System.out.println("Booking changed successfully.");
        } else {
            System.out.println("Change failed (unavailable or time conflict).");
        }
    }
}