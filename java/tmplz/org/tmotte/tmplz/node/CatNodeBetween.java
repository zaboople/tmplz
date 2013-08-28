package org.tmotte.tmplz.node;
import org.tmotte.common.text.StringChunker;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/** Created for the case of a Between tag. */
public final class CatNodeBetween extends CatNode{
  final String text;
  private boolean visible=false;
  public CatNodeBetween(String text, boolean visible){    
    this.text=text;
    this.visible=visible;
  }
  protected CatNode cloneSelf(boolean forShow){
    if (!forShow)
      return this;
    return new CatNodeBetween(text, true);
  }

  /////////////////
  // Properties: //
  /////////////////
  
  public String getName(){
    return null;
  }

  ////////////////////
  // Output methods //
  ////////////////////
  
  public String toString(){    
    return visible ?text :"";
  }
  public void appendTo(Appendable writer) throws java.io.IOException{
    if (visible)
      writer.append(text);
  }
}
