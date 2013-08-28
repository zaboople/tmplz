package org.tmotte.tmplz.parse.dum;
import org.tmotte.tmplz.parse.Types;
import org.tmotte.tmplz.parse.tokenize.TokenRename;
import org.tmotte.tmplz.parse.tokenize.OriginalTag;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.List;


public class NodeRename extends TokenRename implements Node {
  private boolean found=false;
  
  public NodeRename(List<String> toReplace, String replaceWith, boolean isSlot, boolean isSection, String originalToReplace, OriginalTag orig){
    super(toReplace, replaceWith, isSlot, isSection, originalToReplace, orig);
  }
  public Node cloneSelf(){
    return this;
  }
  public TokenRename getOriginalToken() {
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
      +":"+(isSlot()?"Slot ":"Section ")+getToReplace()+"-"+getReplaceWith();
  }   
}
