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

package edu.washington.cs.knowitall.extractor;

import ca.umontreal.rali.reverbfr.FrenchConjunctionCommaArgumentFilter;
import ca.umontreal.rali.reverbfr.FrenchPrepositionalPhrase;
import ca.umontreal.rali.reverbfr.FrenchRelativePronounFilter;
import edu.washington.cs.knowitall.extractor.mapper.ClosestArgumentMapper;
import edu.washington.cs.knowitall.extractor.mapper.FilterMapper;
import edu.washington.cs.knowitall.extractor.mapper.MapperList;
import edu.washington.cs.knowitall.nlp.extraction.ChunkedArgumentExtraction;

public class FrenchReverbArgument1Mappers extends
    MapperList<ChunkedArgumentExtraction> {

    public FrenchReverbArgument1Mappers() {
        init();
    }

    private void init() {

        // Peut pas être un mot vide de type "C'" 
        addFirstTokenNotEqualsFilter("c'");
        
        // Peut pas être le pronom "qui".
        addMapper(new FrenchRelativePronounFilter());
        
        // Premier argument ne peut pas etre 
        // "ARG1, REL" "ARG1 et REL" or
        // "ARG1, et REL"
        addMapper(new FrenchConjunctionCommaArgumentFilter());
        
        // Peut pas etre precede d'une preposition introduisant prob. un c.circ
        addMapper(new FrenchPrepositionalPhrase());
        
        // First argument should be closest to relation that passes through
        // filters
        addMapper(new ClosestArgumentMapper());
    }
    
    private void addFirstTokenNotEqualsFilter(String token) {
        final String tokenCopy = token;
        addMapper(new FilterMapper<ChunkedArgumentExtraction>() {
            @Override
            public boolean doFilter(ChunkedArgumentExtraction extr) {
                return !extr.getTokens().get(0).equalsIgnoreCase(tokenCopy);
            }
        });
    }

}
