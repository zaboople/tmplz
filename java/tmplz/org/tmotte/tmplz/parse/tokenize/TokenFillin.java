package org.tmotte.tmplz.parse.tokenize;
import java.util.List;
import org.tmotte.tmplz.parse.Types;

/**
 * Represents a Fillin tag. These should always appear in pairs in a TokenList, with additional Tokens between them.
 */
public class TokenFillin extends AbstractToken {
  private List<String> names;
  
  public TokenFillin(List<String> names, OriginalTag  orig){
    this.names=names;
    setOriginalTag(orig);
  }
  
  public List<String> getNames(){
    return this.names;
  }
  public boolean hasNames(){
    return this.names!=null && this.names.size()>0;
  }
  public boolean sameNames(TokenFillin other) {
    List<String> otherNames=other.getNames();
    if (names==null && otherNames==null)
      return true;    
    boolean noMatch=
       (names==null && otherNames!=null) ||
       (names!=null && otherNames==null) ||
       (names.size()!=otherNames.size());
    if (noMatch)
      return false;
    for (int i=0; i<names.size(); i++)
      if (!names.get(i).equals(otherNames.get(i)))
        return false;
    return true;
  }
  
  public int getType(){
    return Types.FILLIN;
  }
  public String toString(){
    return getClass().getName().replace("org.tmotte.tmplz.parse.tokenize", "")
      +(names==null ?"" :":"+names);
  }
}
