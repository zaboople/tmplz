package org.tmotte.tmplz.parse.dum;
import org.tmotte.tmplz.parse.Types;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import org.tmotte.common.text.DelimitedString;

/** 
 * This acts as a list of Node objects. It isn't a Node in and of itself.
 */
public class NodeList {
  private List list=new LinkedList();
  
  public NodeList duplicate(){
    NodeList other=new NodeList();
    for (int i=0; i<size(); i++)
      other.add(get(i).cloneSelf());
    return other;
  }
  
  public void add(Node node){
    list.add(node);
  }
  public int size(){
    return list.size();
  }
  public Node get(int i){
    return (Node)list.get(i);
  }
  public void set(int i, Node node){
    list.set(i, node);
  }
  public void clear(){
    list.clear();
  }
  public void remove(int i){
    list.remove(i);
  }

  /**
   * This moves the node at position i forwards one, 
   * and replaces it with <pre>node</pre>
   */
  public void add(int i, Node node){
    list.add(i, node);
  }
  /**
   * This moves the node at position i forwards one, 
   * and replaces it with <pre>nodes</pre>
   * @return The new index of the last node added. Not the index of the 
   *         node after it, or before it, but it. So if a list of length=3
   *         is added, the return value should be i+3-1.
   */
  public int add(int i, NodeList nodes){
    int size=nodes.size();
    for (int m=0; m<size; m++)
      add(m+i, nodes.get(m));
    return size+i-1;
  }
  /**
   *  This calls remove(i), then add(i, nodes); The result returned
   *  is the result of the latter function.<br/>
   *  So x.replace(3, y) removes the item at position 3 of x, and
   *  inserts each node of y into x starting at position 3. If 
   *  x had a size() of 2, the result returned will be 4, which is
   *  the position of the last node inserted into y.
   */
  public int replace(int i, NodeList nodes) {
    remove(i);
    return add(i, nodes);
  }


  ///////////
  // TEST: //
  ///////////

  public String toString(){
    DelimitedString ds=new DelimitedString("\n[\n", ",\n", "\n]\n");
    for (int i=0; i<size(); i++)
      ds.add(get(i));
    return ds.toString();
  }
  
  public static void main(String[] args) {
    NodeList list=new NodeList();
    for (int i=0; i<8; i++)
      list.add(new NodeStatic(""+i));
    System.out.println(list); 
    
    NodeList list2=new NodeList();
    for (int i=0; i<3; i++)
      list2.add(new NodeStatic("2new"+i));
    
    list.remove(2);
    System.out.println(list.add(2, list2));
    System.out.println(list); 
  }
}
