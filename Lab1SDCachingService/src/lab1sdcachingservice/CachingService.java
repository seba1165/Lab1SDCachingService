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
        File archivo = new File("Config.txt");
        FileReader fr = new FileReader(archivo);
        BufferedReader br = new BufferedReader(fr);
        
        String linea1 = br.readLine();
        String linea2 = br.readLine();
        
        
        
        String [] cacheCant = linea1.split(" ");
        String [] cachePart = linea2.split(" ");
        
        //Hacer validación
        int size = Integer.parseInt(cacheCant[1]);
        int part = Integer.parseInt(cachePart[1]);
        
        MemCache MemCompartida = new MemCache(3,2);
        MemCompartida.cache.put("query1", "answer1");
        MemCompartida.cache.put("query2", "answer2");
        MemCompartida.cache.put("query3", "answer3");
        
        System.out.println("");
        
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
                ((HiloCachingService) new HiloCachingService(connectionSocket, idSession, MemCompartida)).start();
                idSession++;
                MemCompartida.print();
            }
        } catch (IOException ex) {
            Logger.getLogger(CachingService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
