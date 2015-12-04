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
import java.net.Socket;
import java.util.*;
import java.util.logging.*;
import org.json.simple.JSONObject;

class HiloFrontService extends Thread {
    protected Socket socketClient;
    protected DataOutputStream outToServer;
    //protected DataInputStream dis;
    BufferedReader inFromUser;
    BufferedReader inFromServer;
    private int id;
    String sentence;
    String fromServer;
    
    String query;
    
    public HiloFrontService(int id, String query) {
        this.id = id;
        this.query = query;
    }
    @Override
    public void run() {
        try {
            inFromUser = new BufferedReader(new InputStreamReader(System.in));
            //Socket para el cliente (host, puerto)
            socketClient = new Socket("localhost", 5000);
            outToServer = new DataOutputStream(socketClient.getOutputStream());
            //dis = new DataInputStream(sk.getInputStream());
            inFromServer = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
            
            //Leemos del cliente y lo mandamos al servidor
            //sentence = inFromUser.readLine();
            
            String[] requests = {
            "GET /respuestas/aslkd;askd;l ;ldsad", // <p>hola mundo</>
            "GET /users",
            "GET /users/1234",
            "GET /users/55556",
            "ABC /users/1234",};
            
            for (int i = 0; i < requests.length; i++) {
                System.out.println(requests[i]);
            }
            System.out.print("Ingrese numero: ");
            
            //int numero = Integer.parseInt(inFromUser.readLine());
            int numero = 0;
            requests[numero] = requests[numero] + query;
            outToServer.writeBytes(requests[numero]+"\n");
            
            System.out.println("Front Service envia mensaje "+requests[0]);
            
            //Recibimos del servidor
            fromServer = inFromServer.readLine();
            
            System.out.println("Server response: " + fromServer);
            outToServer.close();
            socketClient.close();
        } catch (IOException ex) {
            Logger.getLogger(HiloFrontService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
public class FrontService {
    
    public static void main(String[] args) {
        ArrayList<Thread> clients = new ArrayList<>();
        String query = "Query ";
        int numero;
        
        for (int i = 0; i < 1; i++) {
            String numero_query = Integer.toString(i);
            query = query + numero_query;
            clients.add(new HiloFrontService(i, query));
        }
        for (Thread thread : clients) {
            thread.start();
        }
    }

}