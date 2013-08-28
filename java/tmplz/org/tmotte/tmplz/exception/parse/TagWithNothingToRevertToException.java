package org.tmotte.tmplz.exception.parse;
import org.tmotte.common.text.StringChunker;
import org.tmotte.tmplz.parse.tokenize.DocumentTokenizer; 

/**
 * Thrown when a <code>TagWith Revert</code> appears without TagWith having first been used to change tagging options.
 */
public class TagWithNothingToRevertToException extends TemplateContentException {
  public TagWithNothingToRevertToException(StringChunker upTo){
    super(DocumentTokenizer.nameTagWith+DocumentTokenizer.nameTagWithRevert+": already on default, nothing to revert to.", upTo);
  }
}