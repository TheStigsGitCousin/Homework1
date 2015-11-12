/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package rpspeerapplication;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 *
 * @author David
 */
public class ClientPanel extends Panel {
    // Game components
    private JLabel statusMessageLabel=new JLabel("");
    private JLabel currentGuessLabel=new JLabel("Start the game!");
    private JTextField guessTextField=new JTextField(10);
    private JButton guessButton=new JButton("Guess");
    // Connection components
    private JTextField hostTextField=new JTextField(10);
    private JTextField portTextField=new JTextField(10);
    private JButton connectButton=new JButton("Connect");
    
    ConnectionHandler connectionHandler=new ConnectionHandler();
    
    public ClientPanel(){
        setLayout(new BorderLayout());
        constructComponents();
        
        Random r = new Random();
        int Low = 5000;
        int High = 10000;
        int R = r.nextInt(High-Low) + Low;
        connectionHandler.LISTENING_PORT=R;
        hostTextField.setText(Integer.toString(R));
        (new Thread(connectionHandler)).start();
    }
    
    private void constructComponents(){
        // Panel with components for connection establishment
        hostTextField.addActionListener((ActionEvent e)->{ connect(); });
        connectButton.addActionListener((ActionEvent e)->{ connect(); });
        JPanel connectionPanel=new JPanel();
        connectionPanel.setLayout(new FlowLayout());
        connectionPanel.add(new JLabel("host"));
        connectionPanel.add(hostTextField);
        connectionPanel.add(new JLabel("port"));
        connectionPanel.add(portTextField);
        connectionPanel.add(connectButton);
        // Add connectionPanel to ClientPanel
        add(connectionPanel, BorderLayout.NORTH);
        
        guessButton.addActionListener((ActionEvent e)->{ guess(); });
//        guessButton.setEnabled(false);
        guessTextField.addActionListener((ActionEvent e)->{ guess(); });
//        guessTextField.setEnabled(false);
        JPanel gamePanel=new JPanel();
        gamePanel.setLayout(new BorderLayout());
        JPanel guessPanel=new JPanel();
        guessPanel.setLayout(new FlowLayout());
        guessPanel.add(new JLabel("guess"));
        guessPanel.add(guessTextField);
        guessPanel.add(guessButton);
        gamePanel.add(guessPanel, BorderLayout.NORTH);
        gamePanel.add(currentGuessLabel, BorderLayout.CENTER);
        add(gamePanel, BorderLayout.CENTER);
        add(statusMessageLabel, BorderLayout.SOUTH);
    }
    
    private void guess(){
        connectionHandler.addCommand(guessTextField.getText());
//        synchronized(connectionHandler){
//            connectionHandler.notify();
//        }
    }
    
    private void connect(){
        int port;
        try{
            port=Integer.parseInt(portTextField.getText());
        }catch(NumberFormatException e){
            port=8080;
        }
        connectionHandler.CONNECTING_PORT=port;
        connectionHandler.Connect();
    }
    
    public void connected(){
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                guessButton.setEnabled(true);
                guessTextField.setEnabled(true);
            }
        });
    }
    
    public void statusChanged(String statusMessage){
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                System.out.println("Client: Status = "+statusMessage);
                statusMessageLabel.setText(statusMessage);
            }
        });
    }
    
    public void messageReceived(String response){
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                System.out.println("Client: Response = "+response);
                currentGuessLabel.setText(response);
            }
        });
    }
}
