package pojos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Patient{
    /**
     * Patient name
     */
    private String name;
    /**
     * Patients surname
     */
    private String surname;
    /**
     * Boolean to identify if the patient has a genetic predisposition of essential tremor
     * TRUE if there is, FALSE if not
     */
    private Boolean genetic_background;
    /**
     * A list of all the medical records the patient has
     */
    private List<MedicalRecord> medicalRecords;
    /**
     * A list of the doctors that the patient has
     */
    private List<Doctor> doctors;

    /**
     * Constructor
     * @param name patients name
     * @param surname patients surname
     * @param genBack patient genetic background of essential tremor
     */
    public Patient(String name, String surname, Boolean genBack) {
        this.name = name;
        this.surname = surname;
        this.genetic_background = genBack;
        this.medicalRecords = new ArrayList<MedicalRecord>();
        this.doctors = new ArrayList<Doctor>();
    }

    /**
     * Patients String representation
     * @return String representation
     */
    @Override
    public String toString() {
        return "- Name: " + name + '\'' +
                "- Surname: " + surname + '\'';
    }
    
}
