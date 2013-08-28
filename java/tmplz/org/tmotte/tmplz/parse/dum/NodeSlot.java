package org.tmotte.tmplz.parse.dum;
import org.tmotte.tmplz.parse.Types;

public final class NodeSlot implements Node {
  private String name;
  public NodeSlot(String name){
    this.name=name;
  }
  public Node cloneSelf(){
    return new NodeSlot(name);
  }
  public String getName(){
    return name;
  }
  /** Necessary because of Rename tag. */
  public void setName(String name) {
    this.name=name;
  }
  public int getType(){
    return Types.SLOT;
  }
  public String toString(){
    return getClass().getName().replace("org.tmotte.tmplz.parse.dum", "")
      +":"+getName();
  }  
}