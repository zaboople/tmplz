package org.tmotte.tmplz.parse.dum;
import org.tmotte.tmplz.parse.Types;
import java.util.List;
import java.util.ArrayList;

public final class NodeSection implements Node {
  private String name=null;
  private final NodeList nodes;
  private boolean removed=false;
  private List<NodeSection> shown=null;
  
  public NodeSection(String name, NodeList nodes){
    this.name=name;
    this.nodes=nodes;
  }
  public NodeSection(String name){
    this(name, new NodeList());
  }
  public Node cloneSelf(){
    return duplicate();
  }
  public NodeSection duplicate(){
    NodeSection n=new NodeSection(name, nodes.duplicate());
    return n;
  }
  public List<NodeSection> getShown(){
    return shown;
  }
  public NodeSection show(){
    if (shown==null)
      shown=new ArrayList<NodeSection>();
    NodeSection s=duplicate();
    boolean isFirst=shown.size()==0;
    NodeList newNodes=s.getNodes();
    for (int i=0; i<newNodes.size(); i++) {
      Node node=newNodes.get(i);
      if (node.getType()==Types.BETWEEN){
        newNodes.remove(i);
        if (isFirst)
          i--;
        else {
          NodeBetween nb=(NodeBetween)node;
          nb.makeVisible();
          i=newNodes.add(i, nb.getNodes())+1;
        }
      }
    }
    shown.add(s);
    return s;
  }
  public void resetShown() {
    shown=null;
  }
  public void remove() {
    removed=true;
  }
  public boolean isRemoved() {
    return removed;
  }
  public String getName(){
    return this.name;
  }
  public void setName(String name) {
    this.name=name;
  }
  public NodeList getNodes(){
    return nodes;
  }
  public int getType(){
    return Types.SECTION;
  }
  public String toString(){
    return getClass().getName().replace("org.tmotte.tmplz.parse.dum", "")
      +":"+getName()
      +":"+nodes+"";
  }
}