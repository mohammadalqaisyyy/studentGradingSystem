package org.example;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class StudentControl {
    DataInputStream inputFromServer;
    DataOutputStream outputToServer;

    public StudentControl(DataInputStream inputFromServer, DataOutputStream outputToServer) throws IOException {
        this.inputFromServer = inputFromServer;
        this.outputToServer = outputToServer;
        System.out.println("\n" + inputFromServer.readUTF());
    }
}
