package pojos;

import java.util.*;
import java.util.stream.Collectors;

public class Doctor {

    /**
     * Doctors name
     */
    private String name;
    /**
     * Doctors surname
     */
    private String surname;
    /**
     * List of patients the doctor has
     */
    private List<Patient> patients;
    /**
     * List of medical records the doctor receives
     */
    private List<MedicalRecord> medicalRecords;
    /**
     * List of doctors notes the doctor redacts
     */
    private List<DoctorsNote> doctorsNotes;
    /**
     * User information
     */
    private User user;
    /**
     * Empty constructor
     */
    public Doctor() {

    }
    /**
     * Constructor
     * @param name doctors name
     * @param surname doctors surname
     * @param patients list of patient associated with the doctor
     */
    public Doctor(String name, String surname, List<Patient> patients) {
        this.name = name;
        this.surname = surname;
        this.patients = patients;
        this.doctorsNotes = new ArrayList<>();
        this.medicalRecords = new ArrayList<>();
    }

    /**
     * Constructor
     * @param name doctors name
     * @param surname doctors surname
     */
    public Doctor(String name, String surname) {
        this.name = name;
        this.surname = surname;
        this.patients = new ArrayList<>();
        this.medicalRecords = new ArrayList<>();
        this.doctorsNotes = new ArrayList<>();
    }

    /**
     * Patients String representation
     * @return String representation
     */
    @Override
    public String toString() {
        return "Doctor{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                '}';
    }

    public List<MedicalRecord> getMedicalRecords() {
        return medicalRecords;
    }

    public List<DoctorsNote> getDoctorsNote() {
        return doctorsNotes;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Doctor doctor = (Doctor) o;
        return Objects.equals(name, doctor.name) && Objects.equals(surname, doctor.surname) && Objects.equals(patients, doctor.patients);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, surname, patients);
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public List<Patient> getPatients() {
        return patients;
    }

    /**
     * Splits values of a String into a List of String
     * @param str String with values separated with commas
     * @return List of String values
     */
    public static List<String> splitToStringList(String str) {
        return Arrays.asList(str.split(","));
    }

    /**
     * Splits values of a String into a List of Integers
     * @param str String with Integer values separated with commas
     * @return List of Integer values
     */
    public static List<Integer> splitToIntegerList(String str) {
        return Arrays.stream(str.split(","))
                .filter(s -> s.matches("-?\\d+"))  // Solo permite números enteros (positivos o negativos)
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    /**
     * Displays the medical record sent by the patient
     * @param medicalRecord Medical Record sent by the patient
     */
    public void showInfoMedicalRecord(MedicalRecord medicalRecord) {
        System.out.println(medicalRecord);
        medicalRecord.showAcc();
        medicalRecord.showEMG();
    }

    /**
     * Creates a Doctors Note based on a Medical Record sent by the patient
     *
     * @param sc
     * @param medicalRecord Medical Record sent by the patient
     * @return Doctors Note with annotations about the Medical Record
     */
    public DoctorsNote createDoctorsNote(Scanner sc, MedicalRecord medicalRecord) {
        //create a note for the medical record
        System.out.println("\n Write any comments about the medical record (No enters): ");
        String comments = sc.nextLine();
        //loops to chose a state and a treatment
        State st = null;
        Treatment trt = null;
        while (st == null) {
            System.out.println("Choose a state for the patient:");
            for (State state : State.values()) {
                System.out.println(state.ordinal() + 1 + ": " + state + " - " + state.getDescription());
            }
            int stateChoice = sc.nextInt();
            if (stateChoice >= 1 && stateChoice <= State.values().length) {
                st = State.values()[stateChoice - 1];
            } else {
                System.out.println("Invalid choice. Please try again.");
            }
        }
        while (trt == null) {
            System.out.println("Choose a treatment for the patient:");
            for (Treatment treatment : Treatment.values()) {
                System.out.println(treatment.ordinal() + 1 + ": " + treatment + " - " + treatment.getDescription());
            }
            int treatmentChoice = sc.nextInt();
            if (treatmentChoice >= 1 && treatmentChoice <= Treatment.values().length) {
                trt = Treatment.values()[treatmentChoice - 1];
            } else {
                System.out.println("Invalid choice. Please try again.");
            }
        }

        DoctorsNote doctorsNote = new DoctorsNote(comments, st, trt);
        //set mr_id for later use
        doctorsNote.setMr_id(medicalRecord.getId());
        doctorsNote.setDoctorName(this.name);
        doctorsNote.setDoctorSurname(this.surname);
        medicalRecord.getDoctorsNotes().add(doctorsNote);
        this.getDoctorsNote().add(doctorsNote);
        return doctorsNote;
    }
}
