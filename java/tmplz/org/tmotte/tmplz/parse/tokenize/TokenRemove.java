package org.tmotte.tmplz.parse.tokenize;
import java.util.List;
import org.tmotte.tmplz.parse.Types;
import org.tmotte.tmplz.parse.dum.NodeRemove;

public abstract class TokenRemove extends AbstractToken {

  private final List<String> toRemove;
  private final boolean isSlot, isSection;
  
  public TokenRemove(
      List<String> toRemove, 
      boolean isSlot, 
      boolean isSection, 
      OriginalTag orig
    ){
    this.toRemove=toRemove;
    this.isSlot=isSlot;
    this.isSection=isSection;
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
  public List<String> getToRemove(){
    return toRemove;
  }
  public int getType(){ 
    return Types.REMOVE;
  }

}