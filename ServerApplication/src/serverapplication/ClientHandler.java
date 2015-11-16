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
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author David
 */
public class ClientHandler extends Thread {
    private final Socket socket;
    private int failedAttempts=10;
    private int score=0;
    private String currentWord;
    private boolean isFinished=false;
    
    public ClientHandler(Socket clientSocket){
        this.socket=clientSocket;
    }
    
    @Override
    public void run(){
        BufferedInputStream in=null;
        BufferedOutputStream out=null;
        try {
            in=new BufferedInputStream(socket.getInputStream());
            out=new BufferedOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        byte[] msg=new byte[1024];
        ArrayList<Character> correctLetters=new ArrayList<>();
        String message="";
        try {
            while(message!=null){
                System.out.println("Server: Waiting for client");
                String d=getClientResponse(in, msg).toLowerCase();
                System.out.println("Server: Response = "+d);
                String[] response=d.split("\\|");
                System.out.println("Server: Response length = "+response.length);
                
                if(response.length==2)
                    message=tryParseCommand(response[0], response[1], correctLetters);
                
                System.out.println("Message = "+message);
                
                System.out.println("Letters size = '"+correctLetters.size());
                if(message!=null){
                    out.write(message.getBytes());
                    out.flush();
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch(Exception e){
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, e);
        }
        finally {
            try {
                if(in!=null)
                    in.close();
                if(out!=null)
                    out.close();
                if(socket!=null)
                    socket.close();
            } catch (IOException ex) {
                Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private String constructWordMessage(String currentWord, ArrayList<Character> correctLetters) {
        String hiddenWord=getHiddenWord(currentWord, correctLetters);
        String message=hiddenWord+", failed attempts = "+failedAttempts+", score = "+score;
        return message;
    }
    
    private String tryParseCommand(String command, String data, ArrayList<Character> list){
        System.out.println("command = '"+command+", data = "+data);
        String message=null;
        if(command.equals("guess") && !isFinished){
            if(data.length()==1){
                if(currentWord.contains(Character.toString(data.charAt(0)))){
                    if(!list.contains(data.charAt(0))){
                        list.add(data.charAt(0));
                        System.out.println("Added '"+data.charAt(0)+"'");
                    }
                }else{
                    failedAttempts--;
                }
                message=constructWordMessage(currentWord, list);
            }else if(data.length()==currentWord.length()){
                if(data.equals(currentWord)){
                    score++;
                    message="Congratulations! Word = "+currentWord+", score = "+score;
                    isFinished=true;
                }else{
                    failedAttempts--;
                    message=constructWordMessage(currentWord, list);
                }
            }else{
                failedAttempts--;
                message=constructWordMessage(currentWord, list);
            }
            if(failedAttempts==0){
                score--;
                message="GAME OVER! Score = "+score;
                isFinished=true;
            }else if(containsList(currentWord, list)){
                score++;
                message="Congratulations! Word = "+currentWord+", score = "+score;
            }
        } else if(command.equals("close")){
            message=null;
        } else if(command.equals("startgame")){
            isFinished=false;
            list=new ArrayList<Character>();
            failedAttempts=10;
            currentWord=getRandomWord().toLowerCase();
            System.out.println("Server: Selected word = "+currentWord);
            message=constructWordMessage(currentWord, list);
        }
        return message;
    }
    
    private boolean containsList(String word, ArrayList<Character> list){
        for(char c : word.toCharArray()){
            if(!list.contains(c))
                return false;
        }
        return true;
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
    
    private String getHiddenWord(String word, ArrayList<Character> guessedLetters){
        StringBuilder hiddenWord=new StringBuilder();
        for(int i=0;i<word.length();i++){
            hiddenWord.append(guessedLetters.contains(word.charAt(i))?word.charAt(i):'-');
        }
        
        return hiddenWord.toString();
    }
    
    private String getRandomWord(){
        File file=new File("C:\\Users\\David\\Downloads/words.txt");
        Scanner scanner;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        Random r = new Random();
        int Low = 0;
        int High = 25143;
        int R = r.nextInt(High-Low) + Low;
        for(int i=0;i<R;i++)
            scanner.next();
        
        String selectedWord=scanner.next();
        return selectedWord;
    }
}
