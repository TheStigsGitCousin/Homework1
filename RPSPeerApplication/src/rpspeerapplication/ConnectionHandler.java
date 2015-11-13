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
import java.nio.Buffer;
import java.nio.channels.ClosedChannelException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
/**
 *
 * @author David
 */
public class ConnectionHandler implements Runnable {
    public int LISTENING_PORT=5656;
    public int CONNECTING_PORT=6565;
    private static final int BUFFER_CAPACITY=3072;
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    public final LinkedBlockingQueue<String> commands = new LinkedBlockingQueue<>();
    HashMap<SocketChannel, ByteBuffer> channelToBufferMap=new HashMap<>();
    
    public final Event acceptEvent=new Event();
    public final Event connectedEvent=new Event();
    public final Event readEvent=new Event();
    public final Event writeEvent=new Event();
    
    public void addCommand(String message){
        commands.add(message);
    }
    
    public void Connect(){
        new Thread( new Runnable(){
            
            @Override
            public void run() {
                try {
                    // TODO code application logic here
                    SocketChannel socketChannel=SocketChannel.open();
                    socketChannel.configureBlocking(false);
                    socketChannel.connect(new InetSocketAddress(CONNECTING_PORT));
                    socketChannel.register(selector, SelectionKey.OP_CONNECT);
                } catch (IOException ex) {
                    Logger.getLogger(RPSPeerApplication.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();
        
    }
    
    @Override
    public void run() {
        ServerSocket serverSocket;
        try {
            // TODO code application logic here
            serverSocketChannel=ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocket=serverSocketChannel.socket();
            serverSocket.bind(new InetSocketAddress(LISTENING_PORT));
            selector=Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            
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
    
    private synchronized void processConnection() throws IOException {
        while(true){
            int numberOfKeys=selector.select(1000); //selector.selectNow();
            if(numberOfKeys>0){
                Iterator keyIterator=selector.selectedKeys().iterator();
                while(keyIterator.hasNext()){
                    SelectionKey selectedKey=(SelectionKey)keyIterator.next();
                    if(selectedKey.isAcceptable()){
                        accept();
                    }else if(selectedKey.isReadable()){
                        read(selectedKey);
                    }else if(selectedKey.isWritable()){
                        write(selectedKey);
                    }else if(selectedKey.isConnectable()){
                        connected(selectedKey);
                    }
                    
                    selector.selectedKeys().remove(selectedKey);
                }
            }
        }
        
    }
    
    private void accept() throws IOException, ClosedChannelException {
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        System.out.println("Connection on '"+socketChannel.socket()+"'");
        // Register SocketChannel to receive data
        socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        acceptEvent.FireEvent(socketChannel.socket());
    }
    
    private void connected(SelectionKey selectedKey) throws ClosedChannelException, IOException {
        SocketChannel socketChannel;
        socketChannel=(SocketChannel)selectedKey.channel();
        if (socketChannel.isConnectionPending()) {
            socketChannel.finishConnect();
            System.out.println("Connection was pending but now is finiehed connecting.");
        }
        System.out.println("Accepted connection to socket "+socketChannel.socket());
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ);
        connectedEvent.FireEvent(socketChannel.socket());
    }
    
    private void write(SelectionKey selectedKey) throws IOException {
        if(commands.isEmpty())
            return;
        
        SocketChannel socketChannel;
        socketChannel=(SocketChannel)selectedKey.channel();
        if (socketChannel.isConnectionPending()) {
            socketChannel.finishConnect();
            System.out.println("Connection was pending but now is finiehed connecting.");
        }
        byte[] data=null;
        synchronized(this){
            try {
                data=commands.take().getBytes();
                socketChannel.write(ByteBuffer.wrap(data));
                System.out.println("Sent message: "+new String(data));
                selectedKey.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
            } catch (InterruptedException ex) {
                Logger.getLogger(ConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        writeEvent.FireEvent(data);
    }
    
    private void read(SelectionKey selectedKey) throws IOException {
        SocketChannel socketChannel;
        Socket socket;
        socketChannel=(SocketChannel)selectedKey.channel();
        ByteBuffer buffer;
        if(channelToBufferMap.containsKey(socketChannel))
            buffer=channelToBufferMap.get(socketChannel);
        else
            buffer=ByteBuffer.allocate(BUFFER_CAPACITY);
        
        int numberOfBytes=socketChannel.read(buffer);
        System.out.println(numberOfBytes + " bytes read.");
        socket=socketChannel.socket();
        byte[] data=null;
        if (numberOfBytes==-1){
            selectedKey.cancel();
            System.out.println("Closing socket "+socket);
            closeSocket(socket);
        }else{
            try{
                // Reset buffer pointer to start of buffer
                buffer.flip();
                data= new byte[1000];
                buffer.get(data, 0, numberOfBytes);
                System.out.println("Recieved message: "+new String(data));
                selectedKey.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
            }catch(Exception e){
                System.out.println("Closing socket "+socket);
                closeSocket(socket);
            }
        }
        readEvent.FireEvent(data);
    }
    
    private void closeSocket(Socket socket) {
        try{
            if(socket!=null)
                socket.close();
        }catch(IOException ioEx){
            System.out.println("Unable to close socket");
        }
    }
    
}
