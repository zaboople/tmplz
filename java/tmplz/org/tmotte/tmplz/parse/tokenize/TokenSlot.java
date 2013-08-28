package org.tmotte.tmplz.parse.tokenize;
import org.tmotte.tmplz.parse.Types;

public class TokenSlot extends AbstractToken {
  private String name;
  public TokenSlot(String name, OriginalTag o){
    this.name=name;
    setOriginalTag(o);
  }
  public String getName(){
    return name;
  }
  public boolean hasName(){
    return name!=null;
  }
  public int getType(){
    return Types.SLOT;
  }
  public String toString(){
    return getClass().getName().replace("org.tmotte.tmplz.parse.tokenize", "")
      +":"+getName();
  }  
}