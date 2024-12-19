/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */

package javclientsri;


import com.google.gson.Gson;
/**
 *
 * @author lenovo
 */
public class JavClientSri {

        Gson gson = new Gson();
        static String claveAcceso;
        static String rutaGenerado;
        static String rutaFirmado;
        static String rutaEnviado ;
        static String rutaAutorizado;
        static String rutaRechazado;
        static String rutaNoAutorizado;
        static String rutaRecepcionSRI;
        static String rutaAutorizacionSRI;  
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        JavClientSri miClase = new JavClientSri();
        if(args.length>0)
        {   
            switch(args[0])
            {
                case "1":
                    claveAcceso =  args[1];
                    rutaFirmado =  args[2];
                    rutaEnviado =  args[3];
                    rutaRechazado =  args[4];
                    rutaRecepcionSRI =  args[5];
                    miClase.envia(rutaFirmado,claveAcceso,rutaEnviado,rutaRechazado,rutaRecepcionSRI);
                break;
                case "2":                    
                    claveAcceso =  args[1];
                    rutaAutorizado =  args[2];
                    rutaNoAutorizado =   args[3];
                    rutaAutorizacionSRI =  args[4];
                    miClase.comprobar(rutaAutorizado,rutaNoAutorizado,rutaAutorizacionSRI,claveAcceso);

                break;
                case "3":
                    //envia();
                break;
                case "4":
                    //envia();
                break;
            }   
        }
    }
    
    public void envia(String rutaFirmado,String claveAcceso,String rutaEnviado,String rutaRechazado,String rutaRecepcion) {
        Enviar enviar = new Enviar(rutaFirmado+"/"+claveAcceso+".xml",rutaEnviado,rutaRechazado,rutaRecepcion);
        String[] Resp = enviar.executeEnviar();   
        String json = gson.toJson(Resp);
        System.out.println(json);
        
   /*     Comprobar comprovar = new Comprobar("/app/Quijotelui/autorizado", 
                "/app/Quijotelui/noautorizado", 
                "https://celcer.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl");
        
        comprovar.executeComprobar("051220170110044566770011001002000001079123456781");
*/
    } 
    
    public void comprobar(String rutaAutorizado,String rutaNoAutorizado,String rutaAutorizacionSRI,String claveAcceso)
    {
        Comprobar comprovar = new Comprobar(rutaAutorizado,rutaNoAutorizado,rutaAutorizacionSRI);
        String[] Resp= comprovar.executeComprobar(claveAcceso);
        // Convertir array a JSON
        String json = gson.toJson(Resp);
        System.out.println(json);

    }
    
}
