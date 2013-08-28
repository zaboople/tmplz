package org.tmotte.tmplz.parse;
import java.util.HashMap;
import java.util.Map;
import org.tmotte.tmplz.exception.InternalException;
import org.tmotte.tmplz.exception.parse.TemplateNestedException;
import org.tmotte.tmplz.node.CatNodeSlot;
import org.tmotte.tmplz.node.CatNodeStatic;
import org.tmotte.tmplz.node.CatNodeBetween;
import org.tmotte.tmplz.parse.dum.DocumentBuilder;
import org.tmotte.tmplz.parse.dum.Node;
import org.tmotte.tmplz.parse.dum.NodeBetween;
import org.tmotte.tmplz.parse.dum.NodeList;
import org.tmotte.tmplz.parse.dum.NodeSection;
import org.tmotte.tmplz.parse.dum.NodeSlot;
import org.tmotte.tmplz.parse.dum.NodeStatic;
import org.tmotte.tmplz.parse.tokenize.DocumentTokenizer;
import org.tmotte.tmplz.parse.tokenize.TokenList;
import org.tmotte.tmplz.parse.preprocess.Preprocessor;
import org.tmotte.tmplz.Section;

public class TemplateBuilder {

  ////////////
  // SETUP: //
  ////////////

  private DocumentTokenizer tokenizer;
  private Preprocessor preprocessor;

  public void setPreprocessor(Preprocessor p) {
    this.preprocessor=p;
  }
  public void setTokenizer(DocumentTokenizer t) {
    this.tokenizer=t;
  }
  private void init() {
    if (preprocessor==null || tokenizer==null)
      throw new IllegalStateException("Preprocessor or Tokenizer was null");
  }

  /////////////////////
  // PUBLIC METHODS: //
  /////////////////////

  public void load(TextSource textSource, Section baseTemplate) {
    init();
    baseTemplate.clear();
    try {
      NodeList nodes=DocumentBuilder.getNodes(textSource, tokenizer, preprocessor, false, null);
      doLoad(nodes,  baseTemplate);
    } catch (Exception e) {
      textSource.resetOnError();
      throw new TemplateNestedException(textSource.getPath(), e);
    }
  }

  ///////////////////////
  // INTERNAL METHODS: //
  ///////////////////////
  
  private void doLoad(NodeList nodes, Section section) {
    if (nodes==null)
      return;
    for (int i=0; i<nodes.size(); i++){
      Node node=nodes.get(i);
      int nodeType=node.getType();
      if (nodeType==Types.STATIC){
        NodeStatic ns=(NodeStatic)node;
        int trimLevel=ns.getDoAggressiveTrim() 
          ?CatNodeStatic.TRIM :CatNodeStatic.NOTRIM;
        section.add(new CatNodeStatic(trimLevel, ns.getText())); 
      }
      else
      if (nodeType==Types.SLOT) 
        section.add(
          new CatNodeSlot(
            ((NodeSlot)node).getName()
          )
        );
      else
      if (nodeType==Types.SECTION){
        NodeSection dNode=(NodeSection)node;
        Section newSection=new Section(dNode.getName());
        section.add(newSection);
        doLoad(dNode.getNodes(), newSection);
      }
      else
      if (nodeType==Types.BETWEEN) {
        NodeBetween nb=(NodeBetween)node;
        NodeList bNodes=nb.getNodes();
        boolean visible=nb.getVisible();
        if (bNodes.size()==1){
          Node bNode=bNodes.get(0);
          if (bNode.getType()!=Types.STATIC)
            throw new InternalException("Unexpected node type: "+bNode.getType()+bNode);
          section.add(new CatNodeBetween(((NodeStatic)bNode).getText(), visible));
        }
        else {
          StringBuilder sb=new StringBuilder(); 
          for (int k=0; k<bNodes.size(); k++){
            Node bNode=bNodes.get(0);
            if (bNode.getType()!=Types.STATIC)
              throw new InternalException("Unexpected node type: "+bNode.getType()+bNode);
            sb.append(((NodeStatic)bNode).getText());
          }
          section.add(new CatNodeBetween(sb.toString(), visible));
        }
      }
      else
        throw new InternalException("Unexpected node type "+node.getClass());
    }
  }
  
}