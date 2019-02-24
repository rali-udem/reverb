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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import junit.framework.Assert;

import org.junit.Test;

import edu.washington.cs.knowitall.extractor.ReVerbExtractor;
import edu.washington.cs.knowitall.nlp.ChunkedSentence;
import edu.washington.cs.knowitall.nlp.OpenNlpSentenceChunker;
import edu.washington.cs.knowitall.nlp.extraction.ChunkedBinaryExtraction;

/**
 * Test for French version of ReVerb. This test is disabled by default, 
 * because setting the configuration to French using 
 * {@link ReverbConfiguration#setLocale(Locale)} then using the same 
 * method to switch back to English does not work properly. This is most
 * likely due to static final fields initialized when the configuration is
 * in French.
 * <p/>
 * To test this French adaptation, toggle the boolean value disabling the 
 * test and run <strong>only</strong> this test.
 */
public class ReverbFrenchTest {
    
    @Test
    public void test() throws IOException {
        boolean disableTest = true;
        
        if (!disableTest) {
            
            // set up
            ReverbConfiguration.setLocale(Locale.FRENCH);
            OpenNlpSentenceChunker chunker = new OpenNlpSentenceChunker();
            ReVerbExtractor reverb = new ReVerbExtractor();
    
            // read data
            InputStream testFile = ReverbFrenchTest.class.getResourceAsStream("/reverb-fr.txt");
            Scanner scanner = new Scanner(testFile, "utf-8");
            List<String> lines = new ArrayList<String>();
            
            while (scanner.hasNextLine()) {
                lines.add(scanner.nextLine());
            }
            
            scanner.close();
            testFile.close();
    
            // do test
            for (int i = 0; i < lines.size(); ++i) {
                String curLine = lines.get(i);
                Assert.assertTrue(curLine.startsWith("Sent="));
                String curSent = curLine.substring(5);
                
                ChunkedSentence sent = chunker.chunkSentence(curSent);
                
                // Prints out extractions from the sentence.
                for (ChunkedBinaryExtraction extr : reverb.extract(sent)) {
                    
                    Assert.assertEquals(lines.get(++i), "Arg1=" + extr.getArgument1());
                    Assert.assertEquals(lines.get(++i), "Rel=" + extr.getRelation());
                    
                    FrenchReverbUtils.addLemmas(extr.getSentence());
                    Assert.assertEquals(lines.get(++i), "(" + FrenchReverbUtils.formatLemmatizedRelation(extr) + ")");
                    Assert.assertEquals(lines.get(++i), "Canon: (" + FrenchReverbUtils.getCanonicalRelation(extr) + ")");
                    
                    Assert.assertEquals(lines.get(++i), "Arg2=" + extr.getArgument2());                
                }
                
            }
        
        }
    }
}
