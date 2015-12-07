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
        //Variables para el Caching Service
        ServerSocket acceptSocket;
        String [] cacheCant;
        String [] cachePart;
        String [] cachePorc;
        int size;
        int canTpart;
        int porcentaje;
        try{
            //Config tiene los parametros de configuracion del cache
            File archivo = new File("Config.txt");
            //Mem_Estatica tiene algunas de las paginas mas visitadas de la web
            //Con esto se llena la memoria estaica
            File archivo2 = new File("Mem_Estatica.txt");
            FileReader fr = new FileReader(archivo);
            FileReader fr2 = new FileReader(archivo2);
            BufferedReader br = new BufferedReader(fr);
            BufferedReader br2 = new BufferedReader(fr2);
        
            String linea1 = br.readLine();
            String linea2 = br.readLine();
            String linea3 = br.readLine();
        
            cacheCant = linea1.split(" ");
            cachePart = linea2.split(" ");
            cachePorc = linea3.split(" ");
            
            //Hacer validación
            size = Integer.parseInt(cacheCant[1]);
            canTpart = Integer.parseInt(cachePart[1]);
            porcentaje = Integer.parseInt(cachePorc[1]);
            fr.close();
            int cant_mem_estatica = (porcentaje*size)/100;
            int cant_mem_dinamica = size - cant_mem_estatica;
            int tamPart = cant_mem_dinamica/canTpart;
            int resto = cant_mem_dinamica%canTpart;
            System.out.println("La cantidad de memoria estatica es "+cant_mem_estatica);
            System.out.println("La cantidad de memoria dinamica es "+cant_mem_dinamica);
            //Si las particiones de la mem dinamica son mayores al tamaño del cache de esta, se utiliza solo 1
            //particion del tamaño ingresado
            if (resto == cant_mem_dinamica) {
                canTpart = 1;
                tamPart = cant_mem_dinamica;
            }

            Object locks[] = new Object[canTpart];
            boolean NoEscribiendo[] = new boolean[canTpart];
            //Condiciones para controlar la escritura de los hilos en el servidor
            for (int i = 0; i < canTpart; i++) {
                NoEscribiendo[i] = true;
                locks[i] = new Object();
            }

            //Memoria Estatica
            MemCache Mem_estatica = new MemCache(cant_mem_estatica);
            //Memoria Dinamica
            MemCache Mem_dinamica[] = new MemCache[canTpart];

            if (resto!=0 && resto!=cant_mem_dinamica){
                for (int i = 0; i < canTpart-1; i++) {
                    Mem_dinamica[i] = new MemCache(tamPart);
                }
                int ultimaPart = resto+tamPart;
                Mem_dinamica[canTpart-1] = new MemCache(ultimaPart);
            }else if (resto == cant_mem_dinamica){
                Mem_dinamica[0] = new MemCache(tamPart);
            }else{
                for (int i = 0; i < canTpart; i++) {
                    Mem_dinamica[i] = new MemCache(tamPart);
                }
            }
            
            int lineas_estatica = cuenta();
            //System.out.println(lineas_estatica+" cantidad de estatica");
            //Si la memoria estatica es menor a la cantidad de lineas del txt de paginas mas visitadas
            //Se lee y se pasa a la memoria estatica
            if (cant_mem_estatica <= lineas_estatica ) {
                for (int i = 0; i < cant_mem_estatica; i++) {
                    String lineaEst = br2.readLine();
                    String [] linea_Est = lineaEst.split(" ");;
                    Mem_estatica.addEntryToCache(linea_Est[0], linea_Est[1]);
                }
                
            //Si no, se completa lo que falta con querys y answer
            }else{
                System.out.println("AQUIIII22");
                for (int i = 0; i < lineas_estatica; i++) {
                    String lineaEst = br2.readLine();
                    String [] linea_Est = lineaEst.split(" ");;
                    Mem_estatica.addEntryToCache(linea_Est[0], linea_Est[1]);
                }
                String query = "Query";
                String answer = "Answer";
                
                for (int i = lineas_estatica+1; i < cant_mem_estatica; i++) {
                    String numero_query = Integer.toString(i);
                    String query2  = query + numero_query;
                    String answer2 = answer + numero_query;
                    Mem_estatica.addEntryToCache(query2, answer2);
                }
            }
            
            Mem_estatica.print();

            fr2.close();

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
                    (new Thread (new HiloCachingService(connectionSocket, idSession, Mem_dinamica, Mem_estatica, NoEscribiendo, locks))).start();
                    idSession++;
                }
            } catch (IOException ex) {
                Logger.getLogger(CachingService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }catch (Exception e){ //Catch de excepciones
            System.err.println("Ocurrio un error: " + e.getMessage());
        }
    }

    private static int cuenta() throws FileNotFoundException, IOException {
        FileReader fr = new FileReader("Mem_Estatica.txt");
        BufferedReader bf = new BufferedReader(fr);
        int lNumeroLineas = 0;
        String sCadena;
 
        while ((sCadena = bf.readLine())!=null) {
            lNumeroLineas++;
        }
        fr.close();
        return lNumeroLineas;
    }
}
