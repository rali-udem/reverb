/**
 * This file is part of a French adaptation of ReVerb. 
 * </p>
 * The latter was developed at the University of Washington's Turing Center as 
 * part of the KnowItAll Project.
 * </p>
 * See <a href="http://reverb.cs.washington.edu/">this page</a> for more 
 * information on ReVerb.
 * <p/>
 * This adaptation to the French language was created by Philippe Langlais
 * and Fabrizio Gotti, at RALI, the Laboratory for Applied Research in 
 * Computational Linguistics, at the Université de Montréal. See 
 * <a href='http://dx.doi.org/10.1111/coin.12120'>http://dx.doi.org/10.1111/coin.12120</a>
 * or <a href='http://rali.iro.umontreal.ca/rali/node/1553'>http://rali.iro.umontreal.ca/rali/node/1553</a>
 * for the corresponding publication.
 */

package ca.umontreal.rali.reverbfr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.Span;
import edu.washington.cs.knowitall.util.DefaultObjects;

/**
 * Patches oversights like splitting ; from word.
 * 
 */
public class PatchedFrenchTokenizer implements Tokenizer {
    
    private Tokenizer baseTokenizer = null;
    
    public PatchedFrenchTokenizer() {
        try {
            baseTokenizer = new TokenizerME(new TokenizerModel(DefaultObjects.getResourceAsStream(DefaultObjects.tokenizerModelFile)));
        } catch (InvalidFormatException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String[] tokenize(String s) {
        Span[] tokSpans = tokenizePos(s);
        String[] result = new String[tokSpans.length];
        
        for (int i = 0; i < result.length; i++) {
            result[i] = s.substring(tokSpans[i].getStart(), tokSpans[i].getEnd());
        }
        
        return result;
    }

    @Override
    public Span[] tokenizePos(String s) {
        Span[] tokSpans = baseTokenizer.tokenizePos(s);
        ArrayList<Span> newSpans = null;
        
        for (int i = 0; i < tokSpans.length; ++i) {
            final Span curSpan = tokSpans[i];
            
            if ((s.charAt(curSpan.getEnd() - 1) == ';' || s.charAt(curSpan.getEnd() - 1) == ':') && 
                curSpan.length() > 1) {
                
                if (newSpans == null) {
                    newSpans = new ArrayList<Span>();
                    newSpans.addAll(Arrays.asList(Arrays.copyOfRange(tokSpans, 0, i)));
                }
                
                Span newSpan = new Span(curSpan.getStart(), curSpan.getEnd() - 1);
                newSpans.add(newSpan);
                newSpan = new Span(curSpan.getEnd() - 1, curSpan.getEnd());
                newSpans.add(newSpan);
                
            } else if (newSpans != null) {
                newSpans.add(tokSpans[i]);
            }
        }
        
        Span[] result = tokSpans;
        
        if (newSpans != null) {
            result = newSpans.toArray(new Span[newSpans.size()]);
        }
        
        return result;
    }

}
