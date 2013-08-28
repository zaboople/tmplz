package org.tmotte.tmplz.node;
import org.tmotte.common.text.StringChunker;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/** Denotes any "free" text that is not part of a Tmplz tag.*/
public final class CatNodeStatic extends CatNode{
  public static int NOTRIM=0, TRIM=3;
  final String text;
  public CatNodeStatic(int trim, String text){    
    super();
    if (trim==TRIM)
      text=trim(text);
    this.text=text;
  }
  protected CatNode cloneSelf(boolean forShow){
    return this;
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
    return text;
  }
  public void appendTo(Appendable writer) throws java.io.IOException{
    writer.append(text);
  }

  ///////////
  // TRIM: //
  ///////////

  private static Pattern trimAggPattern=Pattern.compile("( |\r\n|\r|\n)+", Pattern.MULTILINE);
  private static String trim(String text) {
    Matcher matcher=trimAggPattern.matcher(text);
    text=matcher.replaceAll(" ");
    return text;
  }
  public static void main(String[] args) throws Exception {
    String s=org.tmotte.common.io.Loader.loadString(System.in);
    System.out.println(trim(s));
  }
}

