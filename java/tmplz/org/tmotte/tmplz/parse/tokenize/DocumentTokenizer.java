package org.tmotte.tmplz.parse.tokenize;
import org.tmotte.tmplz.parse.Types;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import org.tmotte.tmplz.exception.parse.AttrNameBlankException;
import org.tmotte.tmplz.exception.parse.InternalTemplateException;
import org.tmotte.tmplz.exception.parse.NoTailOfTagException;
import org.tmotte.tmplz.exception.parse.RemoveMissingTargetException;
import org.tmotte.tmplz.exception.parse.RenameHasExtraParametersException;
import org.tmotte.tmplz.exception.parse.RenameHasNoParametersException;
import org.tmotte.tmplz.exception.parse.RenameMissingSecondParameterException;
import org.tmotte.tmplz.exception.parse.RenameMissingThirdParameterException;
import org.tmotte.tmplz.exception.parse.TagHasTooManyParametersException;
import org.tmotte.tmplz.exception.parse.TagWithNoParametersException;
import org.tmotte.tmplz.exception.parse.TagWithNothingToRevertToException;
import org.tmotte.tmplz.exception.parse.TagWithUnexpectedNameException;
import org.tmotte.tmplz.exception.parse.UnmatchedQuoteException;
import org.tmotte.tmplz.exception.InternalException;
import org.tmotte.tmplz.parse.dum.NodeStatic;
import org.tmotte.tmplz.parse.dum.NodeRename;
import org.tmotte.tmplz.parse.dum.NodeRemove;
import org.tmotte.common.text.DelimitedString;
import org.tmotte.common.text.StringChunker;

/**
 * DocumentTokenizer is responsible for parsing a template into Token objects. It can be overridden
 * to provide custom parsing behaviors. The default tag delimiters can be changed via DocumentTokenizer's
 * constructors. The tag names themselves (nameSlot, nameSection, etc.) can only be changed by overriding the class and setting them
 * in a constructor (customizing tag names doesn't seem like a very useful thing to do, however).
 * 
 * DocumentTokenizer is thread-safe.
 * @see org.tmotte.tmplz.TemplateManager#setTagDelimiters(String,String)
 * @see org.tmotte.tmplz.TemplateManager#setTokenizer(DocumentTokenizer)
 */
public class DocumentTokenizer {

  ////////////////
  // CONSTANTS: //
  ////////////////


  public static String 
    nameSlot="Slot",
    nameSection="Section",
    nameFillin="Fillin",
    nameShow="Show",
    nameInclude="Include",
    nameRename="Rename",
    nameAttrSection="AttrSection",
    nameTrim="Trim",
    nameRemove="Remove",
    nameBetween="Between"
    ;
  public static String
    nameTagWith="TagWith",
    nameTagWithHTML="html",
    nameTagWithXML="xml",
    nameTagWithCSS="css",
    nameTagWithJS="javascript",
    nameTagWithSQL="sql",
    nameTagWithCurlies="curlies",
    nameTagWithBrackets="brackets",
    nameTagWithDefault="default",
    nameTagWithRevert="revert"
    ;
  public static String
    nameTagWithAlwaysWorks="~~"+nameTagWith;


  /////////////////////
  // INITIALIZATION: //
  /////////////////////

  private Pattern regexBlanks=Pattern.compile("( |\n|\r|\r\n)+"), 
                  regexBlanksEqualsBlanks=Pattern.compile(" *= *");  
  DTOptions defaultOptions;
  private boolean autoTrim=true;

  public DocumentTokenizer() {
    this("[$", "]");
  }
  /**
   * Turns on/off auto trim of tags on lines by themselves. Usually set via TemplateManager.
   * @see org.tmotte.tmplz.TemplateManager#setAutoTrim(boolean)
   */
  public void setAutoTrim(boolean autoTrim) {
    this.autoTrim=autoTrim;
  }
  /**
   * This allows you to create a custom Tokenizer that uses different demarcation tags. Usually
   * set via TemplateManager.setTagDelimiters().
   * @param startOfTag The characters that mark the start of a tag.
   * @param endOfTag The characters that mark the end of a tag.
   * @see org.tmotte.tmplz.TemplateManager#setTokenizer(DocumentTokenizer)
   * @see org.tmotte.tmplz.TemplateManager#setTagDelimiters(String,String)
   */
  public DocumentTokenizer(String startOfTag, String endOfTag){
    defaultOptions=new DTOptions(this, startOfTag, endOfTag);
  }
  
  
  //////////
  // ETC. //
  //////////

  public final String getTokenName(Token t) {
    return getTokenName(t.getType());
  }

  private final String getTokenName(int ttype) {
    if (ttype==Types.SLOT) return nameSlot;
    else if (ttype==Types.SECTION) return nameSection;
    else if (ttype==Types.FILLIN)  return nameFillin;
    else if (ttype==Types.SHOW)    return nameShow;
    else if (ttype==Types.INCLUDE) return nameInclude;
    else if (ttype==Types.STATIC)  return "";
    else if (ttype==Types.TRIM)    return nameTrim;
    else if (ttype==Types.RENAME)  return nameRename;
    else if (ttype==Types.TAG_WITH)       return nameTagWith;
    else if (ttype==Types.ATTR_SECTION_2) return nameAttrSection;
    else if (ttype==Types.REMOVE)         return nameRemove;
    else if (ttype==Types.BETWEEN)        return nameBetween;
    else if (ttype==Types.ATTR_TARGET_2)  return "AttrSection attribute";
    else throw new InternalException("Unexpected: "+ttype);
  }

  //////////////////////////
  // MAIN PARSING DRIVER: //
  //////////////////////////

  /**
   * You can override this method to implement a completely custom markup language, although that could be a somewhat daunting task.
   * Some important things to understand:<ul>
   *   <li>TokenList is a simple list, mostly without any kind of nesting (the exceptions being AttrSlot &amp; AttrSection).</li>
   *   <li>Tokens match one-to-one with tags and "static" template content. </li>
   * </ul>
   * For example, a &quot;section&quot; in a template will be represented by two TokenSection objects in the TokenList, one for the start
   * tag, one for the end tag. Tokens &quot;inside&quot; that section will appear <i>between</i> these two
   *   tokens in the TokenList. The same holds true for TokenSlot, TokenInclude, TokenShow, TokenTrim and TokenFillin. The other tag types
   *   (such as TokenStatic &amp; TokenAttrSection) appear as singular tokens, since they don't have a closing tag.
   */
  public TokenList parse(String source){
    return parse(source, defaultOptions);
  }
  private TokenList parse(String source, DTOptions options){

    //Note that this method does not recurse.
    
    TokenList tokens=new TokenList(this);
    
    //These are "spares" re-used for temporary operations,
    //to save garbage collection overhead:
    StringChunker spareChunker=new StringChunker();
    String[] paramBuff=new String[6];
    
    //While we find a new tag:
    StringChunker chunker=new StringChunker(source);
    while (chunker.find(options.regexStartAnyTag)){
    
      //The in-between stuff counts as "static":
      String upTo=chunker.getUpTo();
      if (!upTo.equals(""))
        tokens.add(new NodeStatic(upTo));
      
      //Now we branch based on what type of tag it is.
      String tagFound=chunker.getFound();
      if (tagFound!=null && (tagFound.equals(nameTagWithAlwaysWorks)))
        options=parseTagWithAlwaysWorks(chunker, tokens, spareChunker, options);
      else 
        options=parseOneToken(tagFound, chunker, tokens, options, spareChunker, paramBuff);
    }
    tokens.add(new NodeStatic(chunker.getRest()));

    //Finally we link AttrSections to the attributes that come after them:
    AttributeResolver.resolve(tokens, this);

    //Now we would like to trim out left-over text around tags that are on lines by themselves
    if (autoTrim)
      //doAutoTrim(tokens, spareChunker);
      DocumentTrimmer.trim(tokens);
    return tokens;
  }
  
  
  //////////////////////////////
  // PARSING FOR TAGWITH TAG: //
  //////////////////////////////
  
  private DTOptions parseTagWithAlwaysWorks(StringChunker chunker, TokenList tokens, StringChunker spareChunker, DTOptions currOptions){
    if (!chunker.find(regexBlanks))
      throw new TagWithNoParametersException(chunker);
    String between=chunker.getFound();
    String tagContents=null;
    TokenStatic leftOver=null;
    if (chunker.find(regexBlanks)){
      tagContents=chunker.getUpTo();
      leftOver=new NodeStatic(chunker.getFound());
    }
    else
      tagContents=chunker.getRest();
    OriginalTag orig=new OriginalTag(nameTagWithAlwaysWorks, between+tagContents, "");

    if (tagContents!=null){
      //If user defined their own "special" tags:
      if (tagContents.contains(",")){
        spareChunker.reset(tagContents);
        spareChunker.find(",");
        currOptions=createTagWith(tokens, currOptions, orig, spareChunker.getUpTo(), spareChunker.getRest());
      }
      else
        currOptions=tagWithName(tokens, currOptions, orig, chunker);
    }
    if (leftOver!=null)
      tokens.add(leftOver);
    return currOptions;
  }
  private DTOptions tagWithName(
      TokenList tokens, 
      DTOptions currOptions, 
      OriginalTag orig,
      StringChunker forErrors
    ) {
    //If user used a "preset" tag set:
    String contents=orig.content.trim().toLowerCase();
    if (contents.equals(nameTagWithDefault))
      return createTagWith(tokens, currOptions, orig, "[$", "]");     
    if (contents.equals(nameTagWithHTML) || contents.equals(nameTagWithXML))
      return createTagWith(tokens, currOptions, orig, "<!--", "-->");
    if (contents.equals(nameTagWithJS) || contents.equals(nameTagWithCSS) || contents.equals(nameTagWithSQL))
      return createTagWith(tokens, currOptions, orig, "/*", "*/");
    if (contents.equals(nameTagWithCurlies))
      return createTagWith(tokens, currOptions, orig, "{", "}");
    if (contents.equals(nameTagWithBrackets))
      return createTagWith(tokens, currOptions, orig, "[", "]");
    if (contents.equals(nameTagWithDefault)){
      TokenTagWith ttw=new TokenTagWith(defaultOptions.delims, orig);
      tokens.add(ttw);
      return defaultOptions;
    }
    if (contents.equals(nameTagWithRevert)){
      if (currOptions.previous==null)
        throw new TagWithNothingToRevertToException(forErrors);          
      TokenTagWith ttw=new TokenTagWith(currOptions.previous.delims, orig);
      tokens.add(ttw);
      return currOptions.previous;
    }
    if (contents.equals(""))
      throw new TagWithNoParametersException(forErrors);
    else
      throw new TagWithUnexpectedNameException(contents, forErrors);
  }

  private DTOptions createTagWith(TokenList tokens, DTOptions oldOptions, OriginalTag orig, String s, String f) {
    //System.out.println(" TAG WITH ->"+s+"<- "+" ->"+f+"<-");
    DTOptions newOptions=new DTOptions(this, s, f);
    newOptions.previous=oldOptions;
    TokenTagWith ttw=new TokenTagWith(newOptions.delims, orig);
    tokens.add(ttw);
    return newOptions;
  }
  
  
  /////////////////////////////
  // PARSING FOR OTHER TAGS: //
  /////////////////////////////
  
  private DTOptions parseOneToken(
      String tagFound, 
      StringChunker chunker, 
      TokenList tokens, 
      DTOptions options, 
      StringChunker spareChunker, //Re-used continuously for memory savings
      String[] paramBuff          //Ditto
    ) {
    //This is the name of the token... Include, Section, AttrSlot, etc:

    //Getting started:
    String tokenType=tagFound.substring(options.startOfTagLen);
    String tokenContents=findEndOfTag(chunker, options.delims, tokenType);
    OriginalTag orig=new OriginalTag(tagFound, tokenContents, options.delims.endOfTag);

    //Now determine token type and process its internals:
    //FIXLATER this needs to be a hash lookup, key=name data=parser. Well, it's faster.
    if (tokenType.equals(nameSlot)){
      spareChunker.reset(tokenContents);
      getParams(spareChunker, 1, paramBuff, chunker);
      tokens.add(new TokenSlot(removeParameterQuotes(paramBuff[0]), orig));
    }
    else
    if (tokenType.equals(nameSection)){
      spareChunker.reset(tokenContents);
      getParams(spareChunker, 1, paramBuff, chunker);
      tokens.add(new TokenSection(removeParameterQuotes(paramBuff[0]), orig));
    }
    else
    if (tokenType.equals(nameTrim))
      tokens.add(new TokenTrim(orig));
    else
    if (tokenType.equals(nameFillin)){
      spareChunker.reset(tokenContents);
      getParams(spareChunker, 1, paramBuff, chunker);
      List<String> toFillin=parseDotNames(spareChunker, paramBuff[0]); 
      tokens.add(new TokenFillin(toFillin, orig));      
    }
    else
    if (tokenType.equals(nameShow)){
      boolean selfClosing=tokenContents.endsWith("/");      
      if (selfClosing)
        tokenContents=tokenContents.substring(0, tokenContents.length()-1);
      spareChunker.reset(tokenContents);
      getParams(spareChunker, 1, paramBuff, chunker);
      List<String> names=parseDotNames(spareChunker, paramBuff[0]);
      tokens.add(new TokenShow(names, selfClosing, orig));
    }
    else
    if (tokenType.equals(nameRename)){
      spareChunker.reset(tokenContents);
      getParams(spareChunker, 3, paramBuff, chunker);
      
      //Need at least 2 parameters:
      if (paramBuff[0]==null) 
        throw new RenameHasNoParametersException(options.delims, chunker);
      else
      if (paramBuff[1]==null)
        throw new RenameMissingSecondParameterException(options.delims, chunker);        

      //Now translate the parameters:
      String toReplace=null, replaceWith=null;
      boolean isSectionTR=paramBuff[0].equals(nameSection);
      boolean isSlotTR =(!isSectionTR) && paramBuff[0].equals(nameSlot);
      if (isSlotTR || isSectionTR) {
        toReplace=paramBuff[1];
        replaceWith=paramBuff[2];
      }
      else {
        toReplace=paramBuff[0];
        replaceWith=paramBuff[1];
        if (paramBuff[2]!=null && !paramBuff[2].equals(""))
          throw new RenameHasExtraParametersException(options.delims, chunker);
      }
      
      //We've already proven we have at least two params (which may be enough);
      //So the remaining exception is that we might need a third:
      if (replaceWith==null || replaceWith.equals(""))
        throw new RenameMissingThirdParameterException(options.delims, chunker);        
      replaceWith=removeParameterQuotes(replaceWith);
      
      //Break down the possibly "." delimited list of Sections+Slot/Section to replace,
      //and create the token:
      List<String> toReplaces=parseDotNames(spareChunker, toReplace);
      tokens.add(new NodeRename(toReplaces, replaceWith, isSlotTR, isSectionTR, toReplace, orig));
    }
    else
    if (tokenType.equals(nameRemove)) {
    
      //Get params:
      spareChunker.reset(tokenContents);
      getParams(spareChunker, 2, paramBuff, chunker);

      //Assign values:
      boolean isSectionTR=paramBuff[0].equals(nameSection);
      boolean isSlotTR =(!isSectionTR) && paramBuff[0].equals(nameSlot);
      String name=(isSlotTR || isSectionTR)
        ?paramBuff[1]
        :paramBuff[0];
      if (name==null || name.equals(""))
        throw new RemoveMissingTargetException(options.delims, chunker); 
      
      //And finish:
      List<String> names=parseDotNames(spareChunker, name);
      tokens.add(new NodeRemove(names, isSlotTR, isSectionTR, orig));
      
    }
    else
    if (tokenType.equals(nameInclude)){
    
      //INCLUDE:
      if (tokenContents.trim().equals(""))
        //This is a closing include tag:
        tokens.add(new TokenInclude(orig));
      else {
        //This include tag has content:
        spareChunker.reset(tokenContents);
        getParams(spareChunker, 2, paramBuff, chunker);
        tokens.add(
          new TokenInclude(
            removeParameterQuotes(paramBuff[0]), 
            removeParameterQuotes(paramBuff[1]),
            orig
          )
        );
      }
    }
    else
    if (tokenType.equals(nameAttrSection)){
    
      //ATTRSECTION:
      //Note that if no section name is specified,
      //we will default to creating a section with the same name as the attribute.
      spareChunker.reset(tokenContents);
      getParams(spareChunker, 2, paramBuff, chunker);
      paramBuff[0]=removeParameterQuotes(paramBuff[0]);
      paramBuff[1]=removeParameterQuotes(paramBuff[1]);
      String attrName=paramBuff[1]!=null ?paramBuff[1] :paramBuff[0],
             sectionName=paramBuff[0];
      if (attrName==null)
        throw new AttrNameBlankException(nameAttrSection, chunker);
      tokens.add(new TokenAttrSection2(sectionName, attrName, orig));

    }
    else
    if (tokenType.equals(nameTagWith)){
    
      //TAGWITH:       
      //System.out.println(" OH FUUU ->"+tokenContents+"<-");
      spareChunker.reset(tokenContents);
      getParams(spareChunker, 2, paramBuff, chunker);      
      paramBuff[0]=removeParameterQuotes(paramBuff[0]);
      paramBuff[1]=removeParameterQuotes(paramBuff[1]);
      if (paramBuff[0]==null)
        throw new TagWithNoParametersException(chunker);
      else
      if (paramBuff[1]==null)
        options=tagWithName(tokens, options, orig, chunker);
      else 
        options=createTagWith(tokens, options, orig, paramBuff[0], paramBuff[1]);

    }
    else
    if (tokenType.equals(nameBetween))
    
      //BETWEEN:
      tokens.add(new TokenBetween(orig));
      
    else
       throw new InternalTemplateException("Unexpected: "+options.delims.startOfTag+tokenType, chunker);
    return options;
  }
  
  
  ///////////////////////
  // PRODUCE DOCUMENT: //
  ///////////////////////

  /**
   * This recreates the original text template from a TokenList. This method is invoked by TemplateErrorInfo
   * and used to find the part of the template where an error was found, then report it to the user.
   *
   * @see org.tmotte.tmplz.exception.parse.TemplateErrorInfo
   */
  public void produce(Appendable a, TokenList tokens, Token lastToken){
    try {
      for (int i=0; i<tokens.size(); i++){
        Token token=tokens.get(i);
        ((AbstractToken)token).getOriginalTag(a);
        if (token==lastToken)
          return;
      }
    } catch (java.io.IOException e) {
      throw new RuntimeException(e);//This is just so we don't have to deal with checked exceptions.
    }
  }


  //////////////////////
  //                  //
  // STATIC UTILITIES //
  //                  //
  //////////////////////


  //////////////////
  // MINOR UTILS: //
  //////////////////

  private static String myTrim(String s){
    if (s==null)
      return null;
    s=s.trim();
    if (s.equals(""))
      s=null;
    return s;      
  }

  //////////////////////
  // FIND END OF TAG: //
  //////////////////////
  
  private static String findEndOfTag(StringChunker chunker, Delimiters delimiters, String tag) {
    boolean onQuotes=false;
    StringBuilder sb=new StringBuilder();
    while (chunker.find(delimiters.pEndOfTag)) {
      String s=chunker.getFound();
      sb.append(chunker.getUpTo());
      if (onQuotes || !s.equals(delimiters.endOfTag))
        sb.append(chunker.getFound());
      //System.out.println(" "+tag+" "+onQuotes+" ->"+sb+"<-");
      if (s.equals("\""))
        onQuotes=!onQuotes;
      else
      if (!onQuotes)
        return sb.toString();
    }
    if (onQuotes)
      throw new UnmatchedQuoteException(chunker);
    else
      throw new NoTailOfTagException(delimiters, tag, chunker);
  }
  private static void testFindEndOfTag(String[] args) {
    Delimiters d=new Delimiters(args[0], args[1]);
    StringChunker sc=new StringChunker(args[2]);
    System.out.println(findEndOfTag(sc, d, "Fillin"));
  }
  
  //////////////////////////
  // TAG PARAMETER PARSER //
  //////////////////////////

  static Pattern patternPSpacesOrDoubleQuote=Pattern.compile("(\"|\\s+)"),
                 patternPQuotes=Pattern.compile("\"");
  private static String getParam(StringChunker chunker, StringChunker forErrors) {
    if (chunker.finished())
      return null;
    StringBuilder result=new StringBuilder();
    boolean onQuotes=false;
    Pattern pattern=patternPSpacesOrDoubleQuote;
    while (chunker.findOrFinish(pattern)){
      String found=chunker.getFound(), 
             upTo=chunker.getUpTo().trim();
      result.append(upTo);
      if (onQuotes && found.equals("\"")) {
        onQuotes=false;
        pattern=patternPSpacesOrDoubleQuote;
        result.append(found);
      }
      else
      if (found.equals("\"")){
        onQuotes=true;
        pattern=patternPQuotes;
        result.append(found);
      }
      else 
      if (result.length()>0)
        break;
    }
    if (onQuotes)
      throw new UnmatchedQuoteException(forErrors);
    return result.toString();
  }
  private static void getParams(StringChunker chunker, int max, String[] paramBuffer, StringChunker forErrors) {
    for (int i=0; i<paramBuffer.length; i++)
      paramBuffer[i]=null;
    int i=-1;
    while (++i<paramBuffer.length) {
      paramBuffer[i]=getParam(chunker, forErrors);
      if (paramBuffer[i]==null)
        break;
    }
    if (i>max)
      throw new TagHasTooManyParametersException(paramBuffer, max, forErrors);
  }
  private static String removeParameterQuotes(String s) {
    if (s==null)
      return s;
    s=s.trim();
    if (s.startsWith("\"")  && s.endsWith("\""))
      return s.substring(1, s.length()-1).trim();
    return s;
  }
  private static void testGetParams(String paramStr) {
    StringChunker sc=new StringChunker(paramStr);
    String[] params=new String[20];
    getParams(sc, params.length, params, sc);
    for (int i=0; i<params.length && params[i]!=null; i++) 
      System.out.println("-->"+params[i]+"<--");
  }
  
  /////////////////////////
  // DOTTED-NAME PARSER: //
  /////////////////////////
  
  static Pattern dotNamePatternBoth=Pattern.compile("(\\.|\")"), dotNamePatternQuoteOnly=Pattern.compile("\"");
  private static List<String> parseDotNames(StringChunker spareChunker, String names) {
    names=myTrim(names);
    if (names==null)
      return null;
    List<String> nameList=new LinkedList<String>();
    spareChunker.reset(names);
    boolean onQuotes=false;
    Pattern pattern=dotNamePatternBoth;
    String spareName="";
    while (spareChunker.findOrFinish(pattern)){
      String found=spareChunker.getFound();
      if (onQuotes) {
        onQuotes=false;
        pattern=dotNamePatternBoth;
      }
      else
      if (found.equals("\"")){ 
        onQuotes=true;
        pattern=dotNamePatternQuoteOnly;
      }
      String s=spareChunker.getUpTo();
      if (!"".equals(s)){
        if (onQuotes) 
          spareName=spareChunker.getUpTo();
        else {
          nameList.add(spareName+spareChunker.getUpTo());
          spareName="";
        }
      }
    }
    return nameList;
  }
  private static void testParseDotNames(String[] args) {
    for (String s: args) 
      System.out.println(parseDotNames(new StringChunker(), s));
  }

  ///////////
  // TEST: //
  ///////////

  private static String blankIfNull(TokenStatic ts) {
    if (ts==null)
      return "";
    return ts.getText();
  }
  public static void main(String[] args) {
    testFindEndOfTag(args);
  }



}
