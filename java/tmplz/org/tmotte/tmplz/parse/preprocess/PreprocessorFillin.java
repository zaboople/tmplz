package org.tmotte.tmplz.parse.preprocess;
import java.util.List;
import org.tmotte.tmplz.exception.InternalException;
import org.tmotte.tmplz.parse.dum.Node;
import org.tmotte.tmplz.parse.dum.NodeFillin;
import org.tmotte.tmplz.parse.dum.NodeList;
import org.tmotte.tmplz.parse.dum.NodeSection;
import org.tmotte.tmplz.parse.dum.NodeSlot;
import org.tmotte.tmplz.parse.Types;

class PreprocessorFillin {

  /**
   * This converts every matching NodeSlot in includeNodes to the contents of fillin. 
   */
  protected static boolean fillin(NodeFillin fillin, NodeList includeNodes){
    boolean filled=fillin(fillin.getNames(), 0, fillin.getNodes(), includeNodes);
    if (filled)
      fillin.setFound();
    return filled;
  }
  
  /**
   * @param names This is from the period-delimited list of names provided by the Fillin tag. Most of the time, there
   *    will be only one name without periods, but with period-delimited names, we have to drill down thru Sections
   *    until we get to the one we want to fillin.
   */
  private static boolean fillin(List<String> names, int currIndex, NodeList fillinNodes, NodeList includeNodes){  
    boolean found=false;
    String name=names.get(currIndex);
    if (currIndex<names.size()-1) {
      for (int k=0; k<includeNodes.size(); k++){
        Node inclNode=includeNodes.get(k);
        int otherType=inclNode.getType();
        if (otherType==Types.SECTION){
          NodeSection ns=(NodeSection)inclNode;
          if (ns.getName().equals(name))
            found|=fillin(names, currIndex+1, fillinNodes, ns.getNodes());
        }
      }
    }
    else
      //End of the line, do the actual fillin:
      for (int k=0; k<includeNodes.size(); k++){
        Node inclNode=includeNodes.get(k);
        int otherType=inclNode.getType();
        if (otherType==Types.SLOT && ((NodeSlot)inclNode).getName().equals(name)){
          k=includeNodes.replace(k, fillinNodes);
          found=true;
        }
      }
    return found;
  }

  
}