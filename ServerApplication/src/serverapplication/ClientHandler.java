/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package serverapplication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 *
 * @author David
 */
public class ClientHandler extends Thread {
    private Socket socket;
    
    public ClientHandler(Socket clientSocket){
        this.socket=clientSocket;
    }
    
    @Override
    public void run(){
        boolean isConnected=true;
        String currentWord=getRandomWord();
        if(currentWord==null)
            return;
        
        BufferedInputStream in=null;
        BufferedOutputStream out=null;
        try {
            in=new BufferedInputStream(socket.getInputStream());
            out=new BufferedOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        int failedAttempts=10;
        char[] correctLetters=new char[currentWord.length()];
        byte[] msg =new byte[1024];
        int bytesRead=0;
        int n;
        try {
            while(isConnected){
                String hiddenWord=getHiddenWord(currentWord, correctLetters);
                String message=hiddenWord+"|"+failedAttempts;
                out.write(message.getBytes());
                out.flush();
                
                bytesRead=0;
                while((n=in.read(msg, bytesRead, 256))!=-1){
                    bytesRead+=n;
                    if(bytesRead==1024)
                        break;
                    
                    if(in.available()==0)
                        break;
                }
                String input=new String(msg, 0, bytesRead);
                
            }
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private String getHiddenWord(String word, char[] guessedLetters){
        StringBuilder hiddenWord=new StringBuilder();
        for(int i=0;i<word.length();i++){
            hiddenWord.append(charArrayContains(word.charAt(i), guessedLetters)?word.charAt(i):'-');
        }
        
        return hiddenWord.toString();
    }
    
    private boolean charArrayContains(char c, char[] array){
        for(char ch : array){
            if(ch==c)
                return true;
        }
        return false;
    }
    
    private String getRandomWord(){
        File file=new File("/Assets/words.txt");
        Scanner scanner;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        Random r = new Random();
        int Low = 0;
        int High = 100;
        int R = r.nextInt(High-Low) + Low;
        for(int i=0;i<R;i++)
            scanner.next();
        
        String selectedWord=scanner.next();
        return selectedWord;
    }
}
