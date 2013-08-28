package org.tmotte.tmplz.parse.tokenize;
import java.util.List;
import org.tmotte.tmplz.parse.Types;
import org.tmotte.tmplz.parse.dum.NodeRename;

public abstract class TokenRename extends AbstractToken {

  private final List<String> toReplace;
  private final String replaceWith;
  private final boolean isSlot, isSection;
  private final String originalToReplace;
  
  public TokenRename(
      List<String> toReplace, 
      String replaceWith, 
      boolean isSlot, 
      boolean isSection, 
      String originalToReplace,
      OriginalTag orig
    ){
    this.toReplace=toReplace;
    this.replaceWith=replaceWith;
    this.isSlot=isSlot;
    this.isSection=isSection;
    this.originalToReplace=originalToReplace;
    super.setOriginalTag(orig);
  }
  public boolean isSlot() {
    return isSlot;
  }
  public boolean isSection() {
    return isSection;
  }
  public boolean isUnknown() {
    return (!isSlot) && (!isSection);
  }
  public List<String> getToReplace(){
    return toReplace;
  }
  /** This is somewhat evil in that is uses the "." notation that really belongs elsewhere. */
  public String getOriginalToReplace() {
    return originalToReplace;
  }
  public String getReplaceWith(){
    return replaceWith;
  }
  public int getType(){ 
    return Types.RENAME;
  }

}