/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package serverapplication;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author David
 */
public class ServerApplication {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            int port=8080;
            if(args.length==1){
                try{
                    port=Integer.parseInt(args[0]);
                }catch(NumberFormatException e){e.printStackTrace();}
            }
            ServerSocket serverSocket=new ServerSocket(port);
            while(true){
                Socket clientSocket=serverSocket.accept();
                (new ClientHandler(clientSocket)).start();
            }
        } catch (IOException ex) {
            Logger.getLogger(ServerApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
