/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package clientapplication;

import clientapplication.Views.ClientPanel;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author David
 */
public class ServerHandler extends Thread {
    private final static String SOCKET_CONNECTION_FAILURE="Socket couldn't connect to host";
    
    private final int port;
    private final String host;
    private final ClientPanel gui;
    private final LinkedBlockingQueue<String> commands =
            new LinkedBlockingQueue<>();
    private BufferedInputStream in;
    private BufferedOutputStream out;
    private Socket socket;
    
    public ServerHandler(ClientPanel gui, int port , String host){
        this.port=port;
        this.host=host;
        this.gui=gui;
    }
    
    @Override
    public void run(){
        connect();
        gui.connected();
        byte[] message=new byte[1024];
        while(true){
            try {
                synchronized(commands){
                    while(commands.isEmpty())
                        wait();
                    
                    out.write(commands.take().getBytes());
                    
                    String response=getServerResponse(message);
                    gui.messageReceived(response);
                }
            } catch (InterruptedException ex) {
                gui.statusChanged(ex.toString());
                break;
            } catch (IOException ex) {
                gui.statusChanged(ex.toString());
            }
        }
    }
    
    public void addCommand(String command){
        commands.add(command);
    }

    private String getServerResponse(byte[] message) throws IOException {
        int bytesRead=0,n;
        while((n=in.read(message, bytesRead, 256))!=-1){
            bytesRead+=n;
            if(bytesRead==1024)
                break;
            
            if(in.available()==0)
                break;
        }
        String response=new String(message,0,bytesRead);
        return response;
    }
    
    public void connect(){
        try {
            socket=new Socket(host,port);
            in=new BufferedInputStream(socket.getInputStream());
            out=new BufferedOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            gui.statusChanged(SOCKET_CONNECTION_FAILURE);
        }
    }
}
