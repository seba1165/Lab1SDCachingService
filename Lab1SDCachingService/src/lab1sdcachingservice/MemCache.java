/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab1sdcachingservice;

/**
 *
 * @author Frank
 */
import java.util.LinkedHashMap;

/**
 *
 * @author Seba
 */
//Cache LRU
public class MemCache {
    
    int size;
    LinkedHashMap<String, String> cache;
    //Obtener answer de algun query determinado
    public String getEntryFromCache(String query) {
        String result = cache.get(query);
        if(result != null) {
            cache.remove(query);
            cache.put(query, result);
        }
        return result;
    }

    public MemCache(int size) {
        this.size = size;
        this.cache = new LinkedHashMap<>();
    }
    //Añadir entrada al cache LRU
    public void addEntryToCache(String query, String answer) {
        if (cache.containsKey(query)) { // HIT
            // Bring to front
            cache.remove(query);
            cache.put(query, answer);
        } else { // MISS
            if(cache.size() == this.size) {
                String first_element = cache.entrySet().iterator().next().getKey();
                System.out.println("Removiendo: '" + first_element + "'");
                cache.remove(first_element);
            }
            cache.put(query, answer);
        }
    }
    
    
    //Si algún hilo se encuentra escribiendo en la particion del cache, no se puede leer
    public String leer_en_particion(boolean NoEscribiendo, String query) {
        while(!NoEscribiendo){//Espera mientras alguien este escribiendo   
        }
        return getEntryFromCache(query);
    }
    //2 o mas hilos no pueden escribir simultaneamente en la misma particion del cache
    public void escribir_en_particion(boolean NoEscribiendo, String query, String answer, Object Mylock) {
        synchronized (Mylock){
            NoEscribiendo = false;
            addEntryToCache(query, answer);
            NoEscribiendo = true;
        }
    }
}