package org.tmotte.tmplz.parse.tokenize;
import java.util.List;
import org.tmotte.tmplz.parse.Types;

public class TokenBetween extends AbstractToken {
  public TokenBetween(OriginalTag orig){
    setOriginalTag(orig);
  }
  public int getType(){
    return Types.BETWEEN;
  }
  public String toString(){
    return getClass().getName().replace("org.tmotte.tmplz.parse.tokenize", "");
  }
}
