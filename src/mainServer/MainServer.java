package mainServer;

import jdbc.*;
import pojos.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainServer {

    public static ConnectionManager connectionManager;
    public static JDBCUserManager userManager;
    public static JDBCDoctorManager doctorManager;
    public static JDBCDoctorNotesManager doctorNotesManager;
    public static JDBCMedicalRecordManager medicalRecordManager;
    public static JDBCPatientManager patientManager;
    public static JDBCStateManager stateManager;
    public static JDBCTreatmentManager treatmentManager;
    private static boolean control;
    private static Scanner sc = new Scanner(System.in);
    private static Doctor doctor;
    private static ServerSocket serverSocket;
    private static Socket clientSocket;
    private static PrintWriter printWriter;
    private static BufferedReader bufferedReader;

    public static void main(String[] args) {
        serverSocket = null;
        printWriter = null;
        bufferedReader = null;
        boolean conexion = true;

        try {
            connectionManager = new ConnectionManager();
            userManager = new JDBCUserManager(connectionManager);
            doctorManager = new JDBCDoctorManager(connectionManager);
            doctorNotesManager = new JDBCDoctorNotesManager(connectionManager);
            medicalRecordManager = new JDBCMedicalRecordManager(connectionManager);
            patientManager = new JDBCPatientManager(connectionManager);
            stateManager = new JDBCStateManager(connectionManager);
            treatmentManager = new JDBCTreatmentManager(connectionManager);

            //Create socket
            serverSocket = new ServerSocket(9000);

            try {
                while (conexion) {
                    System.out.println("Waiting for clients...");
                    clientSocket = serverSocket.accept();
                    System.out.println("Client connected.");
                    new Thread(new Patient()).start();
                    printWriter = new PrintWriter(MainServer.clientSocket.getOutputStream(), true);
                    bufferedReader = new BufferedReader(new InputStreamReader(MainServer.clientSocket.getInputStream()));

                    // Tables for state and treatment are created MAINTAIN?
                    stateManager.addState();
                    treatmentManager.addTreatment();

                    int option;
                    try {
                        control = true;
                        while (control) {
                            printLoginMenu();

                            try {
                                option = sc.nextInt();
                            } catch (InputMismatchException e) {
                                System.out.println("Invalid input. Please enter a number.");
                                sc.next(); // Clear the invalid input
                                continue; // Restart the loop
                            }
                            switch (option) {
                                case 1:
                                    register();
                                    break;
                                case 2:
                                    login();
                                    break;
                                case 0:
                                    conexion = false;
                                    control = false;
                                    break;
                                default:
                                    System.out.println("  NOT AN OPTION \n");
                                    break;
                            }
                        }


                    } catch (NumberFormatException e) {
                        System.out.println("  NOT A NUMBER. Closing application... \n");
                        sc.close();
                    } catch (SQLException ex) {
                        Logger.getLogger(MainServer.class.getName()).log(Level.SEVERE, null, ex);
                    }


                }
            } catch (IOException ex) {
                System.out.println("Error with the connection");
                ex.printStackTrace();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            releaseResourcesServer(serverSocket);
            sc.close();
        }
    }

    private static void releaseResourcesServer(ServerSocket serverSocket) {
        try {
            serverSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(MainServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void register() throws SQLException {
        try {
            Scanner sc = new Scanner(System.in);
            User u = new User();
            System.out.println("Let's proceed with the registration:");

            String username, password, role;
            System.out.print("Username: ");
            username = sc.nextLine();
            u.setUsername(username);

            System.out.print("Password: ");
            password = sc.nextLine();
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());
            byte[] hash = md.digest();
            u.setPassword(hash);

            System.out.print("Role (doctor/patient): ");
            role = sc.nextLine().toLowerCase();
            if (!role.equals("doctor") && !role.equals("patient")) {
                throw new IllegalArgumentException("Invalid role. Please choose either 'doctor' or 'patient'.");
            }
            u.setRole(role);

            userManager.addUser(u);
            int userId = userManager.getId(username);

            if (role.equals("doctor")) {
                registerDoctor(userId);
            } else {
                registerPatient(userId);
            }
        } catch (NoSuchAlgorithmException | IllegalArgumentException | IOException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private static void registerDoctor(int userId) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Registering Doctor:");
        System.out.print("Name: ");
        String name = sc.nextLine();
        System.out.print("Surname: ");
        String surname = sc.nextLine();

        Doctor doctor = new Doctor(name, surname);
        doctorManager.addDoctor(doctor, userId);
        System.out.println("Doctor registered successfully.");
        sc.close();
    }

    private static void registerPatient(int userId) throws IOException {
        System.out.println("Waiting for information of patient...");
        bufferedReader = new BufferedReader(new InputStreamReader(MainServer.clientSocket.getInputStream()));
        String patientData = bufferedReader.readLine();
        Patient patient = processPatientData(patientData);

        if (patient != null) {
            patientManager.addPatient(patient, userId);
            System.out.println("Patient registered successfully.");
        } else {
            System.out.println("Failed to register patient due to invalid data.");
        }
    }

    public static void login() throws IOException, SQLException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Are you a doctor or a patient?");
        String role = sc.nextLine();
        if (role.equals("doctor")) {
            System.out.print("Username: ");
            String usernameDoctor = sc.nextLine();
            System.out.print("Password: ");
            String passwordDoctor = sc.nextLine();
            if (userManager.verifyUsername(usernameDoctor) && userManager.verifyPassword(usernameDoctor, passwordDoctor)) {
                printWriter.println("LOGIN_SUCCESS");
                User u = userManager.getUser(userManager.getId(usernameDoctor));
                menuUser(u);
            } else {
                printWriter.println("LOGIN_FAILED");
            }
        } else if (role.equals("patient")) {
            String loginData = bufferedReader.readLine();
            String[] data = loginData.split("\\|");
            String usernamePatient = data[0];
            String encryptedPassword = data[1];
            if (userManager.verifyUsername(usernamePatient) && userManager.verifyPassword(usernamePatient, encryptedPassword)) {
                printWriter.println("LOGIN_SUCCESS");
                int user_id = userManager.getId(usernamePatient);
                Patient patient = patientManager.getPatientByUserId(user_id);
                String patientInfo = patient.getName() + "|" + patient.getSurname() + "|" + patient.getGenetic_background();
                printWriter.println(patientInfo);
            } else {
                printWriter.println("LOGIN_FAILED");
            }
        }
    }

    public static void menuUser(User u) throws IOException, SQLException {
        int option;
        MedicalRecord mr = null;
        int userId = u.getId();
        System.out.println("Checking doctor for user ID: " + userId);
        Doctor doctor = doctorManager.getDoctorByUserId(userId);
        if (doctor == null) {
            System.out.println("Doctor not found for user ID: " + userId);
        } else {
            System.out.println("Doctor found: " + doctor.getName() + " " + doctor.getSurname());
        }

        while (true) {
            printMenuDoctor();
            try {
                option = sc.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                sc.next(); // Clear the invalid input
                continue; // Restart the loop
            }

            switch (option) {
                case 1: {
                    mr = doctor.receiveMedicalRecord(clientSocket, bufferedReader);
                    if (mr != null) {
                        medicalRecordManager.addMedicalRecord(mr);
                    }
                    break;
                }
                case 2: {
                    if (mr != null) {
                        doctor.showInfoMedicalRecord(mr);
                        //TODO option to create doctor note
                        DoctorsNote dn = chooseToDoDoctorNotes(mr);
                        mr.getDoctorsNotes().add(dn);
                        chooseToSendDoctorNotes(dn);
                    } else {
                        System.out.println("No medical record detected, please select option one");
                        break;
                    }
                }
            }
        }

    }

    public static void printMenuDoctor() {
        System.out.println("\n@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println("@@                                                                  @@");
        System.out.println("@@                 Welcome.                                         @@");
        System.out.println("@@                 1. Receive medical record                        @@");
        System.out.println("@@                 2. Show medical record                           @@");
        System.out.println("@@                 0. Exit                                          @@");
        System.out.println("@@                                                                  @@");
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.print("\nSelect an option: ");
    }

    public static void printLoginMenu(){
        System.out.println("\n@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println("@@                                                                  @@");
        System.out.println("@@                 Welcome.                                         @@");
        System.out.println("@@                 1. Register                                      @@");
        System.out.println("@@                 2. Login                                         @@");
        System.out.println("@@                 0. Exit                                          @@");
        System.out.println("@@                                                                  @@");
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.print("\nSelect an option: ");
    }

    public static DoctorsNote chooseToDoDoctorNotes(MedicalRecord mr) {
        System.out.println("\nDo you want to create a doctors note? (y/n)");
        String option = sc.nextLine();
        DoctorsNote dn = null;
        if (option.equalsIgnoreCase("y")) {
            dn = doctor.createDoctorsNote(mr);
            if (dn != null) {
                doctorNotesManager.addDoctorNote(dn); // Inserción en la base de datos
            }
        } else if (!option.equalsIgnoreCase("y") || option.equalsIgnoreCase("n")) {
            System.out.println("Not a valid option, try again...");
            chooseToDoDoctorNotes(mr);
        }
        return dn;
    }

    public static void chooseToSendDoctorNotes(DoctorsNote dn) throws IOException {
        System.out.println("\nDo you want to send a doctors note? (y/n)");
        String option = sc.nextLine();
        if (option.equalsIgnoreCase("y")) {
            doctor.sendDoctorsNote(dn, clientSocket, printWriter);
        } else if (!option.equalsIgnoreCase("y") || option.equalsIgnoreCase("n")) {
            System.out.println("Not a valid option, try again...");
            chooseToSendDoctorNotes(dn);
        }
    }

    public static Patient processPatientData(String patientData) {
        //Los datos del cliente llegan en formato: "name|surname|genetic_background|username|password"
        String[] data = patientData.split("\\|");
        if (data.length == 6) {
            String name = data[0];
            String surname = data[1];
            boolean geneticBackground = Boolean.parseBoolean(data[2]);
            String username = data[3];
            String encryptedPassword = data[4];
            String role = data[5];

            //de hexadecimal (String) a byte[]
            byte[] passwordBytes = hexStringToByteArray(encryptedPassword);
            User user = new User(username, passwordBytes, role);
            userManager.addUser(user);
            Patient patient = new Patient(name, surname, geneticBackground);
            System.out.println("Patient added successfully: " + patient.getName() + " " + patient.getSurname());
            return patient;
        } else {
            System.out.println("Error: incorrect patient data format.");
            return null;
        }
    }

    public static byte[] hexStringToByteArray(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i+1), 16));
        }
        return data;
    }

}
