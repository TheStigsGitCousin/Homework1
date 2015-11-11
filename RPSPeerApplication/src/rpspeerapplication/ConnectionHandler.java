/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package rpspeerapplication;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ClosedChannelException;
import java.util.HashMap;
import java.util.Map;
/**
 *
 * @author David
 */
public class ConnectionHandler implements Runnable {
    private static final int PORT=5656;
    private static final int BUFFER_CAPACITY=3072;
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    private Map<SocketChannel,byte[]> dataTracking = new HashMap<SocketChannel, byte[]>();
    
    @Override
    public void run() {
        ServerSocket serverSocket;
        try {
            // TODO code application logic here
            serverSocketChannel=ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocket=serverSocketChannel.socket();
            serverSocket.bind(new InetSocketAddress(PORT));
            selector=Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            
            SocketChannel socketChannel=SocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.connect(new InetSocketAddress(PORT));
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
            
            processConnection();
        } catch (IOException ex) {
            Logger.getLogger(RPSPeerApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            processConnection();
        } catch (IOException ex) {
            Logger.getLogger(ConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void processConnection() throws IOException {
        while(true){
            int numberOfKeys=selector.select();
            if(numberOfKeys>0){
                Iterator keyIterator=selector.selectedKeys().iterator();
                while(keyIterator.hasNext()){
                    SelectionKey selectedKey=(SelectionKey)keyIterator.next();
                    if(selectedKey.isAcceptable()){
                        acceptConnection();
                    }else if(selectedKey.isReadable()){
                        acceptData(selectedKey);
                    }else if(selectedKey.isWritable()){
                        acceptWrite(selectedKey);
                    }else if(selectedKey.isConnectable()){
                        SocketChannel socketChannel;
                        socketChannel=(SocketChannel)selectedKey.channel();
                        if (socketChannel.isConnectionPending()) {
                            socketChannel.finishConnect();
                            System.out.println("Connection was pending but now is finiehed connecting.");
                        }
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ);   
                    }
                    
                    selector.selectedKeys().remove(selectedKey);
                }
            }
        }
        
    }
    
    private void acceptWrite(SelectionKey selectedKey) throws IOException {
        SocketChannel socketChannel;
        socketChannel=(SocketChannel)selectedKey.channel();
        if (socketChannel.isConnectionPending()) {
            socketChannel.finishConnect();
            System.out.println("Connection was pending but now is finiehed connecting.");
        }
        byte[] data=dataTracking.get(socketChannel);
        dataTracking.remove(socketChannel);
        socketChannel.write(ByteBuffer.wrap(data));
        selectedKey.interestOps(SelectionKey.OP_WRITE);
    }
    
    private void acceptData(SelectionKey selectedKey) throws IOException {
        SocketChannel socketChannel;
        Socket socket;
        ByteBuffer buffer=ByteBuffer.allocate(BUFFER_CAPACITY);
        socketChannel=(SocketChannel)selectedKey.channel();
        buffer.clear();
        int numberOfBytes=socketChannel.read(buffer);
        System.out.println(numberOfBytes + " bytes read.");
        socket=socketChannel.socket();
        if (numberOfBytes==-1){
            selectedKey.cancel();
            System.out.println("Closing socket "+socket);
            closeSocket(socket);
        }else{
            try{
                // Reset buffer pointer to start of buffer
                buffer.flip();
                byte[] data = new byte[1000];
                buffer.get(data, 0, numberOfBytes);
                
                dataTracking.put(socketChannel, data);
                selectedKey.interestOps(SelectionKey.OP_WRITE);
            }catch(Exception e){
                System.out.println("Closing socket "+socket);
                closeSocket(socket);
            }
        }
    }
    
    private void closeSocket(Socket socket) {
        try{
            if(socket!=null)
                socket.close();
        }catch(IOException ioEx){
            System.out.println("Unable to close socket");
        }
    }
    
    private void acceptConnection() throws IOException, ClosedChannelException {
        Socket socket;
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        socket=socketChannel.socket();
        System.out.println("Connection on '"+socket+"'");
        // Register SocketChannel to receive data
        socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
    }
    
}
