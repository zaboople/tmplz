package org.tmotte.tmplz.parse.preprocess;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.tmotte.tmplz.exception.InternalException;
import org.tmotte.tmplz.exception.parse.FillinNoSuchSlotException;
import org.tmotte.tmplz.exception.parse.IncludeCircularException;
import org.tmotte.tmplz.exception.parse.IncludeNestedException;
import org.tmotte.tmplz.exception.parse.IncludeReferencesMissingSectionException;
import org.tmotte.tmplz.exception.parse.RemoveNoSuchElementException;
import org.tmotte.tmplz.exception.parse.RenameNoSuchTagException;
import org.tmotte.tmplz.exception.parse.ShowNoSuchSectionException;
import org.tmotte.tmplz.load.Path;
import org.tmotte.tmplz.parse.dum.DocumentBuilder;
import org.tmotte.tmplz.parse.dum.Node;
import org.tmotte.tmplz.parse.dum.NodeFillin;
import org.tmotte.tmplz.parse.dum.NodeInclude;
import org.tmotte.tmplz.parse.dum.NodeList;
import org.tmotte.tmplz.parse.dum.NodeRemove;
import org.tmotte.tmplz.parse.dum.NodeRename;
import org.tmotte.tmplz.parse.dum.NodeSection;
import org.tmotte.tmplz.parse.dum.NodeShow;
import org.tmotte.tmplz.parse.dum.NodeSlot;
import org.tmotte.tmplz.parse.dum.NodeStatic;
import org.tmotte.tmplz.parse.TemplateBuilder;
import org.tmotte.tmplz.parse.TextSource;
import org.tmotte.tmplz.parse.tokenize.DocumentTokenizer;
import org.tmotte.tmplz.parse.tokenize.TokenList;
import org.tmotte.tmplz.parse.Types;

/**
 * This takes a NodeList (usually created by a DocumentBuilder) and
 * processes the Include statements within such. 
 */
public class Preprocessor {

  private static NodeStatic nodeNothing=new NodeStatic("");

  private DocumentTokenizer tokenizer;
  private boolean destructiveShow=false;
  
  public void setTokenizer(DocumentTokenizer t) {
    this.tokenizer=t;
  }
  public void setDestructiveShow(boolean tf) {
    destructiveShow=tf;
  }
  public boolean getDestructiveShow() {
    return destructiveShow;
  }

  
  /**
   * @param nodes The list of nodes to preprocess.
   * @param originalTokens The tokens the nodes came from; this is just for use in throwing Exceptions.
   * @param textSource The TextSource the nodes came from. Will be asked to resolve Include references to relative paths.
   */
  public void preprocess(NodeList nodes, TokenList originalTokens, TextSource textSource, Set<TextSource> priorParents)  {
    Set<TextSource> parents=null;
    for (int i=0; i<nodes.size(); i++){
      Node node=nodes.get(i);
      int nodeType=node.getType();
      if (nodeType==Types.INCLUDE) {
        NodeInclude includer=(NodeInclude)node;
        String inclPath=includer.getReference();

        //Always make a new Set for the list of parents; if we use the existing one,
        //it will confuse parallel or antecedent loads. Sucks for garbage collection,
        //but we gotta:
        if (parents==null)
          parents=new HashSet<TextSource>();
        parents.clear();
        if (priorParents!=null)
          parents.addAll(priorParents);
        
        //Load included data:
        String includedData=null;
        TextSource includedTextSource;
        NodeList included;
        try {
          includedTextSource=textSource.getInThisContext(inclPath, parents);
          
          /*
          //This happens when we come back a second time and the source still has
          //an error (usually a bad URL).
          if (includedTextSource.hasError())
            throw includedTextSource.getError();            
          */
          included=DocumentBuilder.getNodes(includedTextSource, tokenizer, this, includer.getDoAggressiveTrim(), parents);
        } catch (org.tmotte.tmplz.exception.load.CircularPathException cpe) {
          throw new IncludeCircularException(originalTokens, includer.getOriginalToken(), inclPath);
        } catch (Exception e) {
          throw new IncludeNestedException(originalTokens, includer.getOriginalToken(), e);//Change name to IncludeFailedException
        } 
        
        //If only a specific Section was requested, obtain it here:
        String inclSectionName=includer.getSectionName();
        if (inclSectionName!=null) 
          included=getIncludedSection(included, includer, originalTokens);
        
        //Apply the includer's instructions to the included nodes. Yeah, we gotta clear parents out again,
        //because the included file is not a "parent" of any fillins:
        parents.clear();
        if (priorParents!=null)
          parents.addAll(priorParents);
        applyIncluderInstructions(includer.getNodes(), included, originalTokens, textSource, inclPath, parents);
        
        //Make sure all the show, fillin, rename, and remove tags worked:        
        verify(includer.getNodes(), originalTokens, inclPath);
        
        //Replace this node with the included ones:
        i=nodes.replace(i, included);
                    
      }
      else 
      if (nodeType==Types.SECTION)
        preprocess(((NodeSection)node).getNodes(), originalTokens, textSource, priorParents);
    } 
  }


  //////////////////////
  // PRIVATE METHODS: //
  //////////////////////
  
  private static NodeList getIncludedSection(NodeList included, NodeInclude includer, TokenList originalTokens) {
    String sectionName=includer.getSectionName();
    NodeList results=null;
    for (int z=0; z<included.size(); z++){
      Node inclNode=included.get(z);
      if (inclNode.getType()==Types.SECTION){
        NodeSection section=(NodeSection)inclNode;
        if (section.getName().equals(sectionName)){
          NodeList sectionNodes=section.getNodes();
          if (results==null)
            results=sectionNodes;
          else 
            for (int i=0; i<sectionNodes.size(); i++)
              results.add(sectionNodes.get(i));
        }
      }
    } 
    if (results==null)
      throw new IncludeReferencesMissingSectionException(includer, originalTokens);
    return results;
  }
  private static void verify(NodeList nodes, TokenList originalTokens, String inclPath) {
    for (int i=0; i<nodes.size(); i++){
      Node node=nodes.get(i);
      int nodeType=node.getType();
      if (nodeType==Types.INCLUDE) {
        NodeInclude nodeInclude=(NodeInclude) node;
        verify(nodeInclude.getNodes(), originalTokens, nodeInclude.getReference());
      }
      else
      if (nodeType==Types.SECTION) 
        verify(((NodeSection)node).getNodes(), originalTokens, inclPath);
      else
      if (nodeType==Types.RENAME) {
        NodeRename nodeRename=(NodeRename)node;
        if (!nodeRename.getFound())
          throw new RenameNoSuchTagException(originalTokens, nodeRename.getOriginalToken(), inclPath);
      }
      else
      if (nodeType==Types.REMOVE) {
        NodeRemove nodeRemove=(NodeRemove)node;
        if (!nodeRemove.getFound())
          throw new RemoveNoSuchElementException(originalTokens, nodeRemove.getOriginalToken(), inclPath);
      }
      else
      if (nodeType==Types.FILLIN) {
        NodeFillin nodeFillin=(NodeFillin)node;
        if (!nodeFillin.getFound())
          throw new FillinNoSuchSlotException(originalTokens, nodeFillin.getOriginalToken(), inclPath); 
        verify(nodeFillin.getNodes(), originalTokens, inclPath);
      }
      else
      if (nodeType==Types.SHOW) {
        NodeShow nodeShow=(NodeShow)node;
        if (!nodeShow.getFound())
          throw new ShowNoSuchSectionException(originalTokens, nodeShow.getOriginalToken(), inclPath); 
        verify(nodeShow.getNodes(), originalTokens, inclPath);
      }
    }
  }

  /**
   * @param instructions This is the NodeList from a NodeInclude or a NodeShow. It will contain
   *   NodeStatics (ignored), NodeFillins, and NodeShows. 
   * @param included This is the NodeList from an included document or a NodeSection corresponding to 
   *   the case when the instructions parameter comes from a NodeShow.
   * @param originalTokens The tokens from the parent that was passed to the preprocessor
   */
  private void applyIncluderInstructions(
      NodeList instructions, 
      NodeList included, 
      TokenList originalTokens,
      TextSource textSource, 
      String includedPath,
      Set<TextSource> parents
    )  {
    
    //1. Process the include instructions against the included data. Note that this could be from
    //   an Include against a NodeList, or an IsSection against a NodeSection's nodes.
    for (int j=0; j<instructions.size(); j++){
      Node instruct=instructions.get(j);
      int instructType=instruct.getType();
      if (instructType==Types.RENAME)
        resolveRename((NodeRename)instruct, included, originalTokens, includedPath);
      else
      if (instructType==Types.FILLIN){
        NodeFillin fillin=(NodeFillin)instruct;
        preprocess(fillin.getNodes(), originalTokens, textSource, parents);
        PreprocessorFillin.fillin(fillin, included);          
      }
      else
      if (instructType==Types.SHOW)
        resolveShow((NodeShow)instruct, included,  originalTokens, textSource, includedPath, parents);
      else
      if (instructType==Types.REMOVE)
        resolveRemove((NodeRemove)instruct, included, originalTokens, includedPath);
      else
      if (instructType==Types.STATIC){
        //Yeah, static gets ignored
      }
      else 
        throw new InternalException(
          "Should have been caught earlier in processing; this node type not allowed inside Include statement: "+instruct.getClass()
        );
    }


    //2. Now we do a second layer of preprocessing:
    //   Convert any included NodeSections that were "shown" by the instructions NodeList
    //    above so that the shown nodes replace the original NodeSection. Note 
    //    that "shown" will be a list of NodeSections.
    displayShownSections(included);
  }
  private void displayShownSections(NodeList included) {
    for (int i=0; i<included.size(); i++){
      Node node=included.get(i);
      int nodeType=node.getType();
      if (nodeType==Types.SECTION){
        NodeSection section=(NodeSection)node;
        displayShownSections(section.getNodes());//RECURSE
        List<NodeSection> shown=section.getShown();
        if (shown!=null){
          included.remove(i);
          for (int q=0; q<shown.size(); q++)
            i=included.add(i, shown.get(q).getNodes())
              +1;//Adding 1 because index of last node is returned; we want next insert to go *after* that node.
          if (destructiveShow || section.isRemoved())
            i--;//Subtracting 1 because loop adds 1 after this statement.
          else {
            section.resetShown();
            included.add(i, section);
          }
        }
        else
        if (section.isRemoved()){
          included.remove(i);
          i--;
        }
      }
    }  
  }

  /**
   * This applies the given NodeShow against a list of included nodes. Note that:<br/>
   * 1. It may be that multiple Sections with the same name are in includedNodes (unusual but possible). <br/>
   * 2. This method may get called for different NodeShow's referring to the same Section name, 
   *    to repeat sections (much more common). <br/>
   * 3. AttrSections are also handled. Note that an AttrSection should only be shown once; if you try to 
   *    show it twice, the second will generate a not-found exception because it's been converted to
   *    an AttrSlot. It's still okay to have two AttrSections with the same name, however, and show each of them.
   */
  private void resolveShow(
      NodeShow nodeShow, 
      NodeList includedNodes, 
      TokenList originalTokens,
      TextSource textSource, 
      String includedPath,
      Set<TextSource> parents
    )  {
    if (resolveShow(nodeShow, 0, includedNodes, originalTokens, textSource, includedPath, parents))
      nodeShow.setFound();
  }
  private boolean resolveShow(
      NodeShow nodeShow,
      int currIndex,
      NodeList includedNodes, 
      TokenList originalTokens,
      TextSource textSource, 
      String includedPath,
      Set<TextSource> parents
    )  {
    List<String> showNames=nodeShow.getNames();
    String showName=showNames.get(currIndex);
    boolean onLastShowInSequence=currIndex==showNames.size()-1;
    boolean result=false;
    for (int k=0; k<includedNodes.size(); k++){
      Node inclNode=includedNodes.get(k);      
      int inclNodeType=inclNode.getType();
      if (inclNodeType==Types.SECTION) {    
        NodeSection section=(NodeSection)inclNode;
        if (section.getName().equals(showName)){
          if (onLastShowInSequence){
            //Note that the "show" takes place right here:
            result=true;
            if (section.isRemoved())
              throw new ShowNoSuchSectionException(originalTokens, nodeShow.getOriginalToken(), includedPath);
            applyIncluderInstructions(
              nodeShow.getNodes(), section.show().getNodes(),
              originalTokens, textSource, includedPath, parents
            );
          }
          else
            //Note that we don't actually do a "show"; the show will take
            //place at the specified level of nesting.
            result|=resolveShow(
              nodeShow, currIndex+1, section.getNodes(),
              originalTokens, textSource, includedPath, parents
            );
        }
      }
    }    
    return result;
  }


  /////////////
  // RENAME: //
  /////////////

  private void resolveRename(
      NodeRename renamer, NodeList included, 
      TokenList originalTokens, String includedPath
    ){
    if (resolveRename(renamer, renamer.getToReplace(), renamer.getReplaceWith(), included, 
                      originalTokens, includedPath))
      renamer.setFound();
  }
  private boolean resolveRename(
      NodeRename renamer, 
      List<String> toReplace, 
      String replaceWith, 
      NodeList included,
      TokenList originalTokens,
      String includedPath
    ){
    String nextLevel=toReplace.get(0);
    if (toReplace.size()>1) {
      //We are still on dot-delimited parent names, drilling down:
      boolean found=false;
      for (int k=0; k<included.size(); k++){
        Node inclNode=included.get(k);
        int nodeType=inclNode.getType();
        NodeList subnodes=null;
        if (nodeType==Types.SECTION) {
          NodeSection section=(NodeSection)inclNode;
          if (section.getName().equals(nextLevel))
            subnodes=section.getNodes();
        }
        if (subnodes!=null) {
          found|=resolveRename(
            renamer, toReplace.subList(1, toReplace.size()),  replaceWith,  subnodes, 
            originalTokens, includedPath
          );
        }
      }
      return found;
    }
    else 
      //Now we actually do the rename:
      return resolveRename(renamer, nextLevel, replaceWith, included, 
                           originalTokens, includedPath);
  }
  private boolean resolveRename(
      NodeRename renamer, 
      String toReplace, 
      String replaceWith, 
      NodeList included, 
      TokenList originalTokens,
      String includedPath
    )    {
    boolean found=false;
    boolean canBeSlot=renamer.isSlot() || renamer.isUnknown(),
            canBeSection=renamer.isSection() || renamer.isUnknown();
    for (int k=0; k<included.size(); k++){
      Node inclNode=included.get(k);
      int inclType=inclNode.getType();
      if (canBeSlot && inclType==Types.SLOT && ((NodeSlot)inclNode).getName().equals(toReplace)){
         ((NodeSlot)inclNode).setName(replaceWith);
         found=true;
      }
      
      if (canBeSection) {
        if (inclType==Types.SECTION) {
          NodeSection section=(NodeSection)inclNode;
          if (section.getName().equals(toReplace)) {
            if (section.isRemoved())
              throw new RenameNoSuchTagException(originalTokens, renamer.getOriginalToken(), includedPath);     
            section.setName(replaceWith);
            found=true;
          }
        }
      }
    }
    return found;
  }




  /////////////
  // REMOVE: //
  /////////////

  private static void resolveRemove(
      NodeRemove remover, NodeList included, TokenList originalTokens, String includedPath
    ){
    if (resolveRemove(remover, remover.getToRemove(), included, originalTokens, includedPath))
      remover.setFound();
  }
  private static boolean resolveRemove(
      NodeRemove remover, List<String> toRemove, NodeList included, TokenList originalTokens, String includedPath
    ){
    String nextLevel=toRemove.get(0);
    if (toRemove.size()>1) {
      //We need to find a section that needs removing
      boolean found=false;
      for (int k=0; k<included.size(); k++){
        Node inclNode=included.get(k);
        NodeList subnodes=null;
        if (inclNode.getType()==Types.SECTION && ((NodeSection)inclNode).getName().equals(nextLevel)) 
          subnodes=((NodeSection)inclNode).getNodes();
        if (subnodes!=null) 
          found|=resolveRemove(
            remover, toRemove.subList(1, toRemove.size()),  subnodes, 
            originalTokens, includedPath
          );
      }
      return found;
    }
    else 
      return resolveRemove(remover, nextLevel, included, originalTokens, includedPath);
  }
  private static boolean resolveRemove(
      NodeRemove remover, String toRemove, NodeList included, 
      TokenList originalTokens, String includedPath
    )    {
    boolean found=false;
    boolean canBeSlot=remover.isSlot() || remover.isUnknown(),
            canBeSection=remover.isSection() || remover.isUnknown();
    for (int k=0; k<included.size(); k++){
      Node inclNode=included.get(k);
      int inclType=inclNode.getType();
      if (canBeSlot && inclType==Types.SLOT && ((NodeSlot)inclNode).getName().equals(toRemove)){
        included.remove(k--);
        found=true;
      }
      else
      if (canBeSection) {
        if (inclType==Types.SECTION && ((NodeSection)inclNode).getName().equals(toRemove)){
          NodeSection nnn=(NodeSection)inclNode;
          if (nnn.isRemoved())
            throw new RemoveNoSuchElementException(originalTokens, remover.getOriginalToken(), includedPath);
          ((NodeSection)inclNode).remove();
          found=true;
        }
      }
    }
    return found;
  }
}