package org.tmotte.tmplz.parse.dum;
import org.tmotte.tmplz.parse.Types;
import java.util.List;
import java.util.ArrayList;

public final class NodeBetween implements Node {
  private final NodeList nodes;
  private boolean visible;
  
  public NodeBetween(NodeList nodes){
    this.nodes=nodes;
  }
  public void makeVisible() {
    this.visible=true;
  }
  public boolean getVisible() {
    return visible;
  }
  public Node cloneSelf(){
    return this;
  }
  public NodeList getNodes(){
    return nodes;
  }
  public int getType(){
    return Types.BETWEEN;
  }
  public String toString(){
    return getClass().getName().replace("org.tmotte.tmplz.parse.dum", "")
      +":"+nodes+"";
  }
}