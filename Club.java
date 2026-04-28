import java.util.Scanner ;
//sham is hear
public class Club {
  //Attribute
   private String clubName ; 
   private  Committee [] committees ; 
   private Event [] events ; 
   private int numOfCommittees ; 
   private int numOfEvents ;
//constructor
   public Club (String name  ) {
      clubName=name;
      committees=new Committee [10] ; 
      events = new Event [10] ; 
      numOfCommittees=0;
      numOfEvents=0;
   }
//add committee
   public boolean addCommittee ( Committee c ) {
      if ( numOfCommittees < events.length ) {
         committees[numOfCommittees++] = new Committee (c) ; 
         return true ; 
      }
      return false ; 
   } 
//add event
   public boolean addEvent ( Event e ) { 
      if (numOfEvents < events.length ) {
         events[numOfEvents++]=new Event (e) ; 
         return true ; 
      } 
      return false ; 
   } 

// find committee method 
   public Committee findCommittee(String name ){
      for(int i=0;i<numOfCommittees;i++){
         if(committees[i].getCommName().equalsIgnoreCase(name)){
         
            return committees[i]; 
         }
      }
   
      return null;
   }
//get event
   public Event getEvent (String n ) {
      for ( int i = 0 ; i <numOfEvents ; i ++ ) {
         if ( events[i].getName().equals(n) ) 
            return events[i] ; 
      }
      System.out.println("Event not found");
      return null ; 
   }




             

}


