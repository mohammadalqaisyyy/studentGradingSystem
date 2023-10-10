package org.example;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class TeacherControl {
    DataInputStream inputFromServer;
    DataOutputStream outputToServer;
    ObjectOutputStream objectOutputStream;
    Scanner scanner;

    public TeacherControl(DataInputStream inputFromServer, DataOutputStream outputToServer, Socket clientSocket) throws IOException {
        this.inputFromServer = inputFromServer;
        this.outputToServer = outputToServer;
        objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
        scanner = new Scanner(System.in);

        label:
        while (true) {
            System.out.println(inputFromServer.readUTF());
            System.out.print("Enter your choice: ");
            outputToServer.writeUTF(scanner.next());
            String choice = inputFromServer.readUTF();//Like handshaking
            switch (choice) {
                case "1" -> System.out.println(inputFromServer.readUTF());
                case "2" -> {
                    System.out.println(inputFromServer.readUTF());
                }
                case "3" -> {
                    System.out.println(inputFromServer.readUTF());
                    System.out.print("Enter course ID: ");
                    String courseID = scanner.next();
                    outputToServer.writeUTF(courseID);
                    System.out.println(inputFromServer.readUTF());
                }
                case "4" -> {
                    System.out.println(inputFromServer.readUTF());//1
                    System.out.print("Choose course to add marks: ");
                    String courseID = scanner.next();
                    outputToServer.writeUTF(courseID);//2
                    int sizeOfClass = Integer.parseInt(inputFromServer.readUTF());//3
                    System.out.println(inputFromServer.readUTF());//4
                    ArrayList<Integer> marks = new ArrayList<>();
                    for (int i = 0; i < sizeOfClass; i++) {
                        System.out.print("    ");
                        marks.add(scanner.nextInt());
                    }
                    objectOutputStream.writeObject(marks);
                    objectOutputStream.flush();
                    System.out.println("Done..");
                }
                case "5" -> {
                    System.out.println(inputFromServer.readUTF());//1

                    System.out.print("Choose course to edit marks: ");
                    String courseID = scanner.next();
                    outputToServer.writeUTF(courseID);
                    System.out.println(inputFromServer.readUTF());
                    System.out.print("Student ID: ");
                    String studentID = scanner.next();
                    outputToServer.writeUTF(studentID);
                    System.out.print("New Grade: ");
                    String newGrade = scanner.next();
                    outputToServer.writeUTF(newGrade);
                    System.out.println("Done..");
                }
                case "6" ->{
                    System.out.println("\n\nLogout..\n");
                    break label;
                }
                default -> System.out.println("Not available choice!!");//
            }

        }


    }
}
