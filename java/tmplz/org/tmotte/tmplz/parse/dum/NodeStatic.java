package org.tmotte.tmplz.parse.dum;
import org.tmotte.tmplz.parse.Types;
import org.tmotte.tmplz.parse.tokenize.TokenStatic;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public final class NodeStatic extends TokenStatic implements Node {

  public static int MAX_DEBUG_LENGTH=50;
  //public static int MAX_DEBUG_LENGTH=Integer.MAX_VALUE;

  private boolean doAggressiveTrim;
  
  public NodeStatic(String text){
    super(text);
  }
  /** 
   * This returns the node itself, even though it is not technically immutable.
   * @return This node.
   */
  public Node cloneSelf(){
    return this;
  }

  /** I know, I know, I said it was immutable. I'm cheating, but it's ok. I think. */
  public void setDoAggressiveTrim(boolean doAggressiveTrim) {
    this.doAggressiveTrim=doAggressiveTrim;
  }
  public boolean getDoAggressiveTrim() {
    return doAggressiveTrim;
  }

  public String toString(){
    String t=getText();
    return getClass().getName().replace("org.tmotte.tmplz.parse.dum", "")
      +(t.length()>MAX_DEBUG_LENGTH ? t.substring(0, MAX_DEBUG_LENGTH)+"..." :t);
  }  
 
}
