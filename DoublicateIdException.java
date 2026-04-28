public class DoublicateIdException extends Exception {
   public DoublicateIdException(String id){
      super("Error: the ID :"+ id+ " is already registered in this committee!");}


}
