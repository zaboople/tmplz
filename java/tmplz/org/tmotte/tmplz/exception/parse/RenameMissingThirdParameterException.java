package org.tmotte.tmplz.exception.parse;
import org.tmotte.common.text.StringChunker;
import org.tmotte.tmplz.parse.tokenize.DocumentTokenizer; 
import org.tmotte.tmplz.parse.tokenize.Delimiters; 

/**
 * Thrown when a Rename element doesn't have a third parameter. A third is needed when the first
 * specifies "Slot" or "Section". 
 */
public class RenameMissingThirdParameterException extends TemplateContentException {
  Delimiters dt;
  public RenameMissingThirdParameterException(Delimiters dt, StringChunker upTo){
    super(
      dt.startOfTag+DocumentTokenizer.nameRename+" needs a third parameter when the first specifies "
        +DocumentTokenizer.nameSlot+" or "+DocumentTokenizer.nameSection, 
      upTo
    );
    this.dt=dt;
  }
}