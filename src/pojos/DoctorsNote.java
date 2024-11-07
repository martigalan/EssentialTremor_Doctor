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
     * Constructor
     * @param notes annotations about a medical record
     */
    public DoctorsNote(String notes) {
        this.notes = notes;
    }

    /**
     * Constructor
     * @param doctorName doctors name
     * @param doctorSurname doctors surname
     * @param notes annotations about a medical record
     */
    public DoctorsNote(String doctorName, String doctorSurname, String notes) {
        this.doctorName = doctorName;
        this.doctorSurname = doctorSurname;
        this.notes = notes;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
