/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab1sdcachingservice;

/**
 *
 * @author Seba
 */
import java.io.*;
import java.net.*;
import java.util.logging.*;
public class CachingService {
    public static void main(String args[]) throws IOException {
        ServerSocket acceptSocket;
        System.out.println("Inicializando CachingService... ");
           
        try {
            //Socket para el servidor en el puerto 5000
            acceptSocket = new ServerSocket(5000);
            System.out.print("Server is running...");
            System.out.println("\t[OK]\n");
            int idSession = 0;
            while (true) {
                Socket connectionSocket;
                 //Socket listo para recibir
                connectionSocket = acceptSocket.accept();
                System.out.println("Nueva conexión entrante: "+connectionSocket);
                ((HiloCachingService) new HiloCachingService(connectionSocket, idSession)).start();
                idSession++;
            }
        } catch (IOException ex) {
            Logger.getLogger(CachingService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
