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
import org.json.simple.JSONObject;
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
    MemCache[] MemDinamica;
    MemCache MemEstatica;
    MemCache miParticion;
    boolean condicion_particion[];
    int particiones;
    boolean NoEscribiendo;
    Object locks[];
    Object Mylock;
    //String para mensaje enviado por el cliente;
    String request;
    
    public HiloCachingService(Socket socket, int id, MemCache[] Mem_dinamica, MemCache Mem_estatica, boolean[] condicion_particion, Object locks[]) throws IOException {
        this.socket = socket;
        this.idSession = id;
        this.MemDinamica = Mem_dinamica;
        this.MemEstatica = Mem_estatica;
        this.condicion_particion = condicion_particion;
        this.particiones = condicion_particion.length;
        this.locks = locks;
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
            //Recibe consultas del Front Service u ordenes de ingresar informacion al cache desde el Index Service
            outToClient = new DataOutputStream(socket.getOutputStream());
            inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //dis = new DataInputStream(socket.getInputStream());
            fromClient =inFromClient.readLine();
            request = fromClient;
            
            //System.out.println("Servidor "+ idSession);
            //Los mensajes recibidos tienen el formato REST
            String[] tokens = request.split(" ");
            String parametros = tokens[1];
            int espacios = tokens.length;
            String http_method = tokens[0];

            String[] tokens_parametros = parametros.split("/");
            String resource = tokens_parametros.length > 1 ? tokens_parametros[1] : "";

            String id = tokens_parametros.length > 2 ? tokens_parametros[2] : "";
            int cantidadQuerys=0;
//            System.out.println("Partes restantes del query: "+(tokens.length-2));
            
            if (tokens.length-2>0){
                for (int i = 0; i < tokens.length-2; i++) {
                    id += " "+tokens[i+2];
                }
            }
//            System.out.println("El query completo es "+id);
            
            
//            for (int i = 0; i < tokens.length; i++) {
//                System.out.println(tokens[i]);
//            }
            
//            for (int i = 0; i < tokens_parametros.length; i++) {
//                System.out.println(tokens_parametros[i]);
//            }
            String meta_data = tokens.length > 2 ? tokens[2] : "";
            
            
//            System.out.println("\nConsulta: " + request);
//            System.out.println("HTTP METHOD: " + http_method);
//            System.out.println("Resource: " + resource);
//            System.out.println("ID:       " + id);
//            System.out.println("META DATA:    " + meta_data);
            
            //System.out.println("La consulta se deberia encontrar en la posicion "+posicion_consulta);
            
            JSONObject jo = new JSONObject();
            
            switch (http_method) {
                case "GET":
                    if (id == "") {
                        System.out.println("El mensaje fue enviado por el FrontService");
                        System.out.println("Buscando en el cache los ultimos 10 registros de tipo '" + resource + "'");
                        // buscar en el cache
                        // hit o miss
                    } else {
                        int posicion_consulta = funcion_hash(id, condicion_particion.length);
                        miParticion = MemDinamica[posicion_consulta];
                        NoEscribiendo = condicion_particion[posicion_consulta];
                        Mylock = locks[posicion_consulta];
                        System.out.println("El mensaje fue enviado por el FrontService");
                        System.out.println("Buscando en el cache de '" + resource + "' el registro con id " + id);
                        String result;
                        String result2;
                        //Primero se busca en memoria estatica
                        result = MemEstatica.leer_en_particion(NoEscribiendo, id);
                        //result = miParticion.leer_en_particion(NoEscribiendo, id);
                         if (result == null) { // MISS en estatica
                             //Se busca en memoria dinamica
                             result2 = miParticion.leer_en_particion(NoEscribiendo, id);
                             if (result2 == null) { // MISS dinamica
                                jo.put("Result", "Miss");
                                System.out.println("Miss en mem dinamica "+posicion_consulta);
                                outToClient.writeBytes(jo.toJSONString());
                            }else{ //Hit en dinamica
                                jo.put("Result", "Hit");
                                jo.put("Query", id);
                                jo.put("Answer", result2);
                                outToClient.writeBytes(jo.toJSONString());
                                System.out.println("Hit en mem dinamica");
                            }
                        }else{
                             //Hit en mem estatica
                             jo.put("Result", "Hit");
                             jo.put("Query", id);
                             jo.put("Answer", result);
                 
                             outToClient.writeBytes(jo.toJSONString());
                             System.out.println("Hit en mem estatica");
                        }
                    }
                    break;
                case "POST":
                    //System.out.println("El mensaje fue enviado por el IndexService");
                    //System.out.println("Creando un usuario con los siguientes datos: (" + meta_data + ")");
                    for (String params : meta_data.split("&")) {
                        String[] parametros_meta = params.split("=");
                        //System.out.println("\t* " + parametros_meta[0] + " -> " + parametros_meta[1]);
                    }
                    //Si se recibe un post, se ingresa la informacion en el cache dinamico
                    String[] querySplit = id.split(" ");
                    String[] answerSplit = querySplit[1].split("=");
                    int posicion_consulta = funcion_hash(querySplit[0], condicion_particion.length);
                    miParticion = MemDinamica[posicion_consulta];
                    NoEscribiendo = condicion_particion[posicion_consulta];
                    Mylock = locks[posicion_consulta];
                    miParticion.escribir_en_particion(NoEscribiendo, querySplit[0], answerSplit[1], Mylock);
                    System.out.println("Se escribio "+querySplit[0]+" en el cache dinamico "+posicion_consulta);
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
            
        } catch (IOException ex) {
            Logger.getLogger(HiloCachingService.class.getName()).log(Level.SEVERE, null, ex);
        }
        desconnectar();
    }
    //Funcion para calcular en que particion del cache se guarda la informacion que ha llegado desde el index Service
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
