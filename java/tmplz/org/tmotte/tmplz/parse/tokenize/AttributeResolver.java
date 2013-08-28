package org.tmotte.tmplz.parse.tokenize;
import java.util.regex.Pattern;
import org.tmotte.tmplz.exception.parse.AttrSectionNoSuchAttributeException;
import org.tmotte.tmplz.exception.parse.AttrSectionNoEndQuoteException;
import org.tmotte.tmplz.parse.Types;
import org.tmotte.tmplz.parse.dum.NodeStatic;
import org.tmotte.common.text.StringChunker;

/**
 * This runs after initial tokenization and gathers
 * tokens into a AttrTarget tokens for AttrSection tags.
 */
class AttributeResolver {

  public static void resolve(TokenList tokens, DocumentTokenizer dt) {
    for (int i=0; i<tokens.size(); i++){
      Token token=tokens.get(i);
      int tokenType=token.getType();
      if (tokenType==Types.ATTR_SECTION_2) 
        doResolve1(tokens, i+1, (TokenAttrSection2)token, dt);
    }
  }


  ///////////////////////////
  // PRIVATE DATA/METHODS: //
  ///////////////////////////
  
  
  private static void doResolve1(TokenList tokens, int startWith, TokenAttrSection2 attrSection, DocumentTokenizer currTokenizer) {
    
    String attrName=attrSection.getAttributeName(),
           sectionName=attrSection.getSectionName();
    Pattern patternStartAttr=Pattern.compile("( +|'|\")"+attrName+" *= *\"");
    StringChunker scStart=new StringChunker();
    
    //Search for a static token containing the attribute name:
    for (int i=startWith; i<tokens.size(); i++){
      Token token=tokens.get(i);
      int tokenType=token.getType();
      if (tokenType==Types.ATTR_TARGET_2) 
        //We've stumbled on another attrtarget that already exists, and we don't
        //want to double-down on the same thing. So let's find the end of it and move on:
        for (i=i+1; i<tokens.size(); i++){
          if (tokens.get(i).getType()==Types.ATTR_TARGET_2)
            break;
        }       
      else
      if (tokenType==Types.STATIC){
        String tokenText=((NodeStatic)token).getText();
        scStart.reset(tokenText);
        if (scStart.find(patternStartAttr)){
          doResolve2(tokens, i, attrSection, scStart);
          return;
        }
      }
    }//for loop thru tokens

    throw new AttrSectionNoSuchAttributeException(tokens, attrSection);
  }

  private static Pattern attrEndQuote=Pattern.compile("\"");

  private static void doResolve2(TokenList tokens, int i, TokenAttrSection2 attrSection, StringChunker scStart) {

    //Let's make the attrTarget:
    TokenAttrTarget2 attrTarget=new TokenAttrTarget2(attrSection);
    
    //Add static node for static text before attribute, 
    //and the "marker" right after that:
    tokens.set(i, new NodeStatic(scStart.getUpTo()));  
    tokens.add(++i, attrTarget);                       
    
    //This is the xxx=" part, i.e. the start of the attribute:
    String startAttribute=scStart.getFound(); 
    
    //If true, the attribute is entirely static and we're done:
    if (scStart.find(attrEndQuote)){ 
      tokens.add(++i, new NodeStatic(startAttribute+scStart.getIncluding()));
      tokens.add(++i, TokenAttrTarget2.getTargetClose());
      tokens.add(++i, new NodeStatic(scStart.getRest()));
      return;
    }     
    
    //Okay then there are Tmplz elements in the "" part of the attribute. We'll have to
    //look forwards through the token list past them and find the first static
    //token with the closing " character:
    tokens.add(++i, new NodeStatic(startAttribute+scStart.getRest()));
    while (++i<tokens.size()) {
      Token token=tokens.get(i);
      int tokenType=token.getType();
      if (tokenType==Types.STATIC){
        String tokenText=((NodeStatic)token).getText();
        scStart.reset(tokenText);
        if (scStart.find(attrEndQuote)) {
          //Deal with old tokens:
          tokens.set(i, new NodeStatic(scStart.getIncluding()));
          tokens.add(++i, TokenAttrTarget2.getTargetClose());
          tokens.add(++i, new NodeStatic(scStart.getRest()));
          return;
        }
      }
    }
    throw new AttrSectionNoEndQuoteException(tokens, attrSection, attrTarget);
  }

  
 
}