import java.io.*;
public class Committee implements Serializable{
  //Attribute
   private String commName;
   private List<Member> members;
   

   public Committee(String co){
      commName=co;
      members=new List<Member> ();
      
   }
/*copy constracter*/
   public Committee(Committee obj){
      commName=obj.commName;
      members=new List<Member> ();
      Node<Member> current = obj.members.getFirstNode();
        while (current != null) {
            this.members.insertAtBack(current.data); 
            current = current.nextNode;
        }
    }
    
//getters
   public String getCommName() { 
      return commName ; 
   }
   //get the list of members
   public List<Member> getMembers() {
    return members;
}
   
//add member
   public boolean addMember(Member m){
         if (searchMember(m.getId())!=null){
            System.out.println("a member with this ID already exist!");
            return false;}
         else
            members.insertAtBack(m) ;
         return true;
      }
//remove member
   public boolean removeMember(String id){
      if(searchMember(id) != null){
      if(members.remove(searchMember(id))) {
         return true ;}
         }
     return false ; 
   }

//return the member with the given ID
   public Member searchMember(String id ){
   Node <Member> current= members.getFirstNode() ; 
      while (current != null ) {
         if(id.equals(current.getData().getId())){
            return current.getData(); 
         }
         current=current.nextNode ;
      }
     
      return null ; 
   }

//count active members

    public int countActiveMembers() {
        int count = 0;
        Node<Member> current = members.getFirstNode();

        while (current != null) {
            if (current.data.isIsActive()) {
                count++;
            }
            current = current.nextNode;
        }

        return count;
    }


//method to check DuplicateID
public void checkDuplicateID(String id) throws DoublicateIdException{
if (searchMember(id ) != null){
throw new DoublicateIdException(id);}

}



















}