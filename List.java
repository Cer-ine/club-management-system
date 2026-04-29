import java.io.* ; 
//class List from the lecture but we added some methods 
public class List<V> implements Serializable {
   private Node<V> firstNode;
   private Node<V> lastNode;
   private String name;

   public List()
   {
      this("list");
   }

   public List(String listName)
   {
      name = listName;
      firstNode = lastNode = null;
   }

   public void insertAtFront(V insertItem)
   {
      if (isEmpty())
         firstNode = lastNode = new Node<V>(insertItem);
      else
         firstNode = new Node<V>(insertItem, firstNode);
   }

   public void insertAtBack(V insertItem)
   {
      if (isEmpty())
         firstNode = lastNode = new Node<V>(insertItem);
      else
         lastNode = lastNode.nextNode = new Node<V>(insertItem);
   }

   public V removeFromFront()
   {
      V removedItem = firstNode.data;
   
      if (firstNode == lastNode)
         firstNode = lastNode = null;
      else
         firstNode = firstNode.nextNode;
   
      return removedItem;
   }

   public V getFromFront()
   {
      return firstNode.data;
   }

   public V removeFromBack()
   {
      V removedItem = lastNode.data;
   
      if (firstNode == lastNode)
         firstNode = lastNode = null;
      else
      {
         Node<V> current = firstNode;
      
         while (current.nextNode != lastNode)
            current = current.nextNode;
      
         lastNode = current;
         current.nextNode = null;
      }
   
      return removedItem;
   }
   //remove a node with the given data at any position
   public boolean remove(V value) {
      Node<V> current = firstNode;
      Node<V> prev = null;
   
      while (current != null) {
      
         if (current.data.equals(value)) {
         
            // removing first node
            if (prev == null) {
               firstNode = current.nextNode;
            } 
            // removing middle or last
            else {
               prev.nextNode = current.nextNode;
            }
         
            // update lastNode if needed
            if (current == lastNode) {
               lastNode = prev;
            }
         
            return true;
         }
      
         prev = current;
         current = current.nextNode;
      }
   
      return false;
   }

   public boolean isEmpty()
   {
      return firstNode == null;
   }

   public void print()
   {
      if (isEmpty())
      {
         System.out.printf("Empty %s%n", name);
         return;
      }
   
      System.out.printf("The %s is: ", name);
      Node<V> current = firstNode;
   
      while (current != null)
      {
         System.out.printf("%s ", current.data);
         current = current.nextNode;
      }
   
      System.out.println("\n");
   }
   //return the firstnode
   public Node<V> getFirstNode () {
      return firstNode ; 
   }
} // end class List