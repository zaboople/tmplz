package org.tmotte.tmplz.parse.tokenize;
import java.util.regex.Pattern;

public final class Delimiters {

  public final String  startOfTag,
                       endOfTag;

  Pattern pEndOfTag;

  public Delimiters(String startOfTag, String endOfTag){
    this.startOfTag=startOfTag;
    this.endOfTag=endOfTag;
    pEndOfTag=Pattern.compile("(\"|"+DelimiterFixer.fix(endOfTag)+")");
  }
  public String toString() {
    return startOfTag+","+endOfTag;
  }
}