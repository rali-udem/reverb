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

import edu.washington.cs.knowitall.extractor.mapper.FilterMapper;
import edu.washington.cs.knowitall.nlp.ChunkedSentence;
import edu.washington.cs.knowitall.nlp.extraction.ChunkedArgumentExtraction;

/**
 * Prevenir l'utilisation de NP qui sont en fait des c.circ.
 * <p/>
 * Sur les 22 municipalités fusionnées en 2002 qui ont obtenu d'avoir la tenue d'un référendum
 * <p/>
 * Éviter d'utiliser 2002 comme arg de 
 */
public class FrenchPrepositionalPhrase extends
             FilterMapper<ChunkedArgumentExtraction> {

    @Override
    public boolean doFilter(ChunkedArgumentExtraction arg) {
        
        boolean result = true;

        ChunkedSentence sent = arg.getSentence();
        
        if (arg.getStart() > 0) {
            String previousPos = sent.getPosTag(arg.getStart() - 1);
            String previousTok = sent.getToken(arg.getStart() - 1);
            
            result = !(previousPos.equalsIgnoreCase("P") &&
                       previousTok.equalsIgnoreCase("en"));
        }
        
        return result;
    }

}
