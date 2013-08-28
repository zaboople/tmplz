package org.tmotte.tmplz.parse.tokenize;
import org.tmotte.tmplz.parse.Types;
import org.tmotte.tmplz.parse.dum.NodeStatic;

public abstract class TokenStatic extends AbstractToken {
  private String text;
  public TokenStatic(String text){
    super.setOriginalTag(new OriginalTag("", text, ""));
    this.text=text;
  }
  /** This gets invoked by auto-trim. It doesn't affect the value of getOriginalTag() */
  public void setText(String text) {
    this.text=text;
  }
  public String getText(){
    return text;
  }
  public int getType(){
    return Types.STATIC;
  }
}
