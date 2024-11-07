package pojos;

import data.ACC;
import data.EMG;
import jdbc.ConnectionManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;

public class Doctor {

    /**
     * User to store username and password for the application
     */
    private int userId;
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
     * Acces to the data base
     */
    private ConnectionManager access;

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

    public void setMedicalRecords(List<MedicalRecord> medicalRecords) {
        this.medicalRecords = medicalRecords;
    }

    public List<DoctorsNote> getDoctorsNote() {
        return doctorsNotes;
    }

    public void setDoctorsNote(List<DoctorsNote> doctorsNotes) {
        this.doctorsNotes = doctorsNotes;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Doctor doctor = (Doctor) o;
        return Objects.equals(name, doctor.name) && Objects.equals(surname, doctor.surname) && Objects.equals(patients, doctor.patients) && Objects.equals(access, doctor.access);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, surname, patients, access);
    }

    public void setPatients(List<Patient> patients) {
        this.patients = patients;
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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public ConnectionManager getAccess() {
        return access;
    }

    /**
     * Chooses a patient from the doctors patients list
     * @return Patient chosen by the doctor
     */
    private Patient choosePatient() {
        Scanner sc = new Scanner(System.in);
        List<Patient> listOfPatients = getPatients();
        for (int i = 0; i < listOfPatients.size(); i++) {
            System.out.println((i + 1) + ": " + listOfPatients.get(i).getName() + " " + listOfPatients.get(i).getSurname());
        }
        System.out.println("--- Please choose the patient by number: ");

        int number = sc.nextInt();
        sc.close();
        return listOfPatients.get(number - 1);
    }

    /**
     * Receive medicl record sent by the patient
     * @param socket connection with the client
     * @param bufferedReader used to send data
     * @return Medical Record sent by the patient
     * @throws IOException in case connection fails
     */
    public MedicalRecord receiveMedicalRecord(Socket socket, BufferedReader bufferedReader) throws IOException {
        MedicalRecord medicalRecord = null;
        System.out.println("Client connected. Receiving data...");

        // Read each line
        String patientName = bufferedReader.readLine();
        String patientSurname = bufferedReader.readLine();
        int age = Integer.parseInt(bufferedReader.readLine());
        double weight = Double.parseDouble(bufferedReader.readLine());
        int height = Integer.parseInt(bufferedReader.readLine());
        // Symptoms
        String symptoms = bufferedReader.readLine();
        List<String> listSymptoms = splitToStringList(symptoms);
        // time, acc and emg
        String time = bufferedReader.readLine();
        List<Integer> listTime = splitToIntegerList(time);
        String acc = bufferedReader.readLine();
        List<Integer> listAcc = splitToIntegerList(acc);
        String emg = bufferedReader.readLine();
        List<Integer> listEmg = splitToIntegerList(emg);
        // genBack
        boolean geneticBackground = Boolean.parseBoolean(bufferedReader.readLine());

        ACC acc1 = new ACC(listAcc, listTime);
        EMG emg1 = new EMG(listEmg, listTime);
        medicalRecord = new MedicalRecord(patientName, patientSurname, age, weight, height, listSymptoms, acc1, emg1, geneticBackground);
        this.getMedicalRecords().add(medicalRecord);
        medicalRecord.getDoctors().add(this);
        return medicalRecord;
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
     * @param medicalRecord Medical Record sent by the patient
     * @return Doctors Note with annotations about the Medical Record
     */
    public DoctorsNote createDoctorsNote(MedicalRecord medicalRecord) {
        //create a note for the medical record
        Scanner sc = new Scanner(System.in);
        System.out.println("\n Write any comments about the medical record (No enters): ");
        String comments = sc.nextLine();
        DoctorsNote doctorsNote = new DoctorsNote(comments);
        sc.close();
        medicalRecord.getDoctorsNotes().add(doctorsNote);
        this.getDoctorsNote().add(doctorsNote);
        return doctorsNote;
    }

    /**
     * Shows a Doctors Note from the doctors list
     */
    public void showDoctorNotes() {
        Scanner sc = new Scanner(System.in);
        List<DoctorsNote> notes = this.getDoctorsNote();

        if (notes.isEmpty()) {
            System.out.println("No doctor notes available.");
            return;
        }

        // List of all the notes
        System.out.println("Doctor Notes:");
        for (int i = 0; i < notes.size(); i++) {
            System.out.println((i + 1) + ": " + notes.get(i).getNotes());
        }
        sc.close();
    }

    /**
     * Edits Doctors Note
     */
    public void editDoctorNote() {
        Scanner sc = new Scanner(System.in);
        List<DoctorsNote> notes = this.getDoctorsNote();

        if (notes.isEmpty()) {
            System.out.println("No doctor notes available to edit.");
            return;
        }

        showDoctorNotes(); // Show all notes

        int choice = -1;
        while (true) {
            System.out.println("---> Choose a note to edit by number: ");
            choice = sc.nextInt();
            sc.nextLine(); // Consume newline

            if (choice >= 1 && choice <= notes.size()) {
                break; // exit of the loop
            } else {
                System.out.println("Invalid choice. Please enter a valid note number.");
            }
        }

        DoctorsNote selectedNote = notes.get(choice - 1);

        System.out.println("Current Comment: " + selectedNote.getNotes());
        System.out.println("Enter new comment:");
        String newComment = sc.nextLine();
        selectedNote.setNotes(newComment);

        System.out.println("Note updated successfully.");
        sc.close();
    }

    /**
     * Send Doctors Note to patient
     * @param doctorsNote Doctors Note based on the received record
     * @param socket connection with the client
     * @param printWriter used to send data
     * @throws IOException
     */
    public void sendDoctorsNote(DoctorsNote doctorsNote, Socket socket,PrintWriter printWriter) throws IOException {
        System.out.println("Sending text");
        printWriter.println(getName());
        printWriter.println(getSurname());
        printWriter.println(doctorsNote.getNotes());
    }

    private void addPatient() {
        Scanner sc = new Scanner(System.in);
        System.out.println("- Name: ");
        String name = sc.nextLine();
        System.out.println("- Surname: ");
        String surname = sc.nextLine();
        System.out.println("- Genetic background: (y/n)");
        String genBackCheck = sc.nextLine();
        Boolean genBack = null;
        //check
        Boolean valid = false;
        while (!valid) {
            if (genBackCheck.equals("y")) {
                valid = true;
                genBack = true;
            } else if (genBackCheck.equals("n")) {
                valid = true;
                genBack = false;
            } else {
                System.out.println("---NOT A VALID INPUT, PLEASE TRY AGAIN...");
            }
        }
        Patient patient = new Patient(name, surname, genBack);
        patient.getDoctors().add(this);
        this.getPatients().add(patient);
        sc.close();
    }
}
