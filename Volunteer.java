import java.io.*;
public class Volunteer  extends Member implements Serializable {
//Attribute
   private int volunteerHours;
//Constructor
   public Volunteer(int volunteerHours, int joinYear, boolean isActive, String id, String name) {
      super(joinYear, isActive, id, name);
      this.volunteerHours = volunteerHours;
   }
//Override abstract method
   
   public double calculateReward(){
      if(!isActive){
         return 0;}
    
      double hoursPoint;    
      if( volunteerHours <25)
         hoursPoint= 5.0;             
      else
         hoursPoint=7.0;
         
      return volunteerHours * hoursPoint;
   }
 //Override toString method
   public String toString(){
      return  "*VOLUNTEER*\n "+super.toString() + ".\n Number of hours: "+ volunteerHours +"\n";
    
   }
   public void addHours(int h){
   if (h<0)                                                                       
    throw new IllegalArgumentException("Hours canot be negative !");            
    
      volunteerHours=volunteerHours+h;
      System.out.println("Hours added successfully");
   }
//setter and getter methods
   public int getVolunteerHours() {
      return volunteerHours;
   }

   public void setVolunteerHours(int volunteerHours) {
      this.volunteerHours = volunteerHours;
   }

     
}
