package org.tmotte.tmplz.parse.dum;
import java.util.List;
import org.tmotte.tmplz.parse.Types;
import org.tmotte.tmplz.parse.tokenize.TokenShow;

public class NodeShow implements Node {
  private List<String> names;
  private NodeList nodes=new NodeList();
  private TokenShow originalToken;
  private boolean found=false;
  
  public NodeShow(List<String> names, TokenShow originalToken) {
    this.names=names;
    this.originalToken=originalToken;
  }
  public Node cloneSelf(){
    NodeShow f=new NodeShow(names, originalToken);
    f.found=found;
    f.nodes=nodes.duplicate();
    return f;
  }
  public void setFound() {
    found=true;
  }
  public boolean getFound() {
    return found;
  }
  public List<String> getNames(){
    return names;
  }
  public TokenShow getOriginalToken() {
    return originalToken;
  }
  public NodeList getNodes(){
    return nodes;
  }
  public int getType(){
    return Types.SHOW;
  }
  public String toString(){
    return getClass().getName().replace("org.tmotte.tmplz.parse.dum.", "")
      +": names="+getNames()
      +": nodes="+nodes+"";
  }
}
