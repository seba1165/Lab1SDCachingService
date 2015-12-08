# Lab1SDCachingService
Implementación de Caching Service

Parámetros del cache:
  Para configurar los parámetros del cache, se debe editar el archivo Config.txt. En este archivo existen 3 lineas para manipular el       cache:
    La primera linea corresponde al tamaño.
    La segunda a la cantidad de particiones.
    La tercera al porcentaje de memoria estática para el cache.
  
Cache Estático:
  Para llenar el cache estático se utiliza un archivo txt llamado Mem_Estatica, donde estarán algunas de las páginas mas visitadas de la   web.En caso de que la parte estática del cache sea mayor a la cantidad de páginas en el txt, se rellena el espacio restante con querys   y answer con números. Por ejemplo, si se tienen 10 lineas en el Mem_Estatica.txt, y la parte estática del cache es de tamaño 15, los 5   espacios restantes se llenarían así:
    {Query11, Answer11},{Query12, Answer12} y así hasta llegar al 15.
  
Cache Dimámico:
  En un comienzo el cache dinámico esta vacío, por lo que debe llenarse con el index service. Con el Index Service incluido en el          programa se puede llenar el cache cambiando la cantidad de hilos que realizarán inserciones. Con esta opción se llena el cache dinámico   con querys con numeros, de la misma forma en que se llena el espacio restante del cache estático.
