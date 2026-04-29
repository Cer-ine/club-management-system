import java.io.* ;
//class Node from the lecture
public class Node<T> implements Serializable 
{
    public T data;
    public Node<T> nextNode;

    public Node(T object)
    {
        this(object, null);
    }

    public Node(T object, Node<T> node)
    {
        data = object;
        nextNode = node;
    }

    public T getData()
    {
        return data;
    }

    public Node<T> getNext()
    {
        return nextNode;
    }
} // end class Node