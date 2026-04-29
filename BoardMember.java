import java.io.*;
public class BoardMember extends Member implements Serializable {
 //Attribute
   private String position;
 //Constructor
   public BoardMember(String position, int joinYear, boolean isActive, String id, String name) {
      super(joinYear, isActive, id, name);
      this.position = position;
   }
 //Ovrride abstract method 
   
   public double calculateReward(){
      if(!isActive){
         return 0;}
         
      double basePoint=60;
      double experience= (2026-this.getJoinYear());
      double roleMultiplier;
      roleMultiplier = 
         switch (position.toLowerCase()) {
            case "leader" -> 3.0;
            case "assistant" -> 2.5;
            case "coordinator" -> 2.0;
            default -> 1.0;
         };
      return (basePoint*roleMultiplier )+( Math.pow(experience,2)*10);
   }
//Ovrride toString method
   public String toString(){
      return "*BOARD MEMBER*.\n Position: "+ position+ ".\n "+ super.toString()+ "\n";
   }
//setter and getter methods
   public String getPosition() {
      return position;
   }

   public void setPosition(String position) {
      this.position = position;
   }

    
}
