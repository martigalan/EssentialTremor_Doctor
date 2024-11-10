package pojos;

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
     * State assiganed to the patient by the doctor
     */
    private State state;
    /**
     * Treatment the patient should undergo
     */
    private Treatment treatment;


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
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDoctorSurname() {
        return doctorSurname;
    }

    public void setDoctorSurname(String doctorSurname) {
        this.doctorSurname = doctorSurname;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Treatment getTreatment() {
        return treatment;
    }

    public void setTreatment(Treatment treatment) {
        this.treatment = treatment;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
