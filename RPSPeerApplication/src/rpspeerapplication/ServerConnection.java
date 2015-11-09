/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package rpspeerapplication;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author David
 */
public class ServerConnection implements Runnable {
    private final ServerSocket serverSocket;
    
    public ServerConnection(int port) throws IOException{
        serverSocket=new ServerSocket(port);
    }
    
    @Override
    public void run() {
        try {
            while(true){
                Socket socket=serverSocket.accept();
                (new Thread(new PeerHandler(socket))).start();
            }
        } catch (IOException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
