/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package rpspeerapplication;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 *
 * @author David
 */
public class Event {
    
    List<Consumer<Object>> eventListeners=new ArrayList<>();
    
    public void FireEvent(Object object){
        for(Consumer<Object> function : eventListeners){
            function.accept(object);
        }
    }
    
    public void AddEventHandler(Consumer<Object> function){
        eventListeners.add(function);
    }
    
    public void RemoveEventHandler(Consumer<Object> function){
        eventListeners.remove(function);
    }
}
