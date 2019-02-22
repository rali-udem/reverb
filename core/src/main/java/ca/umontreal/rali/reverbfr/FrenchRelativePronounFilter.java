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
import edu.washington.cs.knowitall.nlp.extraction.ChunkedArgumentExtraction;

/**
 * Filtre de pronom.
 *
 */
public class FrenchRelativePronounFilter extends
        FilterMapper<ChunkedArgumentExtraction> {

    /**
     * Must return true to keep item.
     */
    @Override
    public boolean doFilter(ChunkedArgumentExtraction extr) {
        return !(extr.getTokens().get(0).equalsIgnoreCase("qui") && 
                 extr.getPosTags().get(0).equalsIgnoreCase("PROREL"));
    }

}
