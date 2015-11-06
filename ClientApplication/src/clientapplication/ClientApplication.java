/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientapplication;

import clientapplication.Views.ClientPanel;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 *
 * @author David
 */
public class ClientApplication {

    private static ClientPanel clientPanel=new ClientPanel();
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        JFrame frame=new JFrame("ClientApplication");
        frame.setContentPane(clientPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        // A change
    }
    
}
