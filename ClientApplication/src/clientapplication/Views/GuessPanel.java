/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package clientapplication.Views;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Panel;
import java.awt.event.ActionEvent;
/**
 *
 * @author David
 */
public class GuessPanel extends Panel {
    private JTextField guessTextField=new JTextField(10);
    private JButton guessButton=new JButton("Guess");
    
    public GuessPanel(){
        setLayout(new FlowLayout());
    }
    
    private void constructComponents(){
        guessButton.addActionListener((ActionEvent e)->{
            
        });
        add(guessTextField);
        add(guessButton);
    }
}
