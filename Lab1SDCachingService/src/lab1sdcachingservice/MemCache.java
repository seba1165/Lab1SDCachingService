/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab1sdcachingservice;

import java.util.LinkedHashMap;

/**
 *
 * @author Seba
 */
public class MemCache {
    
    int size;
    LinkedHashMap<String, String> cache;
    
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
    
    public void print() {
        System.out.println("===== My LRU Cache =====");
        System.out.println("| " + String.join(" | ", cache.keySet()) + " | ");
        System.out.println("========================");
    }

    public synchronized String leer_en_particion(boolean NoEscribiendo, String query) {
        // Wait until message is
        // available.
        while (!NoEscribiendo) {
            try {
                System.out.println("Alguien esta escribiendo en la particion");
                wait();
            } catch (InterruptedException e) {}
        }
        return getEntryFromCache(query);
    }
}
