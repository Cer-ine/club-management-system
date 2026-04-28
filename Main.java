import java.util.Scanner;
// hihiihiih
public class Main{
   public static void main(String[]args){
      Scanner input=new Scanner (System.in);
      boolean valid=false;

      Club club =new Club ("University Club " );
   
      int choice ;
      String choosencommitteename=""  ; 
      Committee choosencommittee  ; 
      System.out.println("==================================================");
      System.out.println("  Welcome to the Club Management System");
      System.out.println("==================================================");
      do{
         System.out.println("=========== the Menu ===========");
         System.out.println("1- Add a new Committee ");
         System.out.println("2- Add a Member ");
         System.out.println("3- Remove a Member ");
         System.out.println("4- Display Member Report ");
         System.out.println("5- Calculate Rewards ");
         System.out.println("6- Edit Member Info ");
         System.out.println("7- Count Active Members  ");
         System.out.println("8- Add an Event  ");
         System.out.println("9- Display Event Report  ");
         System.out.println("10- Exit ");
         System.out.println("==================================");
         System.out.println(" Enter your choice: ");
         choice= input.nextInt();
         input.nextLine();
      
         switch(choice){
         //Add a new Committee  
            case 1:
               System.out.println("Enter committee name : ");
               String commName= input.nextLine();
               if(club.findCommittee(commName) !=null){
                  System.out.println("the committee is already exist!");
                  break;}
               System.out.println("Enter committee size  : ");
               int size = input.nextInt();
               input.nextLine();
            
               Committee committee=new Committee(commName,size);
               if(club.addCommittee(committee))
                  System.out.println("Committee added successfully. ");
               else 
                  System.out.println("Failed to add committee. ");
               break;
         
         // Add a Member 
            case 2:
               System.out.println("Enter committee name to add member to : ");
               choosencommitteename= input.next();
               choosencommittee=club.findCommittee(choosencommitteename);
            
               if(choosencommittee==null){
                  System.out.println("Committee not found . ");
                  break;
               }
            
               System.out.println("choose member type : \n 1- Board Member . \n 2- Volunteer . ");
               int type = input.nextInt();
               input.nextLine();
            
               System.out.println("Enter member ID : ");
               String id = input.nextLine();
            
               System.out.println("Enter member name : ");
               String name = input.nextLine();
            
            
               System.out.println("Enter join year : ");
               int year = input.nextInt();
               //input.nextLine();
            
            
               System.out.println("is active (true/false) :");
               boolean active = input.nextBoolean();
               input.nextLine();
            
               Member member=null;
               if(type==1){
               
                  String position = "";
                  int posChoice;
               
               
                  do {
                     System.out.println("Choose Board Member position:");
                     System.out.println("1- Leader");
                     System.out.println("2- Assistant");
                     System.out.println("3- Coordinator");
                     System.out.print("Enter your choice (1-3): ");
                  
                     posChoice = input.nextInt();
                     input.nextLine(); 
                  
                     if (posChoice == 1) {
                        position = "Leader";
                     } else if (posChoice == 2) {
                        position = "Assistant";
                     } else if (posChoice == 3) {
                        position = "Coordinator";
                     } else {
                        System.out.println("Invalid choice! Please enter 1, 2, or 3.\n");
                     }
                  } while (posChoice < 1 || posChoice > 3); 
               
               
                  member = new BoardMember(position, year, active, id, name);
               }
               else if(type==2){
               
                  System.out.println("Enter volunteer hours :");
                  int hours = input.nextInt();
                  input.nextLine();
               
                  member=new Volunteer(hours,year,active,id,name);
               
               }
               else{
                  System.out.println("Invalid type.");
                  break;
               
               
               }
            
               if(choosencommittee.addMember(member))
                  System.out.println("Member added successfully.");
               else
                  System.out.println("Cannot add member.");
            
            
               break;
         
         
         //Remove a Member 
            case 3:
               System.out.println("Enter committee name to Remove member to : ");
               choosencommitteename= input.nextLine();
               choosencommittee=club.findCommittee(choosencommitteename);
            
               if(choosencommittee!=null){
                  System.out.println("Enter member ID to remove : ");
                  String removeID= input.next();
               
                  if(choosencommittee.removeMember(removeID))
                     System.out.println("Member removed successfully . ");
                  else 
                     System.out.println("Member not found . ");
               }
               else 
                  System.out.println("Committee not found . ");
               break;
         
         
         
         //display report of member
            case 4: 
               {
                  System.out.println("Enter committee name of the member : ");
                  choosencommitteename= input.nextLine();
                  choosencommittee=club.findCommittee(choosencommitteename);
               
                  if(choosencommittee!=null){
                     System.out.println("Enter member ID : ");
                     String ID= input.next();
                     Member m = choosencommittee.getMember(choosencommittee.searchMember(ID)) ; 
                     if (m != null )
                        m.displayReport() ; 
                  }
                  else 
                     System.out.println("Committee not found . ");
               
               }
               break;
         
         //calculate rewards
            case 5:
               System.out.println("Enter committee name to calculate rewards: ");
               choosencommitteename = input.nextLine();
               choosencommittee = club.findCommittee(choosencommitteename);
            
               if(choosencommittee != null) {
                  if(choosencommittee.getNumOfMembers() == 0) {
                     System.out.println("No members in this committee.");
                  } else {
                     System.out.println("=== Rewards Report ===");
                     for(int i = 0; i < choosencommittee.getNumOfMembers(); i++) {
                        Member m = choosencommittee.getMember(i);
                        System.out.println("Member: " + m.getName() + " | Reward: " + m.calculateReward());
                     }
                  }
               } else {
                  System.out.println("Committee not found.");
               }
               break;
         
         
         //edit member
            case 6:
               System.out.println("Enter committee name of the member to edit: ");
               choosencommitteename = input.nextLine();
               choosencommittee = club.findCommittee(choosencommitteename);
            
               if (choosencommittee != null) {
                  System.out.println("Enter member ID to edit: ");
                  String editID = input.nextLine();
               
                  int memberIndex = choosencommittee.searchMember(editID);
               
                  if (memberIndex != -1) {
                     Member mToEdit = choosencommittee.getMember(memberIndex);
                     System.out.println("1- Edit Name\n2- Edit Active Status");
                  
                     if (mToEdit instanceof BoardMember) {
                        System.out.println("3- Edit Position");
                     } else if (mToEdit instanceof Volunteer) {
                        System.out.println("3- Add Volunteer Hours");
                     }
                  
                     System.out.print("Enter your choice: ");
                     int editChoice = input.nextInt();
                     input.nextLine();
                  
                     if (editChoice == 1) {
                        System.out.print("Enter the new name: ");
                        String newName = input.nextLine();
                        mToEdit.setName(newName);
                        System.out.println("Name updated successfully!");
                     } else if (editChoice == 2) {
                        System.out.print("Is the member currently active? (true/false): ");
                        boolean newStatus = input.nextBoolean();
                        input.nextLine();
                                
                        mToEdit.setIsActive(newStatus); 
                        System.out.println("Status updated successfully!");
                     } else if (editChoice == 3) {
                        if (mToEdit instanceof BoardMember) {
                           BoardMember bm = (BoardMember) mToEdit;
                           String newPosition = "";
                           int posChoice;
                        
                           do {
                              System.out.println("Choose new Position:\n1- Leader\n2- Assistant\n3- Coordinator");
                              System.out.print("Enter choice (1-3): ");
                              posChoice = input.nextInt();
                              input.nextLine();
                           
                              if (posChoice == 1) newPosition = "Leader";
                              else if (posChoice == 2) newPosition = "Assistant";
                              else if (posChoice == 3) newPosition = "Coordinator";
                              else System.out.println("Invalid choice!");
                           } while (posChoice < 1 || posChoice > 3);
                        
                           bm.setPosition(newPosition);
                           System.out.println("Position updated successfully!");
                        
                        } else if (mToEdit instanceof Volunteer) {
                            Volunteer vol = (Volunteer) mToEdit;
                            System.out.print("Enter the number of hours to add: ");
                           while(!valid){
                           int newHours = input.nextInt();
                           input.nextLine();
                        try{                                                                   
                           vol.addHours(newHours);
                           break;
                          }
                          catch(IllegalArgumentException e){
                          System.out.println("Error :"+e.getMessage());
                          System.out.print("Please enter a vaild number :");

                          }  
                          }   
                                    
                        }
                     } else {
                        System.out.println("Invalid choice.");
                     }
                  } else {
                     System.out.println("Member ID not found in this committee.");
                  }
               } else {
                  System.out.println("Committee not found.");
               }
               break;
         
         //count active members 
            case 7:
               System.out.println("Enter committee name to count active members: ");
               choosencommitteename = input.nextLine();
               choosencommittee = club.findCommittee(choosencommitteename);
            
               if(choosencommittee != null) {
               
                  int activeCount = choosencommittee.countActiveMembers(0);
                  System.out.println("Total Active Members in '" + choosencommitteename + "' is: " + activeCount);
               } else {
                  System.out.println("Committee not found.");
               }
               break;
         
         
         //add event
            case 8: 
               {
                  System.out.print("enter event name:") ; 
                  String eventname=input.next();
                  System.out.print("enter event date:") ; 
                  String eventdate=input.next();
                  System.out.print("enter event location:") ; 
                  String eventloc=input.next();
                  Event e = new Event (eventname, eventdate , eventloc ) ; 
                  if ( club.addEvent(e) )
                     System.out.println("Event added succesfully!") ; 
                  else 
                     System.out.println("failed to add the Event ") ; 
               }
               break;
         
         //display event
            case 9: 
               { 
                  System.out.print("enter event name : ") ; 
                  String n = input.next() ; 
                  Event event = club.getEvent(n) ; 
                  if ( event != null ) event.displayReport() ; 
               }
               break;
         
         
         //exit
            case 10:
               System.out.println("thank you");
               break;
         }
      
      
      }while(choice !=10);
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   }















}