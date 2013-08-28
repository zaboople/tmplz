package org.tmotte.tmplz.exception.parse;
import org.tmotte.tmplz.parse.tokenize.TokenAttrSection2; 
import org.tmotte.tmplz.parse.tokenize.TokenAttrTarget2; 
import org.tmotte.tmplz.parse.tokenize.DocumentTokenizer; 
import org.tmotte.tmplz.parse.tokenize.Token; 
import org.tmotte.tmplz.parse.tokenize.TokenList; 

/**
 * Thrown when an AttrSection element finds the attribute with the starting quote, but no end quote.
 */
public class AttrSectionNoEndQuoteException extends TemplateContentException {
  public final TokenAttrSection2 attrSection;
  public final TokenAttrTarget2 attrTarget;
  public AttrSectionNoEndQuoteException(TokenList tokens, TokenAttrSection2 attrSection, TokenAttrTarget2 attrTarget){
    super(
      new TemplateErrorInfo(
         "No end quote found for attribute \""+attrSection.getAttributeName()+"\" of "
        +DocumentTokenizer.nameAttrSection+" \""+attrSection.getSectionName()+"\"", 
        tokens, 
        attrTarget //Because this is the end of it
      )
    );
    this.attrSection=attrSection;
    this.attrTarget=attrTarget;
  }
}