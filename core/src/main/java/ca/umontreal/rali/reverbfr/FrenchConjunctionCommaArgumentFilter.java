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
import edu.washington.cs.knowitall.nlp.extraction.ChunkedExtraction;

/**
 * An argument filter that filters out any arguments matching the following patterns:
 * <ul>
 * <li><code>ARG , REL</code></li>
 * <li><code>ARG and REL</code></li>
 * <li><code>ARG , and REL</code></li>
 * </ul>
 * @author afader
 *
 */
public class FrenchConjunctionCommaArgumentFilter extends FilterMapper<ChunkedArgumentExtraction> {

    @Override
    public boolean doFilter(ChunkedArgumentExtraction arg) {
        ChunkedExtraction rel = arg.getRelation();
        ChunkedSentence sent = arg.getSentence();
        int relStart = rel.getStart();
        int argEnd = arg.getStart() + arg.getLength();
        int sentLen = sent.getLength();

        // Can't match "ARG , REL"
        if (argEnd < sentLen - 1 && sent.getTokens().get(argEnd).equals(",") && relStart == argEnd + 1) {
            return false;
        }

        // Can't match "ARG et REL"
        if (argEnd < sentLen - 1 && sent.getTokens().get(argEnd).equals("et") && relStart == argEnd + 1) {
            return false;
        }

        // Can't match "ARG, and REL"
        if (argEnd < sentLen - 2 && sent.getTokens().get(argEnd).equals(",") && sent.getTokens().get(argEnd + 1).equals("et") && relStart == argEnd + 2) {
            return false;
        }

        return true;

    }

}
