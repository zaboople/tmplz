package org.tmotte.tmplz.parse.tokenize;
import org.tmotte.tmplz.parse.Types;
import java.util.List;
import java.util.ArrayList;

public class TokenSection extends AbstractToken {
  private String name=null;

  public TokenSection(String name, OriginalTag o){
    this.name=name;
    setOriginalTag(o);
  }
  public TokenSection(){
  }
  public String getName(){
    return this.name;
  }
  public boolean hasName(){
    return name!=null;
  }
  public int getType(){
    return Types.SECTION;
  }
  public String toString(){
    return getClass().getName().replace("org.tmotte.tmplz.parse.tokenize", "")
      +(name==null ?"" :":"+getName());
  }
}