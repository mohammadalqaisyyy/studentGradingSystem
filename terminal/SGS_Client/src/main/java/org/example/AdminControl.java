package org.example;

import java.io.*;
import java.util.Scanner;

public class AdminControl {
    DataInputStream inputFromServer;
    DataOutputStream outputToServer;
    Scanner scanner;

    public AdminControl(DataInputStream inputFromServer, DataOutputStream outputToServer) throws IOException {
        this.inputFromServer = inputFromServer;
        this.outputToServer = outputToServer;
        scanner = new Scanner(System.in);

        label:
        while (true) {
            System.out.println(inputFromServer.readUTF());
            System.out.print("Enter your choice: ");
            outputToServer.writeUTF(scanner.next());
            String choice = inputFromServer.readUTF();//Like handshaking
            switch (choice) {
                case "1" -> {
                    System.out.println("Enter info of user:");
                    System.out.print("userType:  (1)Admin  (2)Teacher  (3)Student   -->  ");
                    int userType = scanner.nextInt();
                    if (userType < 1 || userType > 3)
                        throw new IllegalAccessError("Out of available choose");
                    outputToServer.writeUTF(String.valueOf(userType));

                    System.out.print("Name: ");
                    String name = scanner.next();
                    outputToServer.writeUTF(name);

                    System.out.print("Email: ");
                    String email = scanner.next();
                    outputToServer.writeUTF(email);

                    System.out.print("Password: ");
                    String password = scanner.next();
                    outputToServer.writeUTF(password);

                    System.out.println("Done..");
                }
                case "2" -> {
                    System.out.println("Teachers: ");
                    System.out.println(inputFromServer.readUTF());
                }
                case "3" -> {
                    System.out.println("Students: ");
                    System.out.println(inputFromServer.readUTF());
                }
                case "4" -> {
                    System.out.println("Teachers: ");
                    System.out.println(inputFromServer.readUTF());

                    System.out.println("\nEnter course name: ");
                    String courseName = scanner.next();
                    outputToServer.writeUTF(courseName);

                    System.out.println("\nEnter teacher ID: ");
                    int teacherID = scanner.nextInt();
                    outputToServer.writeUTF(String.valueOf(teacherID));

                    System.out.println("\nDone..");
                }
                case "5" -> {
                    System.out.println("Courses: ");
                    System.out.println(inputFromServer.readUTF());
                }
                case "6" -> {
                    System.out.println("Students: ");
                    System.out.println(inputFromServer.readUTF());

                    System.out.println("Courses: ");
                    System.out.println(inputFromServer.readUTF());

                    System.out.println("Enrollment: ");
                    System.out.print("Student ID: ");
                    int studentID = scanner.nextInt();
                    outputToServer.writeUTF(String.valueOf(studentID));

                    System.out.print("Course ID: ");
                    int courseID = scanner.nextInt();
                    outputToServer.writeUTF(String.valueOf(courseID));

                    System.out.print("\nDone..");
                }
                case "7" -> {
                    System.out.println("\n\nLogout..\n");
                    break label;
                }
                default -> System.out.println("Not available choice!!");//
            }

        }
    }
}
