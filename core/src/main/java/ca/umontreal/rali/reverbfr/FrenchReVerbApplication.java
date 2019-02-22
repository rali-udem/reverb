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

/* For representing a sentence that is annotated with pos tags and np chunks.*/
import java.util.Locale;
import java.util.Scanner;

import edu.washington.cs.knowitall.nlp.ChunkedSentence;

/* String -> ChunkedSentence */
import edu.washington.cs.knowitall.nlp.OpenNlpSentenceChunker;

/* The class that is responsible for extraction. */
import edu.washington.cs.knowitall.extractor.ReVerbExtractor;

/* A class for holding a (arg1, rel, arg2) triple. */
import edu.washington.cs.knowitall.nlp.extraction.ChunkedBinaryExtraction;

public class FrenchReVerbApplication {
    
    public static void main(String[] args) throws Exception {

        // switch to French
        ReverbConfiguration.setLocale(Locale.FRENCH);

        // Looks on the classpath for the default model files.
        OpenNlpSentenceChunker chunker = new OpenNlpSentenceChunker();

        // Create extractor
        ReVerbExtractor reverb = new ReVerbExtractor();
        
        // loop on sentences on stdin. Use new Scanner(file) to read a file instead.
        Scanner scanner = new Scanner(System.in, "utf-8");
        
        while (scanner.hasNextLine()) {
            String sentStr = scanner.nextLine();
            
            // chunk sentence
            ChunkedSentence sent = chunker.chunkSentence(sentStr);

            // extract with reverb and iterate on results
            for (ChunkedBinaryExtraction extr : reverb.extract(sent)) {
                
                System.out.println("Arg1=" + extr.getArgument1().getText().trim());
                System.out.println("Rel=" + extr.getRelation().getText().trim());
                System.out.println("Arg2=" + extr.getArgument2().getText().trim());
                
                // lemmatization and canonization (optional operations)
                FrenchReverbUtils.addLemmas(extr.getSentence());
                System.out.println("RelLemma=" + FrenchReverbUtils.formatLemmatizedRelation(extr));
                System.out.println("RelCanon=" + FrenchReverbUtils.getCanonicalRelation(extr));
                
                System.out.println();
            }

        }

        scanner.close();
    }
}
