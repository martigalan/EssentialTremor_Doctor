package pojos;

import data.ACC;
import data.EMG;

import java.util.ArrayList;
import java.util.List;

public class MedicalRecord {

    /**
     * Name of the patient that creates the record
     */
    private String patientName;
    /**
     * Surname of the patient that creates the record
     */
    private String patientSurname;
    /**
     * Age of the patient that creates the record
     */
    private int age;
    /**
     * Weight of the patient that creates the record
     */
    private double weight;
    /**
     * Height of the patient that creates the record
     */
    private int height;
    /**
     * Symptoms of the patient that creates the record, in the moment of creation
     */
    private List<String> symptoms;
    /**
     * Acceleration data
     */
    private ACC acceleration;
    /**
     * EMG data
     */
    private EMG emg;
    /**
     * Boolean to identify if the patient has a genetic predisposition of essential tremor
     * TRUE if there is, FALSE if not
     */
    private Boolean genetic_background;
    /**
     * List of doctors notes associated to the medical record
     */
    private List<DoctorsNote> doctorsNotes;
    /**
     * List of doctors that receive this medical record
     */
    private List<Doctor> doctors;

    /**
     *
     */
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<DoctorsNote> getDoctorsNotes() {
        return doctorsNotes;
    }

    public Boolean getGenetic_background() {
        return genetic_background;
    }

    public void setGenetic_background(Boolean genetic_background) {
        this.genetic_background = genetic_background;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientSurname() {
        return patientSurname;
    }

    public void setPatientSurname(String patientSurname) {
        this.patientSurname = patientSurname;
    }

    public int getAge() {
        return age;
    }

    public double getWeight() {
        return weight;
    }

    public int getHeight() {
        return height;
    }

    public List<String> getSymptoms() {
        return symptoms;
    }

    public ACC getAcceleration() {
        return acceleration;
    }

    public EMG getEmg() {
        return emg;
    }

    /**
     * Constructor
     * @param age patients age
     * @param weight patients weight
     * @param height patient height
     * @param symptoms patients symptoms
     */
    public MedicalRecord(int age, double weight, int height, List<String> symptoms) {
        this.age = age;
        this.weight = weight;
        this.height = height;
        this.symptoms = symptoms;
        this.acceleration = new ACC();
        this.emg = new EMG();
        this.doctorsNotes = new ArrayList<>();
        this.doctors = new ArrayList<>();
    }

    /**
     * Constructor
     * @param patientName patients name
     * @param patientSurname patients surname
     * @param age patients age
     * @param weight patients weight
     * @param height patients height
     * @param symptoms patients symptoms
     * @param acceleration patients acceleration data
     * @param emg patients emg data
     * @param genetic_background patients genetic background info
     */
    public MedicalRecord(String patientName, String patientSurname, int age, double weight, int height, List<String> symptoms, ACC acceleration, EMG emg, Boolean genetic_background) {
        this.patientName = patientName;
        this.patientSurname = patientSurname;
        this.age = age;
        this.weight = weight;
        this.height = height;
        this.symptoms = symptoms;
        this.acceleration = acceleration;
        this.emg = emg;
        this.genetic_background = genetic_background;
        this.doctorsNotes = new ArrayList<>();
        this.doctors = new ArrayList<>();
    }

    /**
     * Medical Record string representation
     * @return string representation of medical record
     */
    @Override
    public String toString() {
        return "Patients name: " + patientName +
                "\nSurname: "+ patientSurname +
                "\nAge: " + age +
                "\nWeight: " + weight +
                "\nHeight: " + height +
                "\nSymptoms: " + symptoms +
                "\nGenetic background: " + genetic_background +
                "\nAcceleration data: " + acceleration +
                "\nEMG data: " + emg;
    }

    /**
     * Function that calls another one to represent the acceleration data
     */
    void showAcc() {
        this.acceleration.plotSignal();
    }

    /**
     * Function that calls another one to represent the emg data
     */
    void showEMG(){
        this.emg.plotSignal();
    }
}
