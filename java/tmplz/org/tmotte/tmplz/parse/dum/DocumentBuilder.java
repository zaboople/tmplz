package org.tmotte.tmplz.parse.dum;
import java.util.List;
import java.util.Set;
import org.tmotte.tmplz.exception.parse.FillinHasNoSlotNameException;
import org.tmotte.tmplz.exception.parse.IllegalTagPlacementException;
import org.tmotte.tmplz.exception.parse.IncludeHasNoPathException;
import org.tmotte.tmplz.exception.parse.InternalTemplateException;
import org.tmotte.tmplz.exception.parse.NoTextWithinTagException;
import org.tmotte.tmplz.exception.parse.SectionHasNoNameException;
import org.tmotte.tmplz.exception.parse.ShowHasNoSectionNameException;
import org.tmotte.tmplz.exception.parse.SlotHasNoNameException;
import org.tmotte.tmplz.exception.parse.TagNotClosedException;
import org.tmotte.tmplz.load.Path;
import org.tmotte.tmplz.parse.preprocess.Preprocessor;
import org.tmotte.tmplz.parse.TextSource;
import org.tmotte.tmplz.parse.tokenize.DocumentTokenizer;
import org.tmotte.tmplz.parse.tokenize.Token;
import org.tmotte.tmplz.parse.tokenize.TokenAttrTarget2;
import org.tmotte.tmplz.parse.tokenize.TokenFillin;
import org.tmotte.tmplz.parse.tokenize.TokenInclude;
import org.tmotte.tmplz.parse.tokenize.TokenList;
import org.tmotte.tmplz.parse.tokenize.TokenSection;
import org.tmotte.tmplz.parse.tokenize.TokenShow;
import org.tmotte.tmplz.parse.tokenize.TokenSlot;
import org.tmotte.tmplz.parse.Types;

public class DocumentBuilder {

  public static NodeList getNodes(
      TextSource textSource, 
      DocumentTokenizer tokenizer, 
      Preprocessor preprocessor, 
      boolean doAggressiveTrim,
      Set<TextSource> parents
    ) throws Exception {
    TokenList tokens=textSource.getCachedTokens();
    if (tokens==null){
      tokens=tokenizer.parse(textSource.getText());
      textSource.setCachedTokens(tokens);
    }
    NodeList nodes=process(tokens, doAggressiveTrim);
    //Note: If there are includes, the preprocessor will recurse back into getNodes() for the included NodeList:
    preprocessor.preprocess(nodes, tokens, textSource, parents);
    return nodes;
  }
  private static NodeList process(TokenList tokens, boolean doAggressiveTrim) {
    NodeList nodes=new NodeList();
    process(nodes, tokens, null, 0, doAggressiveTrim);
    return nodes;
  }
  private static int process(
      NodeList nodes,
      TokenList tokens, 
      Token parentToken, 
      int tokenIndex,
      boolean doAggTrim
    )  {
    DocumentTokenizer tokenizer=tokens.getTokenizer();
    final int parentType=parentToken==null 
      ?-1 
      :parentToken.getType();
    final int tokenCount=tokens.size();
    final int lastIndex=tokenCount-1;
    for (int i=tokenIndex; i<tokenCount; i++){
      Token token=tokens.get(i);
      final int tokenType=token.getType();
      
      boolean inLevel1Space=parentType==-1 || Types.memberOf(parentType, Types.SECTION|Types.FILLIN|Types.TRIM|Types.ATTR_TARGET_2);

      if (tokenType==Types.STATIC) {
        NodeStatic ns=(NodeStatic)token;
        if (inLevel1Space || parentType==Types.BETWEEN){
          if (!ns.getText().equals("")){
            ns.setDoAggressiveTrim(doAggTrim);
            nodes.add(ns); 
          }
        }
        else
        if (
            (parentType==Types.INCLUDE || parentType==Types.SHOW)
            && !ns.getText().trim().equals("")
            )
          throw new NoTextWithinTagException(tokens, parentToken, ns); 
      }
      else
      if (tokenType==Types.TRIM) {
        if (parentType==Types.TRIM) 
          return i;
        if (!inLevel1Space) 
          throw new IllegalTagPlacementException(tokens, parentToken, token);
        i=process(nodes, tokens, token, i+1, true); 
      }
      else
      if (tokenType==Types.SLOT) {
        TokenSlot ts=(TokenSlot)token;
        String slotName=ts.getName();
        if (parentType==Types.SLOT && slotName==null)
          return i;
        if (!inLevel1Space) 
          throw new IllegalTagPlacementException(tokens, parentToken, token);
        if (slotName==null)
          throw new SlotHasNoNameException(tokens, ts);
        if (isSelfClosing(ts, tokens, i+1))
          nodes.add(new NodeSlot(slotName));
        else {
          //Continue and find the closing Slot tag:
          i=process(nodes, tokens, token, i+1, false); 
          nodes.add(new NodeSlot(slotName));
        }
      }
      else
      if (tokenType==Types.SECTION) {
        TokenSection ts=(TokenSection)token;
        String sectionName=ts.getName();
        if (parentType==Types.SECTION && sectionName==null)
          return i;
        if (!inLevel1Space) 
          throw new IllegalTagPlacementException(tokens, parentToken, token);
        if (sectionName==null)
          throw new SectionHasNoNameException(tokens, ts);
        NodeList sectionNodes=new NodeList();
        i=process(sectionNodes, tokens, token, i+1, doAggTrim); 
        nodes.add(new NodeSection(sectionName, sectionNodes));
      }
      else
      if (tokenType==Types.ATTR_TARGET_2) {
        TokenAttrTarget2 attr=(TokenAttrTarget2) token;
        if (parentType==Types.ATTR_TARGET_2){
          if (!attr.isClosing())
            throw new InternalTemplateException(
              "Target attribute of AttrSection did not close!", tokens, parentToken
            );
          return i;
        }
        else
        if (attr.isClosing())
          throw new TagNotClosedException(tokens, parentToken);
        NodeList sectionNodes=new NodeList();
        i=process(sectionNodes, tokens, token, i+1, doAggTrim); 
        nodes.add(new NodeSection(attr.getSectionName(), sectionNodes));        
      }
      else
      if (tokenType==Types.INCLUDE) {
        TokenInclude ti=(TokenInclude)token;
        String ref=ti.getReference(), sectionName=ti.getSectionName();
        if (parentType==Types.INCLUDE && ref==null)
          return i;        
        if (!inLevel1Space)
          throw new IllegalTagPlacementException(tokens, parentToken, token);        
        if (ref==null)
          throw new IncludeHasNoPathException(tokens, token);
          
       
        //Resolve the tag:
        if (isSelfClosing(ti, tokens, i+1))
          nodes.add(new NodeInclude(ref, sectionName, new NodeList(), ti, doAggTrim));
        else {
          NodeList inclInstructions=new NodeList();
          i=process(inclInstructions, tokens, token, i+1, doAggTrim);
          nodes.add(new NodeInclude(ref, sectionName, inclInstructions, ti, doAggTrim));
        }
      }
      else
      if (tokenType==Types.SHOW) {
        TokenShow tokenShow=(TokenShow)token;
        List<String> names=tokenShow.getNames();
        if (parentType==Types.SHOW)
          if (tokenShow.closes((TokenShow)parentToken))
            return i;
        if (parentType!=Types.INCLUDE && parentType!=Types.SHOW) 
          throw new IllegalTagPlacementException(tokens, parentToken, token, tokenizer.nameInclude, tokenizer.nameShow);
        if (names==null)
          throw new ShowHasNoSectionNameException(tokens, token);

        //Resolve the tag:
        NodeShow ns=new NodeShow(names, tokenShow);
        if (!tokenShow.isSelfClosing())
          i=process(ns.getNodes(), tokens, tokenShow, i+1, doAggTrim);
        nodes.add(ns);
      }
      else
      if (tokenType==Types.FILLIN) {
        TokenFillin ti=(TokenFillin)token;
        List<String> names=ti.getNames();
        if (parentType==Types.FILLIN && names==null)
          return i;
        if (parentType!=Types.INCLUDE && parentType!=Types.SHOW) 
          throw new IllegalTagPlacementException(tokens, parentToken, token, tokenizer.nameInclude, tokenizer.nameShow);
        if (names==null)
          throw new FillinHasNoSlotNameException(tokens, token);
        NodeFillin ns=new NodeFillin(names, ti);
        i=process(ns.getNodes(), tokens, ti, i+1, doAggTrim);
        nodes.add(ns);        
      }
      else
      if (tokenType==Types.TAG_WITH || tokenType==Types.ATTR_SECTION_2) {      
        //Don't care; TagWith is handled by the tokenizer, and AttrSection2
        //is handled entirely by the AttrTarget2 token.
      }
      else
      if (tokenType==Types.RENAME) {
        if (parentType!=Types.SHOW && parentType!=Types.INCLUDE) 
          throw new IllegalTagPlacementException(tokens, parentToken, token, tokenizer.nameShow, tokenizer.nameInclude);
        nodes.add((NodeRename)token);
      }
      else
      if (tokenType==Types.REMOVE) {
        if (parentType!=Types.SHOW && parentType!=Types.INCLUDE) 
          throw new IllegalTagPlacementException(tokens, parentToken, token, tokenizer.nameShow, tokenizer.nameInclude);
        nodes.add((NodeRemove)token);
      }
      else
      if (tokenType==Types.BETWEEN) {      
        if (parentType==Types.BETWEEN)
          //This closes a prior betweeen:
          return i;      
        if (parentType!=Types.SECTION) 
          throw new IllegalTagPlacementException(tokens, parentToken, token, tokenizer.nameBetween);
        NodeList newNodes=new NodeList();
        i=process(newNodes, tokens, token, i+1, doAggTrim); 
        nodes.add(new NodeBetween(newNodes));  
      }
      
      // All deprecated from here down to "throw":
      
      else
        throw new InternalTemplateException("Unexpected token type: "+tokenType+" "+token, tokens, token);
    }

    if (parentType!=-1)
      //Um, this shouldn't happen for AttrSection, should it? Nah.
      throw new TagNotClosedException(tokens, parentToken);
    return tokens.size();
  }
  
  private static boolean notSlotClose(Token t, String slotName) {
    int tokenType=t.getType();
    if (tokenType==Types.SLOT){
      TokenSlot ts=(TokenSlot)t;
      String ref=ts.getName();
      return ref!=null && !slotName.equals(ref);
    }
    else
      return true;
  }
  private static boolean isSelfClosing(TokenInclude ti, TokenList tokens, int startIndex){        
    for (int k=startIndex; k<tokens.size(); k++){
      Token t=tokens.get(k);
      int ttype=t.getType();
      if (ttype!=Types.STATIC) 
        return ttype!=Types.SHOW 
            && ttype!=Types.RENAME
            && ttype!=Types.REMOVE
            //Not followed by closing include tag (an include with reference means this include is self-closing):
            && (ttype!=Types.INCLUDE || ((TokenInclude)t).getReference()!=null) 
            //Not followed by named fillin tag (a closing fillin tag means we're inside a fillin):
            && (ttype!=Types.FILLIN  || ((TokenFillin)t).getNames()==null)      
          ;
    }
    return true;
  }
  private static boolean isSelfClosing(TokenSlot ti, TokenList tokens, int startIndex){
    //Wait, shouldn't all slots should be self-closing? No, they shouldn't be. We allow that.
    for (int k=startIndex; k<tokens.size(); k++){
      Token t=tokens.get(k);
      int ttype=t.getType();
      if (ttype!=Types.STATIC) 
        return ttype!=Types.SLOT || ((TokenSlot)t).getName()!=null;
    }
    return true;
  }
}