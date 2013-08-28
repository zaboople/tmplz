package org.tmotte.tmplz.parse.tokenize;
import org.tmotte.tmplz.parse.Types;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import org.tmotte.tmplz.exception.InternalException;
import org.tmotte.common.text.DelimitedString;
import org.tmotte.common.text.StringChunker;

class DocumentTrimmer {

  private static Pattern regexCRBlanksBefore=Pattern.compile("(\r\n|\r|\n) *\\z", Pattern.MULTILINE),
                         regexBlanksBefore  =Pattern.compile("^ *\\z", Pattern.MULTILINE), 
                         regexBlanksAfter   =Pattern.compile("\\A *$", Pattern.MULTILINE),
                         regexBlanksCRAfter =Pattern.compile("\\A *(\r\n|\r|\n)", Pattern.MULTILINE);

  public static void trim(TokenList tokens) {
  
    //Setup value holders:
    StringChunker s1=new StringChunker(), 
                  s3=new StringChunker();
    int size=tokens.size();
    Token[] targets=new Token[5];
    int[] targetTypes=new int[5];
    boolean[] wasTrimmed={false,false,false,false,false};
    
    //Now loop
    for (int i=0; i<size+2; i++) {
    
      //Reset list:
      for (int q=0; q<targets.length-1; q++){
        targets[q]    =targets[q+1];
        targetTypes[q]=targetTypes[q+1];
        wasTrimmed[q] =wasTrimmed[q+1];
      }
      targets[targets.length-1]    =i<size ?tokens.get(i)                       :null;
      targetTypes[targets.length-1]=i<size ?targets[targets.length-1].getType() :-1;
      wasTrimmed[3]=false;


      //Must be not-null in middle, with static or null text before and after:
      if ((targets[1]!=null && targetTypes[1]!=Types.STATIC) ||
          (targets[2]==null)                                 || 
          (targets[3]!=null && targetTypes[3]!=Types.STATIC))
        continue;
      TokenStatic ts1=(TokenStatic)targets[1],
                  ts3 =(TokenStatic)targets[3];

      //Make sure line is blank up to tag:
      if (ts1!=null)
        s1.reset(ts1.getText());
      boolean emptyBefore=
         ts1==null
       ||wasTrimmed[1]  //Was trimmed as someone's after-tag.
       ||s1.find(regexCRBlanksBefore)
       ||targets[0]==null && s1.find(regexBlanksBefore);
      if (!emptyBefore)
        continue;

      //Make sure line is blank after tag:
      if (ts3!=null)
        s3.reset(ts3.getText());
      if (ts3!=null && !s3.find(regexBlanksCRAfter))
        continue;
        
      if (Types.memberOf(targetTypes[2], 
                         Types.SECTION|Types.SLOT|Types.TAG_WITH|Types.TRIM|Types.INCLUDE|Types.FILLIN|Types.ATTR_SECTION_2)){

        //Clear blanks to the tag, and line breaks after tag:
        if (ts1!=null){
          s1.reset(ts1.getText());
          if (s1.find(regexBlanksBefore))
            ts1.setText(s1.getUpTo());
        }
        if (ts3!=null){
          ts3.setText(s3.getRest());
          wasTrimmed[3]=true;
        }
      }

    }//for
  }
}