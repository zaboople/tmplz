package org.tmotte.tmplz.parse.dum;
import java.util.List;
import org.tmotte.tmplz.parse.Types;
import org.tmotte.tmplz.parse.tokenize.TokenFillin;

public class NodeFillin implements Node {
  private List<String> names;
  private NodeList nodes=new NodeList();
  private TokenFillin originalToken;
  private boolean found=false;
  
  public NodeFillin(List<String> names, TokenFillin originalToken){
    this.names=names;
    this.originalToken=originalToken;
  }
  public Node cloneSelf(){
    NodeFillin f=new NodeFillin(names, originalToken);
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
    return this.names;
  }
  public TokenFillin getOriginalToken() {
    return originalToken;
  }
  public NodeList getNodes(){
    return nodes;
  }
  public int getType(){
    return Types.FILLIN;
  }
  public String toString(){
    return getClass().getName().replace("org.tmotte.tmplz.parse.dum", "")
      +":"+names
      +":"+nodes+"";
  }
}