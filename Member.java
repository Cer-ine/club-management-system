import java.io.*;
public abstract class Member extends Person implements Report , Serializable {
  //Attribute
   private int joinYear;
   protected boolean isActive;
  //Constructor

   public Member(int joinYear, boolean isActive, String id, String name) {
      super(id, name);
      this.joinYear = joinYear;
      this.isActive = isActive;
   }
  //abstarct method 
   public abstract double calculateReward();
   
  //Ovrrid toString method
   public String toString() {
      return "Name: "+ this.getName()+".\n ID: "+this.getId()+ ".\n Join Year: "+joinYear+ ".\n is Active?: "+isActive ;
   }
   
  //setter and getters methods
   public int getJoinYear() {
      return joinYear;
   }

   public void setJoinYear(int joinYear) {
      this.joinYear = joinYear;
   }

   public boolean isIsActive() {
      return isActive;
   }

   public void setIsActive(boolean isActive) {
      this.isActive = isActive;
   }
  
   
   //ovrrid method 
   public String displayReport(){
      return toString();
   }
}
