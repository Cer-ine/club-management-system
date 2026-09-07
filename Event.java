import java.io.*;
public class Event implements Serializable ,Report {
  //Attribute
   private String eventName;
   private String date;
   private String location ;

//constructors
   public Event(String eventName, String date, String location){
      this.eventName=eventName;
      this.date=date;
      this.location=location;
   }

/*copy constracter*/
   public Event(Event obj){
      eventName=obj.eventName;
      date=obj.date;
      location=obj.location;
   }
//getters
   public String getName() {
      return eventName ; 
   }
/*calculate rating :if the event date is recent the rate will be 5 ,
when the even become old the rate will decrese and if the user inters the wrong input  it will return 0 */
   public double calculateRating(){
      if(date!= null && date.length()>=4){
         String year=date.substring(date.length()-4);
         int eventYear=Integer.parseInt(year);
         if(eventYear>2026)
            return 5;
         else if (eventYear==2026)
            return 4.5;
         else
            return 3;  
      }
      else 
         return 0;
   }

//(ovrride method) display report

   public String displayReport() {
    String info = "--- Event Details ---\n";
    info += "Event: " + this.eventName + "\n";
    info += "Date: " + this.date + "\n";
    info += "Location: " + this.location + "\n";
    info += "stars : " + calculateRating() + "\n";
    return info;
}


}