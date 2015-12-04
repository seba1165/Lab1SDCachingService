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
        int canTpart = Integer.parseInt(cachePart[1]);
       
        int tamPart = size/canTpart;
        int resto = size%canTpart;
        //Si las particiones son mayores al tamaño del cache, se utiliza solo 1
        //particion del tamaño ingresado
        if (resto == size) {
            canTpart = 1;
            tamPart = size;
        }
        
        Object locks[] = new Object[canTpart];
        boolean NoEscribiendo[] = new boolean[canTpart];
                //Condiciones para controlar la escritura de los hilos en el servidor
        for (int i = 0; i < canTpart; i++) {
            NoEscribiendo[i] = true;
            locks[i] = new Object();
        }

        MemCache MemCompartida[] = new MemCache[canTpart];
        
        System.out.println(resto);
        if (resto!=0 && resto!=size){
            for (int i = 0; i < canTpart-1; i++) {
                MemCompartida[i] = new MemCache(tamPart);
            }
            int ultimaPart = resto+tamPart;
            MemCompartida[canTpart-1] = new MemCache(ultimaPart);
        }else if (resto == size){
            MemCompartida[0] = new MemCache(tamPart);
        }else{
            for (int i = 0; i < canTpart; i++) {
                MemCompartida[i] = new MemCache(tamPart);
            }
        }
        
        MemCompartida[0].cache.put("query1", "answer1");
        MemCompartida[0].cache.put("query2", "answer2");
        MemCompartida[1].cache.put("query3", "answer3");
        MemCompartida[1].cache.put("query4", "answer4");
        MemCompartida[2].cache.put("query5", "answer5");
        MemCompartida[2].cache.put("query6", "answer6");
        
        
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
                (new Thread (new HiloCachingService(connectionSocket, idSession, MemCompartida, NoEscribiendo, locks))).start();
                MemCompartida[0].print();
                MemCompartida[1].print();
                MemCompartida[2].print();
                idSession++;
            }
        } catch (IOException ex) {
            Logger.getLogger(CachingService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
  
}
