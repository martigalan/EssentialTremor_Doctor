package pojos;

import java.time.LocalDate;
import java.util.Date;

public class DoctorsNote {

    /**
     * Name of the doctor that redacts the note
     */
    private String doctorName;
    /**
     * Surname of the doctor that redacts the note
     */
    private String doctorSurname;
    /**
     * String containing the annotations made about a medical record
     */
    private String notes;
    /**
     * State assigned to the patient by the doctor
     */
    private State state;
    /**
     * Treatment the patient should undergo
     */
    private Treatment treatment;
    /**
     * Date of creation
     */
    private LocalDate date;
    /**
     * Medical record id thats is associated to
     */
    private Integer mr_id;


    /**
     * Constructor
     * @param notes annotations about a medical record
     * @param state state assigned to the patient
     * @param treatment treatment assigned to the patient
     */
    public DoctorsNote(String notes, State state, Treatment treatment) {
        this.notes = notes;
        this.state = state;
        this.treatment = treatment;
        this.date = LocalDate.now();
    }

    /**
     * Constructor
     * @param doctorName doctors name
     * @param doctorSurname doctors surname
     * @param notes annotations about a medical record
     * @param state state assigned to the patient
     * @param treatment treatment assigned to the patient
     */
    public DoctorsNote(String doctorName, String doctorSurname, String notes, State state, Treatment treatment) {
        this.doctorName = doctorName;
        this.doctorSurname = doctorSurname;
        this.notes = notes;
        this.state = state;
        this.treatment = treatment;
        this.date = LocalDate.now();
    }

    public LocalDate getDate() {
        return date;
    }

    public Integer getMr_id() {
        return mr_id;
    }

    public void setMr_id(Integer mr_id) {
        this.mr_id = mr_id;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public void setDoctorSurname(String doctorSurname) {
        this.doctorSurname = doctorSurname;
    }

    public State getState() {
        return state;
    }

    public Treatment getTreatment() {
        return treatment;
    }

    public String getNotes() {
        return notes;
    }

    @Override
    public String toString() {
        return "Doctors name: " + doctorName +
                "\nDoctors surname: " + doctorSurname  +
                "\nNotes: " + notes +
                "\nState: " + state +
                "\nTreatment: " + treatment +
                "\nDate: " + date;
    }
}
