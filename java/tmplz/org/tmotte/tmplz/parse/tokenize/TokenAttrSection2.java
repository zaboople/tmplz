package org.tmotte.tmplz.parse.tokenize;
import org.tmotte.tmplz.exception.InternalException;
import org.tmotte.tmplz.parse.Types;

/** 
 * Denotes an AttrSection tag. 
 */
public class TokenAttrSection2 extends AbstractToken {
  private String sectionName;
  private String attrName;

  public TokenAttrSection2(String sectionName, String attrName, OriginalTag orig){
    this.sectionName=sectionName;
    this.attrName=attrName;
    setOriginalTag(orig);
  }
  public String getSectionName(){
    return sectionName;
  }
  public String getAttributeName() {
    return attrName;
  }
  public int getType(){
    return Types.ATTR_SECTION_2;
  }
  public String toString(){
    return getClass().getName().replace("org.tmotte.tmplz.parse.tokenize", "")
      +":"+getSectionName()
      +":"+getAttributeName();
  }  
}
