package org.tmotte.tmplz.exception.parse;
import org.tmotte.common.text.StringChunker;
import org.tmotte.tmplz.parse.tokenize.DocumentTokenizer; 
import org.tmotte.tmplz.parse.tokenize.Delimiters; 

/**
 * Thrown when a Rename element doesn't have a second parameter.
 */
public class RenameMissingSecondParameterException extends TemplateContentException {
  Delimiters dt;
  public RenameMissingSecondParameterException(Delimiters dt, StringChunker upTo){
    super(dt.startOfTag+DocumentTokenizer.nameRename+" requires at least two parameters, separated by a space character", upTo);
    this.dt=dt;
  }
}