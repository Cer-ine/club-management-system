public class Committee{
  //Attribute
   private String commName;
   private Member members[];
   private int numOfMembers;

   public Committee(String co,int size){
      commName=co;
      members=new Member[size];
      numOfMembers=0;
   }
/*copy constracter*/
   public Committee(Committee obj){
      commName=obj.commName;
      members=new Member[obj.members.length];
      numOfMembers=obj.numOfMembers;
      for(int i = 0;i<numOfMembers;i++)
      {members[i]=obj.members[i];}
    
   }
//getters
   public String getCommName() { 
      return commName ; 
   }
   public int getNumOfMembers(){
      return numOfMembers;
   }
//add member
   public boolean addMember(Member m){
      if(numOfMembers>=members.length)
         return false;
      else{
         if (searchMember(m.getId())!=-1){
            System.out.println("a member with this ID already exist!");
            return false;}
         else
            members[numOfMembers++]=m;
         return true;
      }}
//remove member
   public boolean removeMember(String id){
      int indexMember=searchMember(id);
      if(indexMember!=-1){
         members[indexMember]=members[numOfMembers-1];
         members[numOfMembers-1]=null;
         numOfMembers--;
         return true ;}
      else 
         return false;
   }

//return the index of the member
   public int searchMember(String id ){
      for(int i=0;i<numOfMembers;i++){
         if (id.equals(members[i].getId()))
            return i ;
      }
      return -1 ; 
   }
//get member
   public Member getMember(int i){
      if ( i<0 || i > members.length) {
         System.out.println("Member not found " ) ;
         return null ; 
      }
   
      return members[i] ; 
   }
//count active members

   public int countActiveMembers(int index){
      if(index>=numOfMembers)
         return 0;
   
      if(members[index].isIsActive()==true)
         return 1+countActiveMembers(index+1);
      else
         return 0+ countActiveMembers(index+1);
   }





















}