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
public class HiloCachingService implements Runnable {
    //Atributos para conexion
    private Socket socket;
    private DataOutputStream outToClient;
    BufferedReader inFromClient;
    String fromClient;
    String processedData;
    //Id del hilo 
    private int idSession;
    //Atributos para el manejo del cache
    MemCache[] MemCompartida;
    MemCache miParticion;
    boolean condicion_particion[];
    int particiones;
    boolean NoEscribiendo;
    Object locks[];
    Object Mylock;
    //String para mensaje enviado por el cliente;
    String request;
    
    public HiloCachingService(Socket socket, int id, MemCache[] MemCompartida, boolean[] condicion_particion, Object locks[]) throws IOException {
        this.socket = socket;
        this.idSession = id;
        this.MemCompartida = MemCompartida;
        this.condicion_particion = condicion_particion;
        this.particiones = condicion_particion.length;
        this.locks = locks;
        try {
            outToClient = new DataOutputStream(socket.getOutputStream());
            inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //dis = new DataInputStream(socket.getInputStream());
            fromClient =inFromClient.readLine();
            request = fromClient;
            
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
            System.out.println("Servidor "+ idSession);
            
            //System.out.println("Received: " + fromClient+" from client " + idSession);

            
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
            System.out.println("ID:       " + id);
            System.out.println("META DATA:    " + meta_data);
            
            int posicion_consulta = funcion_hash(id, condicion_particion.length);
            miParticion = MemCompartida[posicion_consulta];
            NoEscribiendo = condicion_particion[posicion_consulta];
            Mylock = locks[posicion_consulta];
            
            System.out.println("La consulta se deberia encontrar en la posicion "+posicion_consulta);
            
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
                        String result;
                        result = miParticion.leer_en_particion(NoEscribiendo, id);
                         if (result == null) { // MISS
                            System.out.println("MISS :(");
                            //Miss para Front service
                        }else{
                            System.out.println("HIT !");
                            //Hit para front service
                        }
                    }
                    break;
                case "POST":
                    System.out.println("El mensaje fue enviado por el IndexService");
                    System.out.println("Creando un usuario con los siguientes datos: (" + meta_data + ")");
                    for (String params : meta_data.split("&")) {
                        String[] parametros_meta = params.split("=");
                        System.out.println("\t* " + parametros_meta[0] + " -> " + parametros_meta[1]);
                    }
                        miParticion.escribir_en_particion(NoEscribiendo, id, "asnwerCualquiera", Mylock);
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
            
            String reverse = new StringBuffer(fromClient).reverse().toString() + '\n';
            //Cambiar por JSON
            outToClient.writeBytes(reverse);
            
        } catch (IOException ex) {
            Logger.getLogger(HiloCachingService.class.getName()).log(Level.SEVERE, null, ex);
        }
        desconnectar();
    }
    
    int funcion_hash(String x, int particiones) {
        char ch[];
        ch = x.toCharArray();
        int xlength = x.length();
        int i, sum;
        for (sum=0, i=0; i < x.length(); i++)
            sum += ch[i];
        return sum % particiones;
    }
     
}
