package org.tmotte.tmplz.parse.dum;
import org.tmotte.tmplz.parse.tokenize.TokenInclude;
import org.tmotte.tmplz.parse.Types;
import org.tmotte.tmplz.exception.InternalException;

public class NodeInclude implements Node {
  private String path;
  private String sectionName;
  private NodeList nodes;
  private TokenInclude origToken;
  private boolean doAggressiveTrim;
  
  public NodeInclude(String path, String sectionName, NodeList nodes, TokenInclude origToken, boolean doAggressiveTrim){
    this.path=path;
    this.sectionName=sectionName;
    if (nodes==null)
      throw new InternalException("Null NodeList");
    this.nodes=nodes;
    this.origToken=origToken;
    this.doAggressiveTrim=doAggressiveTrim;
  }
  public TokenInclude getOriginalToken(){
    return origToken;
  }
  public boolean getDoAggressiveTrim() {
    return doAggressiveTrim;
  }
  public Node cloneSelf(){
    NodeInclude f=new NodeInclude(path, sectionName, nodes.duplicate(), origToken, doAggressiveTrim);
    f.nodes=nodes.duplicate();
    return f;
  }

  /** 
   * FIXLATER remove.
   */
  public String getReference(){
    return this.path;
  }
  public String getPath() {
    return this.path;
  }
  public String getSectionName(){
    return sectionName;
  }
  public NodeList getNodes(){
    return nodes;
  }
  public int getType(){
    return Types.INCLUDE;
  }
  public String toString(){
    return getClass().getName().replace("org.tmotte.tmplz.parse.dum", "")
      +":"+getReference()
      +":"+nodes+"";
  }
}