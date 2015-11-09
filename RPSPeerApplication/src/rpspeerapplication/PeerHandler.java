/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package rpspeerapplication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author David
 */
public class PeerHandler implements Runnable {
    private boolean isHost=false;
    private final Socket socket;
    
    public PeerHandler(Socket socket){
        this.socket=socket;
    }
    
    @Override
    public void run() {
        BufferedInputStream in=null;
        BufferedOutputStream out=null;
        try {
            in=new BufferedInputStream(socket.getInputStream());
            out=new BufferedOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(PeerHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        byte[] msg=new byte[1024];
        while(true){
            try {
                String response=getClientResponse(in, msg);
                String message="";
            } catch (IOException ex) {
                Logger.getLogger(PeerHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private String getClientResponse(BufferedInputStream in, byte[] msg) throws IOException {
        int n;
        int bytesRead=0;
        while((n=in.read(msg, bytesRead, 256))!=-1){
            bytesRead+=n;
            if(bytesRead==1024)
                break;
            
            if(in.available()==0)
                break;
        }
        String input=new String(msg, 0, bytesRead);
        return input;
    }
    
    
}
