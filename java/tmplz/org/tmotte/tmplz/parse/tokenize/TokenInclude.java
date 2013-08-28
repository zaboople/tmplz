package org.tmotte.tmplz.parse.tokenize;
import org.tmotte.tmplz.parse.Types;

/** 
 * Represents an Include tag. These should always appear in pairs, optionally with TokenFillins, TokenShows and TokenStatics between them.
 */
public class TokenInclude extends AbstractToken {
  private String reference;
  private String sectionName;
  
  public TokenInclude(String reference, String sectionName, OriginalTag orig){
    this.reference=reference;
    this.sectionName=sectionName;
    setOriginalTag(orig);
  }
  public TokenInclude(OriginalTag orig){
    this(null, null, orig);
  }

    
  public String getReference(){
    return this.reference;
  }
  public boolean hasReference(){
    return this.reference!=null;
  }
  public String getSectionName(){
    return sectionName;
  }
  public boolean hasSectionName(){
    return this.sectionName!=null;
  }
  public int getType(){
    return Types.INCLUDE;
  }
  public String toString(){
    return getClass().getName().replace("org.tmotte.tmplz.parse.tokenize", "")
      +(reference==null   ?"": ":"+reference)
      +(sectionName==null ?"": ":"+sectionName);
  }
}