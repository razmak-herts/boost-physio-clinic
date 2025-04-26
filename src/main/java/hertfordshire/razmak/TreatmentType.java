package hertfordshire.razmak;

public class TreatmentType {
    private String name;
    private ExpertiseArea expertiseArea;

    public TreatmentType(String name, ExpertiseArea expertiseArea) {
        this.name = name;
        this.expertiseArea = expertiseArea;
    }

    public String getName() {
        return name;
    }

    public ExpertiseArea getExpertiseArea() {
        return expertiseArea;
    }
}