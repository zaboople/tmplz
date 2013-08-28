package org.tmotte.tmplz.exception.parse;
import org.tmotte.tmplz.parse.tokenize.TokenList; 
import org.tmotte.tmplz.parse.dum.NodeInclude; 


/** 
 * Thrown when an Include tag specifies a section that does not exist in the included template. A Section does not have
 * to be specified, of course. This Exception may occur when the reference contains "space" characters, in which case
 * the Include should surround it with double quotes.
 */
public class IncludeReferencesMissingSectionException extends TemplateContentException {
  public IncludeReferencesMissingSectionException(NodeInclude includer, TokenList tokensIncludeCameFrom){
    super(
      new TemplateErrorInfo(
        "Include references nonexistent Section \""+includer.getSectionName()+"\" in template "+includer.getPath(), 
        tokensIncludeCameFrom, 
        includer.getOriginalToken()
      )
    );
  }
}