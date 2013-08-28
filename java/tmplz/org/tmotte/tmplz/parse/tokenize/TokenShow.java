package org.tmotte.tmplz.parse.tokenize;
import java.util.List;
import org.tmotte.tmplz.parse.Types;

public class TokenShow extends AbstractToken {
  private List<String> names;
  private boolean selfClosing=false;
  
  public TokenShow(List<String> names, boolean selfClosing, OriginalTag orig){
    this.names=names;
    this.selfClosing=selfClosing;
    super.setOriginalTag(orig);
  }
  public List<String> getNames(){
    return this.names;
  }
  public boolean closes(TokenShow other) {
    if (selfClosing)
      return false;
    if (names==null)
      return true;
    List<String> otherNames=other.getNames();
    boolean noMatch=
       (names!=null && otherNames==null) ||
       (names.size()!=otherNames.size());
    if (noMatch)
      return false;
    for (int i=0; i<names.size(); i++)
      if (!names.get(i).equals(otherNames.get(i)))
        return false;
    return true;
  }
  public boolean hasNames(){
    return this.names!=null;
  }
  public boolean isSelfClosing() {
    return selfClosing;
  }
  public int getType(){
    return Types.SHOW;
  }
  public String toString(){
    return getClass().getName().replace("org.tmotte.tmplz.parse.tokenize", "")
      +(names==null ?"" :":"+names);
  }
}