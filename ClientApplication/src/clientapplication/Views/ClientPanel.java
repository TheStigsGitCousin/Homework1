/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package clientapplication.Views;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author David
 */
public class ClientPanel extends Panel implements ActionListener {
    // Game components
    private JLabel currentGuessLabel=new JLabel();
    private JTextField guessTextField=new JTextField(10);
    private JButton guessButton=new JButton("Guess");
    // Connection components
    private JTextField ipTextField=new JTextField(10);
    private JTextField portTextField=new JTextField(10);
    private JButton connectButton=new JButton("Connect");
    public ClientPanel(){
        setLayout(new BorderLayout());
        constructComponents();
    }
    
    private void constructComponents(){
        JPanel connectionPanel=new JPanel();
        connectionPanel.setLayout(new FlowLayout());
        connectionPanel.add(new JLabel("ip-adress"));
        connectionPanel.add(ipTextField);
        connectionPanel.add(new JLabel("port"));
        connectionPanel.add(portTextField);
        connectionPanel.add(connectButton);
        add(connectionPanel, BorderLayout.NORTH);
        
        guessButton.addActionListener(this);
        guessTextField.addActionListener(this);
        JPanel gamePanel=new JPanel();
        gamePanel.setLayout(new BorderLayout());
        JPanel guessPanel=new JPanel();
        guessPanel.setLayout(new FlowLayout());
        guessPanel.add(new JLabel("message"));
        guessPanel.add(guessTextField);
        guessPanel.add(guessButton);
        gamePanel.add(guessPanel, BorderLayout.NORTH);
        gamePanel.add(currentGuessLabel, BorderLayout.CENTER);
        add(gamePanel, BorderLayout.CENTER);
    }

    public void setStatusMessage(String message){
        // statusMessageTextField.setText(message);
    }
    
    public void messageReceived(String message){
        // currentGameTextField.setText(message);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
