package org.example;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner;
        scanner = new Scanner(System.in);

        try (Socket clientSocket = new Socket("localhost", 8000)) {
            System.out.println("Connected to server...");

            DataInputStream inputFromServer = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream outputToServer = new DataOutputStream(clientSocket.getOutputStream());
            String serverMessage, userInputString;

            serverMessage = inputFromServer.readUTF();
            System.out.println(serverMessage);


            do {
                serverMessage = inputFromServer.readUTF();
                System.out.println(serverMessage);

                System.out.print("\nEnter: ");
                userInputString = scanner.next();
                outputToServer.writeUTF(userInputString);
            } while (!userInputString.equals("1") && !userInputString.equals("2"));

            if (userInputString.equals("1")) { //login
                System.out.print("\nName: ");

                String user = scanner.next();
                outputToServer.writeUTF(user);

                System.out.print("Password: ");

                String password = scanner.next();
                outputToServer.writeUTF(password);

                String userType = inputFromServer.readUTF();
                System.out.println("\nWelcome " + userType + " " + user);

                switch (userType) {
                    case "Admin" -> {
                        try {
                            new AdminControl(inputFromServer, outputToServer);
                        }
                        catch (Exception e){
                            System.out.println("Error");
                        }
                    }
                    case "Teacher" -> {
                        try {
                            new TeacherControl(inputFromServer, outputToServer, clientSocket);
                        }
                        catch (Exception e){
                            System.out.println("Error");
                        }
                    }
                    case "Student" -> {
                        try {
                            new StudentControl(inputFromServer, outputToServer);
                        }
                        catch (Exception e){
                            System.out.println("Error");
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
