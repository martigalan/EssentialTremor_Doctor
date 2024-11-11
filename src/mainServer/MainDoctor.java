package mainServer;

import pojos.*;

import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainDoctor {
    private static Scanner sc = new Scanner(System.in);
    private static Socket socket;
    private static PrintWriter printWriter;
    private static BufferedReader bufferedReader;
    private static InputStream inputStream;
    private static Doctor doctor;
    private static boolean control;
    private static int option;

    public static void main(String[] args) {
        try {
            socket = new Socket("localhost", 9000);
            printWriter = new PrintWriter(socket.getOutputStream(), true);
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            inputStream = socket.getInputStream();


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
                            break;
                        default:
                            System.out.println("  NOT AN OPTION \n");
                            break;
                    }
                }
            } catch (NumberFormatException | NoSuchAlgorithmException e) {
                System.out.println("  NOT A NUMBER. Closing application... \n");
                sc.close();
            }
        } catch (IOException e) {
            System.out.println("Error connecting to the server.");
            e.printStackTrace();
        } finally {
            releaseResourcesDoctor(inputStream, socket);
            sc.close();
        }
    }

    private static void releaseResourcesDoctor(InputStream inputStream, Socket socket) {
        try {
            inputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(MainDoctor.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(MainDoctor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void registerDoctor() throws IOException, NoSuchAlgorithmException {
        System.out.println("Please enter doctor details:");
        String name = "", surname = "", username = "", password = "";

        while (true) {
            System.out.print("Name: ");
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

        String doctorData = name + "|" + surname + "|" + username + "|" + encryptedPassword + "|doctor";
        printWriter.println(doctorData);  //Send to server
        System.out.println("Doctor and user data sent to the server for registration.");

        String approval = bufferedReader.readLine();
        if (approval.equals("REGISTER_SUCCESS")) {
            System.out.println("Doctor registered correctly.");
            return;
        } else {
            System.out.println("Couldn't register doctor. Please try again");
            return;
        }
    }
    public static void login() throws IOException, NoSuchAlgorithmException {
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
            String roleResponse = bufferedReader.readLine();
            if (roleResponse.equals("WELCOME_DOCTOR")) {
                String doctorData = bufferedReader.readLine();
                String[] doctorInfo = doctorData.split("\\|");
                Doctor doctor = new Doctor(doctorInfo[0], doctorInfo[1]);
                System.out.println("Welcome, " + doctor.getName() + " " + doctor.getSurname());
                menuUser();
            }
        } else {
            System.out.println("Login failed. Please try again.");
            return;
        }
    }

    public static void menuUser() throws IOException {
        int option;
        MedicalRecord mr = null;

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
                    receiveMedicalRecord();
                }
                case 2: {
                    if (mr != null) {
                        doctor.showInfoMedicalRecord(mr);
                        //TODO option to create doctor note
                        DoctorsNote dn = chooseToDoDoctorNotes(mr);
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
        System.out.println("@@                 2. Open medical record                           @@");
        System.out.println("@@                 3. Send Doctors Note                             @@");
        System.out.println("@@                 0. Exit                                          @@");
        System.out.println("@@                                                                  @@");
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.print("\nSelect an option: ");
    }

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
     * Receive medical record sent by the doctor
     *
     * @return Medical Record sent by the doctor
     * @throws IOException in case connection fails
     */
    private static void receiveMedicalRecord() throws IOException {
        MedicalRecord medicalRecord = null;
        String command = "MedicalRecord";
        printWriter.println(command);

        String response = bufferedReader.readLine();
        if (response.equals("SEND_MEDICALRECORD")) {
            String patientName = bufferedReader.readLine();
            String patientSurname = bufferedReader.readLine();
            int age = Integer.parseInt(bufferedReader.readLine());
            double weight = Double.parseDouble(bufferedReader.readLine());
            int height = Integer.parseInt(bufferedReader.readLine());
            String symptoms = bufferedReader.readLine();
            List<String> listSymptoms = splitToStringList(symptoms);

            String time = bufferedReader.readLine();
            List<Integer> listTime = splitToIntegerList(time);
            String acc = bufferedReader.readLine();
            List<Integer> listAcc = splitToIntegerList(acc);
            String emg = bufferedReader.readLine();
            List<Integer> listEmg = splitToIntegerList(emg);
            boolean geneticBackground = Boolean.parseBoolean(bufferedReader.readLine());

            ACC acc1 = new ACC(listAcc, listTime);
            EMG emg1 = new EMG(listEmg, listTime);
            medicalRecord = new MedicalRecord(patientName, patientSurname, age, weight, height, listSymptoms, acc1, emg1, geneticBackground);
            if (medicalRecord != null) {
                printWriter.println("MEDICALRECORD_SUCCESS");
                doctor.getMedicalRecords().add(medicalRecord);
            } else {
                printWriter.println("MEDICALRECORD_FAILED");
            }
        } else {
            System.out.println("Failed to receive the medical record.");
        }
    }


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

    public static void chooseToSendDoctorNotes(DoctorsNote dn) throws IOException {
        System.out.println("\nDo you want to send a doctors note? (y/n)");
        String option = sc.nextLine();
        if (option.equalsIgnoreCase("y")) {
            doctor.sendDoctorsNote(dn, socket, printWriter);
        } else if (!option.equalsIgnoreCase("y") || !option.equalsIgnoreCase("n")) {
            System.out.println("Not a valid option, try again...");
            chooseToSendDoctorNotes(dn);
        }
    }

    public static byte[] hexStringToByteArray(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }

}
