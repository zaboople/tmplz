package org.tmotte.tmplz.parse.tokenize;
import org.tmotte.tmplz.parse.Types;

/** 
 * Designates an attribute that is the target of an AttrSection. It should be in the position of the original attribute in the text template.
 * Therefore a TokenAttrTarget always appears somewhere after the TokenAttrSection that refers to it, always with at least a single TokenStatic 
 * in between (possibly numerous other Tokens in between as well).
 */
public class TokenAttrTarget2 extends AbstractToken {
  static OriginalTag allOrig=new OriginalTag("", "", "");
  static TokenAttrTarget2 closer=new TokenAttrTarget2();
  public static TokenAttrTarget2 getTargetClose() {
    return closer;
  }
  
  private TokenAttrSection2 attrSection;
  private boolean isClosing=false;
  
  private TokenAttrTarget2() {
    setOriginalTag(allOrig);
    isClosing=true;
  }
  public TokenAttrTarget2(TokenAttrSection2 attrSection){
    setOriginalTag(allOrig);
    this.attrSection=attrSection;
  }

  public String getSectionName(){
    return attrSection.getSectionName();
  }
  public TokenAttrSection2 getAttrSection() {
    return attrSection;
  }
  public int getType(){
    return Types.ATTR_TARGET_2;
  }
  public boolean isClosing() {
    return isClosing;
  }
  public String toString(){
    return getClass().getName().replace("org.tmotte.tmplz.parse.tokenize", "")
      +":"+(isClosing ?"<close>" :getSectionName());
  }  
}
