import java.util.Scanner ;
import java.io.*;
public class Club implements Serializable {
  //Attribute
   private String clubName ; 
   private  List<Committee> committees ; 
   private List<Event> events ;  
   
//constructor
   public Club (String name  ) {
      clubName=name;
      committees=new List<Committee>() ; 
      events = new List<Event> () ; 
      
   }
//add committee
   public boolean addCommittee ( Committee c ) {
      
         committees.insertAtBack(c);  
         
         return true ; 
   
   } 
//add event
   public boolean addEvent ( Event e ) { 
      events.insertAtBack(e);  
         
         return true ;
   } 

// find committee method 
   public Committee findCommittee(String name ){
   Node <Committee> current= committees.getFirstNode() ; 
      while (current != null ) {
         if(current.getData().getCommName().equalsIgnoreCase(name)){
            return current.getData(); 
         }
         current=current.nextNode ;
      }
   
      return null;
   }
//get event
   public Event getEvent (String n ) {
       Node <Event> current= events.getFirstNode() ; 
      while (current != null ) {
         if(current.getData().getName().equals(n)){
            return current.getData(); 
         }
         current=current.nextNode ;
      }
      System.out.println("Event not found");
      return null ; 
   }




             

}


