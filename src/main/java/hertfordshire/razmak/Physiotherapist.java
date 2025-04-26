package hertfordshire.razmak;

import java.util.ArrayList;
import java.util.List;

public class Physiotherapist {
    private int id;
    private String name;
    private String address;
    private String telephone;
    private List<ExpertiseArea> expertiseAreas;
    private List<Appointment> timetable;

    public Physiotherapist(int id, String name, String address, String telephone, List<ExpertiseArea> expertiseAreas) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.telephone = telephone;
        this.expertiseAreas = new ArrayList<>(expertiseAreas);
        this.timetable = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getTelephone() {
        return telephone;
    }

    public List<ExpertiseArea> getExpertiseAreas() {
        return expertiseAreas;
    }

    public List<Appointment> getTimetable() {
        return timetable;
    }

    public void addAppointment(Appointment appointment) {
        timetable.add(appointment);
    }
}