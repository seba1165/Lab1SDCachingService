/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab1sdcachingservice;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Seba
 */
class HiloIndexService implements Runnable{
    protected Socket socketClient;
    protected DataOutputStream outToServer;
    //protected DataInputStream dis;
    BufferedReader inFromUser;
    BufferedReader inFromServer;
    private int id;
    String sentence;
    String fromServer;
    String query;
    String answer;
    
    static String[] queries = {"query 1", "query 2", "query 3", "query 4", "query 5", "query 6", "query 7", "query 8", "query 9", "query 10", "query 11", "query 12", "query 13", "query 14", "query 15", "query 16", "query 17", "query 18", "query 19", "query 20"};
    static String answers[] = {"answer 1", "answer 2", "answer 3", "answer 4", "answer 5", "answer 6", "answer 7", "answer 8", "answer 9", "answer 10", "answer 11", "answer 12", "answer 13", "answer 14", "answer 15", "answer 16", "answer 17", "answer 18", "answer 19", "answer 20"};
    
    
     public static String getEntry(String query) {
        for (int i = 0; i < queries.length; i++) {
            if (queries[i].equals(query)) {
                return answers[i];
            }
        }
        return null;
    }

    HiloIndexService(int i, String query, String answer) {
        this.id = id;
        this.query = query;
        this.answer = answer;
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
                "ABC /users/1234",
                "POST /users username=gbenussi&password=contrasena", // adasdas
                "POST /respuestas/",
                //"POST /respuestas/hola body=<p>hola mundo</>",
                "POST /respuestas/hola body=<asdasdasdasdasd",
                "PUT /respuestas/hola title=hola+mundo", // aasdsadasdsa ACTUALIZA
                "PUT /users/1234 username=giovanni", // aasdsadasdsa ACTUALIZA
            };
            
            for (int i = 0; i < requests.length; i++) {
                System.out.println(requests[i]);
            }
            System.out.print("Ingrese numero: ");
            int numero = 2;
            
            requests[numero] = requests[numero] + query +" body="+answer;

            outToServer.writeBytes(requests[numero]+"\n");
            
            System.out.println("Front Service envia mensaje");

            outToServer.close();
            socketClient.close();
        } catch (IOException ex) {
            Logger.getLogger(HiloIndexService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
public class IndexService {
    
    public static void main(String[] args) {
        String query = "Query";
        String answer = "Answer";
        int numero;
        
        for (int i = 0; i < 1; i++) {
            String numero_query = Integer.toString(i);
            String query2  = query + numero_query;
            String answer2 = answer + numero_query;
            (new Thread (new HiloIndexService(i, query2, answer2))).start();   
        }
    }
}
