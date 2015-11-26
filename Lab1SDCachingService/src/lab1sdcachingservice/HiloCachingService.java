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
public class HiloCachingService extends Thread {
    
    private Socket socket;
    private DataOutputStream outToClient;
    //private DataInputStream dis;
    BufferedReader inFromClient;
    private int idSession;
    String fromClient;
    String processedData;
    
    public HiloCachingService(Socket socket, int id) {
        this.socket = socket;
        this.idSession = id;
        try {
            outToClient = new DataOutputStream(socket.getOutputStream());
            inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //dis = new DataInputStream(socket.getInputStream());
            
        } catch (IOException ex) {
            Logger.getLogger(HiloCachingService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void desconnectar() {
        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(HiloCachingService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @Override
    public void run() {
        try {
            fromClient =inFromClient.readLine();
            //System.out.println("Received: " + fromClient+" from client " + idSession);
            
            //Identifica quien envio mensaje
            String request = fromClient;
            String[] tokens = request.split(" ");
            String parametros = tokens[1];
            
            String http_method = tokens[0];

            String[] tokens_parametros = parametros.split("/");

            String resource = tokens_parametros.length > 1 ? tokens_parametros[1] : "";
            String id = tokens_parametros.length > 2 ? tokens_parametros[2] : "";

            String meta_data = tokens.length > 2 ? tokens[2] : "";
            
            System.out.println("\nConsulta: " + request);
            System.out.println("HTTP METHOD: " + http_method);
            System.out.println("Resource: " + resource);
            System.out.println("ID:          " + id);
            System.out.println("META DATA:    " + meta_data);
            
            switch (http_method) {
                case "GET":
                    if (id == "") {
                        System.out.println("El mensaje fue enviado por el FrontService");
                        System.out.println("Buscando en el cache los ultimos 10 registros de tipo '" + resource + "'");
                        // buscar en el cache
                        // hit o miss
                    } else {
                        System.out.println("El mensaje fue enviado por el FrontService");
                        System.out.println("Buscando en el cache de '" + resource + "' el registro con id " + id);
                    }
                    break;
                case "POST":
                    System.out.println("El mensaje fue enviado por el IndexService");
                    System.out.println("Creando un usuario con los siguientes datos: (" + meta_data + ")");
                    for (String params : meta_data.split("&")) {
                        String[] parametros_meta = params.split("=");
                        System.out.println("\t* " + parametros_meta[0] + " -> " + parametros_meta[1]);
                    }
                    break;
                case "PUT":
                    System.out.println("El mensaje fue enviado por el IndexService");
                    System.out.println("Actualizando el usuario con id " + id + " con los siguientes datos (" + meta_data + ")");
                    for (String params : meta_data.split("&")) {
                        String[] parametros_meta = params.split("=");
                        System.out.println("\t* " + parametros_meta[0] + " -> " + parametros_meta[1]);
                    }
                    break;
                default:
                    System.out.println("Not a valid HTTP Request");
                    break;
            }
            
            /*Procedimiento
            
            */
            
            String reverse = new StringBuffer(fromClient).reverse().toString() + '\n';
            //Cambiar por JSON
            outToClient.writeBytes(reverse);
            
        } catch (IOException ex) {
            Logger.getLogger(HiloCachingService.class.getName()).log(Level.SEVERE, null, ex);
        }
        desconnectar();
    }
}
