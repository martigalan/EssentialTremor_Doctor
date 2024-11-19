package mainServer;

import data.ACC;
import data.EMG;
import pojos.Doctor;
import pojos.DoctorsNote;
import pojos.MedicalRecord;

import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainDoctor {
    /**
     * Manages inputs from console
     */
    private static Scanner sc = new Scanner(System.in);
    /**
     * Socket for connection
     */
    private static Socket socket;
    /**
     * Sends data to server
     */
    private static PrintWriter printWriter;
    /**
     * Receives data from server
     */
    private static BufferedReader bufferedReader;
    /**
     * Doctor object
     */
    private static Doctor doctor;
    /**
     * Control variable for loops
     */
    private static boolean control;
    /**
     * Control variable for options the user chooses
     */
    private static int option;

    public static void main(String[] args) {
        try {
            socket = new Socket("localhost", 9000);
            printWriter = new PrintWriter(socket.getOutputStream(), true);
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //sending the role to start the DoctortHandler
            String role = "Doctor";
            printWriter.println(role);

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
                            registerDoctor();
                            break;
                        case 2:
                            login();
                            break;
                        case 0:
                            control = false;
                            //return "exit" to close communication
                            printWriter.println("exit");
                            break;
                        default:
                            System.out.println("  NOT AN OPTION \n");
                            break;
                    }
                }
            } catch (NumberFormatException | NoSuchAlgorithmException e) {
                System.out.println("  NOT A NUMBER. Closing application... \n");
            }
        } catch (IOException e) {
            System.out.println("Error connecting to the server.");
            e.printStackTrace();
        } finally {
            releaseResourcesDoctor(bufferedReader, printWriter, socket);
            sc.close();
        }
    }

    /**
     * Releases resources when finishing program.
     *
     * @param bf     BufferedReader for input.
     * @param pw     PrintWriter for output.
     * @param socket Socket for connection.
     */
    private static void releaseResourcesDoctor(BufferedReader bf, PrintWriter pw, Socket socket) {
        try {
            bf.close();
            pw.close();
        } catch (IOException ex) {
            Logger.getLogger(MainDoctor.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(MainDoctor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Registers doctor.
     * Sends data (name, surname, username and password) to the server to store in the database.
     *
     * @throws IOException              in case of Input/Output exception.
     * @throws NoSuchAlgorithmException in case of encryption error.
     */
    public static void registerDoctor() throws IOException, NoSuchAlgorithmException {
        System.out.println("Please enter doctor details:");
        String name = "", surname = "", username = "", password = "";

        while (true) {
            System.out.print("Name: ");
            sc.nextLine();
            name = sc.nextLine().trim();
            if (!name.isEmpty()) break;
            System.out.println("Invalid name. Please enter a valid name.");
        }

        while (true) {
            System.out.print("Surname: ");
            surname = sc.nextLine().trim();
            if (!surname.isEmpty()) break;
            System.out.println("Invalid surname. Please enter a valid surname.");
        }

        while (true) {
            System.out.print("Username: ");
            username = sc.nextLine().trim();
            if (!username.isEmpty()) break;
            System.out.println("Invalid username. Please enter a valid username.");
        }

        System.out.print("Password: ");
        password = sc.nextLine();
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(password.getBytes());
        byte[] hash = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        String encryptedPassword = sb.toString();

        String command = "register";
        printWriter.println(command);

        String doctorData = name + "|" + surname + "|" + username + "|" + encryptedPassword + "|doctor";
        printWriter.println(doctorData);  //Send to server
        System.out.println("Doctor and user data sent to the server for registration.");

        String approval = bufferedReader.readLine();
        System.out.println(approval);
        if (approval.equals("REGISTER_SUCCESS")) {
            System.out.println("Doctor registered correctly.");
        } else {
            System.out.println("Couldn't register doctor. Please try again");
        }
    }

    /**
     * Logins doctor.
     * The funcion sends input info (username and password) to the server to check in the database.
     * If the info is correct, the user can access a menu to continue with the program, if the info is not correct, the user will go back and have a chance to register or login again.
     *
     * @throws IOException              in case of Input/Output exception.
     * @throws NoSuchAlgorithmException in case of encryption error.
     */
    public static void login() throws IOException, NoSuchAlgorithmException {
        String command = "login";
        printWriter.println(command);

        Scanner sc = new Scanner(System.in);
        System.out.print("Username: ");
        String username = sc.nextLine();
        System.out.print("Password: ");
        String password = sc.nextLine();

        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(password.getBytes());
        byte[] hashedPassword = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : hashedPassword) {
            sb.append(String.format("%02x", b));
        }
        String encryptedPassword = sb.toString();

        String loginData = username + "|" + encryptedPassword;
        printWriter.println(loginData);
        System.out.println("User data sent to the server for login.");

        String response = bufferedReader.readLine(); //receive response from server
        if (response.equals("LOGIN_SUCCESS")) {
            String doctorData = bufferedReader.readLine();
            String[] doctorInfo = doctorData.split("\\|");
            doctor = new Doctor(doctorInfo[0], doctorInfo[1]);
            System.out.println("Welcome, " + doctor.getName() + " " + doctor.getSurname());
            menuUser();
        } else {
            System.out.println("Login failed. Please try again.");
        }
    }

    /**
     * Main user menu.
     */
    public static void menuUser() throws IOException {
        MedicalRecord mr = null;
        try {
            control = true;
            while (control) {
                printMenuDoctor();
                try {
                    if (sc.hasNextInt()) {
                        option = sc.nextInt();
                    } else {
                        option = 0;
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a number.");
                    sc.next();
                    continue;
                }
                switch (option) {
                    case 1: {
                        mr = receiveMedicalRecord();
                    }
                    case 2: {
                        if (mr != null) {
                            doctor.showInfoMedicalRecord(mr);
                            //option to create doctor note
                            DoctorsNote dn = chooseToDoDoctorNotes(mr);
                            chooseToSendDoctorNotes(dn);
                        } else {
                            System.out.println("No medical record detected, please select option one");
                            break;
                        }
                    }
                    case 0: {
                        control = false;
                        //return "exit" to close communication
                        printWriter.println("exit");
                        break;
                    }
                    default: {
                        System.out.println("  NOT AN OPTION \n");
                        break;
                    }
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("  NOT A NUMBER. Closing application... \n");
            sc.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Prints main menu.
     */
    public static void printMenuDoctor() {
        System.out.println("\n@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println("@@                                                                  @@");
        System.out.println("@@                 Welcome.                                         @@");
        System.out.println("@@                 1. Receive medical record                        @@");
        System.out.println("@@                 2. Open medical record                           @@");
        System.out.println("@@                 0. Exit                                          @@");
        System.out.println("@@                                                                  @@");
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.print("\nSelect an option: ");
    }

    /**
     * Prints Register/Login menu.
     */
    public static void printLoginMenu() {
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

    /**
     * Receive medical record sent by the patient.
     * The function lets the doctor choose a patient, and then a medical record from that patient.
     *
     * @return Medical Record sent by the doctor
     * @throws IOException in case connection fails
     */
    private static MedicalRecord receiveMedicalRecord() throws IOException {
        String command = "MedicalRecord";
        printWriter.println(command);

        //search doctor_id by name and surname
        String name = doctor.getName();
        String surname = doctor.getSurname();
        printWriter.println(name);
        printWriter.println(surname);

        //print all the available patients, doctor chooses one and then its associated to them
        String response;
        Integer numberOfPatients = Integer.parseInt(bufferedReader.readLine());
        Integer i = 0;
        while (i < numberOfPatients) {
            response = bufferedReader.readLine();
            System.out.println(response);
            i++;
        }
        //choose id of patient
        System.out.println("Please choose the patient ID: ");
        Integer p_id = sc.nextInt();
        printWriter.println(p_id);

        //checks to see if theres any MR for that patient
        String hasMR = bufferedReader.readLine();
        MedicalRecord medicalRecord = null;
        if (hasMR.equals("NOT_FOUND")) {
            System.out.println("This patient doesn't have any medical records.");
            return medicalRecord;
        }
        //get ids and dates of the medical records from the chosen patient
        Integer numberOfMR = Integer.parseInt(bufferedReader.readLine());
        i = 0;
        while (i < numberOfMR) {
            response = bufferedReader.readLine();
            System.out.println(response);
            i++;
        }

        //choose id of medical record
        System.out.println("Please choose the medical record ID: ");
        Integer mr_id = sc.nextInt();
        printWriter.println(mr_id);
        //obtain medical record
        //TODO aqui da error y se va a la excepción
        response = bufferedReader.readLine();
        if (response.equals("SEND_MEDICALRECORD")) {
            String patientName = bufferedReader.readLine();
            String patientSurname = bufferedReader.readLine();
            int age = Integer.parseInt(bufferedReader.readLine());
            double weight = Double.parseDouble(bufferedReader.readLine());
            int height = Integer.parseInt(bufferedReader.readLine());
            String symptoms = bufferedReader.readLine();
            List<String> listSymptoms = doctor.splitToStringList(symptoms);

            String time = bufferedReader.readLine();
            List<Integer> listTime = doctor.splitToIntegerList(time);
            String acc = bufferedReader.readLine();
            List<Integer> listAcc = doctor.splitToIntegerList(acc);
            String emg = bufferedReader.readLine();
            List<Integer> listEmg = doctor.splitToIntegerList(emg);
            boolean geneticBackground = Boolean.parseBoolean(bufferedReader.readLine());

            ACC acc1 = new ACC(listAcc, listTime);
            EMG emg1 = new EMG(listEmg, listTime);
            medicalRecord = new MedicalRecord(patientName, patientSurname, age, weight, height, listSymptoms, acc1, emg1, geneticBackground);
            //set mr id for later use
            medicalRecord.setId(mr_id);
            if (medicalRecord != null) {
                printWriter.println("MEDICALRECORD_SUCCESS");
                doctor.getMedicalRecords().add(medicalRecord);
                return medicalRecord;
            } else {
                printWriter.println("MEDICALRECORD_FAILED");
                return medicalRecord;
            }
        } else {
            System.out.println("Failed to receive the medical record.");
        }
        return medicalRecord;
    }

    /**
     * This function lets the doctor choose whether to create a doctors note about a medical record previously received.
     *
     * @param mr received medical record.
     * @return doctors note created over the medical record.
     */
    public static DoctorsNote chooseToDoDoctorNotes(MedicalRecord mr) {
        System.out.println("\nDo you want to create a doctors note? (y/n)");
        String option = sc.nextLine();
        DoctorsNote dn = null;
        if (option.equalsIgnoreCase("y")) {
            dn = doctor.createDoctorsNote(mr);
            if (dn != null) {
                doctor.getDoctorsNote().add(dn); // Inserción en la lista
            }
        } else if (!option.equalsIgnoreCase("y") || !option.equalsIgnoreCase("n")) {
            System.out.println("Not a valid option, try again...");
            chooseToDoDoctorNotes(mr);
        }
        return dn;
    }

    /**
     * This function lets the doctor choose whether to send the doctors note to the server for storage in the database.
     *
     * @param dn doctors note.
     * @throws IOException in case of Input/Output exception.
     */
    public static void chooseToSendDoctorNotes(DoctorsNote dn) throws IOException {
        System.out.println("\nDo you want to send a doctors note? (y/n)");
        String option = sc.nextLine();
        if (option.equalsIgnoreCase("y")) {
            //doctor.sendDoctorsNote(dn, printWriter);
            sendDoctorsNote(dn);
        } else if (!option.equalsIgnoreCase("y") || !option.equalsIgnoreCase("n")) {
            System.out.println("Not a valid option, try again...");
            chooseToSendDoctorNotes(dn);
        }
    }

    /**
     * Sends the doctors note to the server for it to be stored in the database.
     *
     * @param doctorsNote doctors note
     * @throws IOException
     */
    public static void sendDoctorsNote(DoctorsNote doctorsNote) throws IOException {
        System.out.println("Sending text");

        String comment = "DoctorsNote";
        printWriter.println(comment);

        printWriter.println(doctorsNote.getDoctorName());
        printWriter.println(doctorsNote.getDoctorSurname());
        printWriter.println(doctorsNote.getNotes());
        printWriter.println(doctorsNote.getState());
        printWriter.println(doctorsNote.getTreatment());
        String dateTxt = String.valueOf(doctorsNote.getDate());
        //format of dateTxt = "Wed Nov 13 13:44:33 CET 2024"
        printWriter.println(dateTxt);
        //send mr_id (medical records its associated to)
        printWriter.println(doctorsNote.getMr_id());

        String approval = bufferedReader.readLine();
        if (approval.equals("DOCTORNOTE_SUCCESS")) {
            System.out.println("Doctors Note sent correctly");
        } else {
            System.out.println("Couldn't send Doctors Note. Please try again.");
        }
    }

}
