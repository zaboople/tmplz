package org.tmotte.tmplz.exception.parse;
import org.tmotte.tmplz.parse.tokenize.TokenAttrSection2; 
import org.tmotte.tmplz.parse.tokenize.Token; 
import org.tmotte.tmplz.parse.tokenize.TokenList; 

/**
 * Thrown when an AttrSection element references an attribute that does not exist.
 */
public class AttrSectionNoSuchAttributeException extends TemplateContentException {
  public final TokenAttrSection2 attrSection;
  public AttrSectionNoSuchAttributeException(TokenList tokens, TokenAttrSection2 offender){
    super(
      new TemplateErrorInfo(
        "No attribute \""+offender.getAttributeName()+"\" found", 
        tokens, 
        offender
      )
    );
    this.attrSection=offender;
  }
}