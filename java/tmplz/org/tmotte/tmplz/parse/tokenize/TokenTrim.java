package org.tmotte.tmplz.parse.tokenize;
import org.tmotte.tmplz.parse.Types;
import org.tmotte.tmplz.parse.dum.NodeStatic;

public class TokenTrim  extends AbstractToken {
  public TokenTrim(OriginalTag oc) {
    setOriginalTag(oc);
  }
  public int getType(){
    return Types.TRIM;
  }
}
