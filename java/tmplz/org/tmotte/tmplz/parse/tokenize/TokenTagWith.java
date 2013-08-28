package org.tmotte.tmplz.parse.tokenize;
import org.tmotte.tmplz.parse.Types;

public final class TokenTagWith extends AbstractToken {
  private final Delimiters delimiters;
  public TokenTagWith(Delimiters d, OriginalTag oc) {
    this.delimiters=d;
    setOriginalTag(oc);
  }
  public Delimiters getDelimiters() {
    return delimiters;
  }
  public int getType() {
    return Types.TAG_WITH;  
  }
  public String toString() {
    return getClass().getName().replace("org.tmotte.tmplz.parse.tokenize", "")
      +delimiters;  
  }
}