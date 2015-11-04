/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package clientapplication.Views;

import java.awt.BorderLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 *
 * @author David
 */
public class ClientPanel extends Panel{
    private JLabel currentGuessLabel=new JLabel();
    private JTextField guessTextField=new JTextField(10);
    private JButton guessButton=new JButton("Guess");
    
    public ClientPanel(){
        setLayout(new BorderLayout());
    }
    
    private void constructComponents(){
        guessButton.addActionListener((ActionEvent e)->{
            
        });
        add(guessTextField);
        add(guessButton);
        add(currentGuessLabel, BorderLayout.CENTER);
    }
}
