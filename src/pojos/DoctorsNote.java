package pojos;

public class DoctorsNote {

    private String doctorName;
    private String doctorSurname;
    private String notes;
    private int medicalRecordId;
    private int doctorId;

    public DoctorsNote(String notes) {
        this.notes = notes;
    }

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

    public int getMedicalRecordId() {
        return medicalRecordId;
    }

    public void setMedicalRecordId(int medicalRecordId) {
        this.medicalRecordId = medicalRecordId;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }
}
