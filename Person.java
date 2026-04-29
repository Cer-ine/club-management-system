import java.io.*;
public class Person implements Serializable  {
  //Attribute
   private String id;
   private String name;
//constructor
   public Person(String id, String name ){
      this.id= id;
      this.name=name;
   }
   
//setter and getter methods
   public void setId(String id) {
      this.id = id;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getId() {
      return id;
   }

   public String getName() {
      return name;
   }
    
}
