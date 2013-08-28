package org.tmotte.tmplz.parse.tokenize;
import java.util.regex.Pattern;
import org.tmotte.common.text.DelimitedString;

class DTOptions {

  DocumentTokenizer tokenizer;
  DTOptions previous;
  Pattern regexStartAnyTag;
  int startOfTagLen=-1;
  Delimiters delims;
  String selfClosingMarker="/";
    
  public DTOptions(DocumentTokenizer dt, String startOfTag, String endOfTag){
    this(dt, new Delimiters(startOfTag, endOfTag));
  }
  public DTOptions(DocumentTokenizer dt, Delimiters delims){
    this.tokenizer=dt;
    reset(delims);
  }
  public void reset(String startOfTag, String endOfTag) {
    reset(new Delimiters(startOfTag, endOfTag));
  }
  public void reset(Delimiters delims) {
    this.delims=delims;
    String strRegexStartAnyTag=
      "("
        +tokenizer.nameTagWithAlwaysWorks
        +"|"
        +new DelimitedString(DelimiterFixer.fix(delims.startOfTag)+"(", "|", ")")
           .add(tokenizer.nameSlot)
           .add(tokenizer.nameSection)
           .add(tokenizer.nameFillin)
           .add(tokenizer.nameShow)
           .add(tokenizer.nameInclude)
           .add(tokenizer.nameAttrSection)
           .add(tokenizer.nameTrim)
           .add(tokenizer.nameRename)
           .add(tokenizer.nameTagWith)
           .add(tokenizer.nameBetween)
           .add(tokenizer.nameRemove)
           .toString()
     +")";  
    this.regexStartAnyTag=Pattern.compile(strRegexStartAnyTag);
    this.startOfTagLen=delims.startOfTag.length();    
  }
} 

