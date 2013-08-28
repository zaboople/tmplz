package org.tmotte.tmplz.parse.dum;
import org.tmotte.tmplz.parse.Types;
import org.tmotte.tmplz.parse.tokenize.TokenRemove;
import org.tmotte.tmplz.parse.tokenize.OriginalTag;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.List;


public class NodeRemove extends TokenRemove implements Node {
  private boolean found=false;

  public NodeRemove(List<String> toRemove, boolean isSlot, boolean isSection, OriginalTag orig){
    super(toRemove, isSlot, isSection, orig);
  }
  public Node cloneSelf(){
    return this;
  }
  public TokenRemove getOriginalToken() {
    return this;//AH tricky
  }
  public void setFound() {
    found=true;
  }
  public boolean getFound() {
    return found;
  }

  public String toString(){
    return getClass().getName().replace("org.tmotte.tmplz.parse.dum", "")
      +":"+(isSlot()?"Slot ":"Section ")+getToRemove();
  }   
}
