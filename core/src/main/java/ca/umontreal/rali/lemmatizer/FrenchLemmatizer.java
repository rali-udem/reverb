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

package ca.umontreal.rali.lemmatizer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import opennlp.tools.postag.POSTagger;
import opennlp.tools.tokenize.Tokenizer;

/**
 * A simple lemmatizer based on 2 resources: OpenNLP's French pos tagger +
 * lemma dictionary BDF. Ambiguities are lifted using the tagger's proposed tag.
 * <p/>
 * This could easily be improved if we kept a separate data structure (map) for
 * ambiguous inflected forms. Unambiguous would be a simple lookup.
 *
 */
public class FrenchLemmatizer {
    
    private static final Matcher LINE_MATCHER = Pattern.compile("(.*)\\t(.*) *<([^,]+)(,([^,]+))?.*>").matcher("");
    private static final Map<String, String[]> BDF2HERNANDEZ_POSMAP = new HashMap<String, String[]>();

    private Tokenizer tokenizer;
    private POSTagger posTagger;
    private Map<String, String[]> lemmaMap;

    // very approximative
    static {
        String[][] v = new String[][] {
                {"AdjQ"}, {"ADJ", "ADJWH"},
                {"Adve"}, {"ADV", "ADVWH"}, 
                {"ConC"}, {"CC"},
                {"ConS"}, {"CS"},
                {"Dete"}, {"DET", "DETWH"},
                {"Inte"}, {"I"},
                {"NomC"}, {"NC"},
                {"Num"}, {"DET"},
                {"Ordi"}, {"DET"},
                {"PreD"}, {"P"},
                {"PreN"}, {"P"},
                {"Prep"}, {"P"},
                {"Pron"}, {"PRO", "PROREL", "PROWH", "CLS", "CLO", "CLR"},
                {"Verb"}, {"V", "VIMP", "VINF", "VPP", "VPR", "VS"},
        };

        for (int i = 0; i < v.length; i += 2) {
            BDF2HERNANDEZ_POSMAP.put(v[i][0].toLowerCase(), lowerCase(v[i + 1]));
        }
    }
    
    /**
     * New lemmatizer.
     * 
     * @param tokenizer Sentence tokenizer.
     * @param posTagger POS tagger (French) - See N. Hernandez's work.
     * @param lemmaMap  map <inflected_form> -> array of <POS, lemma>, repeated
     */
    public FrenchLemmatizer(Tokenizer tokenizer, 
                            POSTagger posTagger, 
                            Map<String, String[]> lemmaMap) {
        this.tokenizer = tokenizer;
        this.posTagger = posTagger;
        this.lemmaMap = lemmaMap;
    }
    
    
    private static String[] lowerCase(String[] strings) {
        String[] result = new String[strings.length];
        for (int i = 0; i < strings.length; i++) {
            String string = strings[i];
            result[i] = string.toLowerCase();
        }
        return result;
    }

    /**
     * Parses BDF's lemma file, of format
     * <pre>
       démangeante     démanger &lt;Verb,ParPré,femi,sing&gt;
       démangeantes    démanger &lt;Verb,ParPré,femi,plur&gt;
       mangeais        manger &lt;Verb,IndImp,sing,p1&gt;
       mangeais        manger &lt;Verb,IndImp,sing,p2&gt;
       </pre>

        <p/>
        The POS tags are reconciled with those produced by N. Hernandez's tagger, i.e.
        <pre>
        ADJ,  ADJWH, 
        ADV, ADVWH
        CC
        CLO, CLR, CLS, 
        CS
        DET, DETWH,
        ET
        I
        NC
        NPP
        P, P+D
        PONCT
        PREF
        PRO, PROREL, PROWH
        U
        V
        VIMP, VINF, VPP, VPR, VS
        </pre>
        
     * @param file The file containing the info.
     * @return The lemma map, as explained in {@link FrenchLemmatizer#FrenchLemmatizer(Tokenizer, POSTagger, Map)}.
     * @throws IOException
     */
    public static Map<String, String[]> loadLemmaMap(File file) throws IOException {
        FileInputStream inStm = new FileInputStream(file);
        Map<String, String[]> res = loadLemmaMap(inStm, true);
        inStm.close();
        return res;
    }


    /**
     * @param inStm
     * @param textFormat 
     * @return
     */
    public static Map<String, String[]> loadLemmaMap(InputStream inStm, boolean textFormat) {
        return textFormat ? readTextFormat(inStm) : readBinary(inStm);
    }


    /**
     * @param inStm
     * @return The map or <code>null</code> iff problem.
     */
    @SuppressWarnings("unchecked")
    private static Map<String, String[]> readBinary(InputStream inStm) {
        Map<String, String[]> result = null;
        try {
            ObjectInputStream stream = new ObjectInputStream(inStm);
            result = (Map<String, String[]>) stream.readObject();
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return result;
    }


    /**
     * @param inStm
     * @return
     */
    public static Map<String, String[]> readTextFormat(InputStream inStm) {
        Map<String, String[]> result = new HashMap<String, String[]>();

        Scanner scanner = new Scanner(inStm, "iso-8859-1");
        String line = null;
        while (scanner.hasNext()) {
            line = scanner.nextLine();
            
            LINE_MATCHER.reset(line);
            if (!LINE_MATCHER.matches()) {
                scanner.close();
                throw new RuntimeException("Invalid line " + line);
            }
            
            String flex =  LINE_MATCHER.group(1).trim().toLowerCase();
            String lemma = LINE_MATCHER.group(2).trim().toLowerCase();
            String pos  =  LINE_MATCHER.group(3).trim().toLowerCase();
            @SuppressWarnings("unused")
            String extraPos = null;
            
            // quick rule
            if (lemma.equalsIgnoreCase("le/la")) {
                lemma = "le";
            }
            
            if (LINE_MATCHER.groupCount() == 5 && LINE_MATCHER.group(5) != null) {
                    extraPos = LINE_MATCHER.group(5).trim().toLowerCase();
            }
            
            if (!pos.equals("ltre")) { // skip letters (a,b,c,)
                
                String[] valArray = null;
                int newStartPos = 0;
                
                if (result.containsKey(flex)) {
                    valArray = result.get(flex);
                    // extend
                    String[] newValArray = new String[valArray.length + 2];
                    System.arraycopy(valArray, 0, newValArray, 0, valArray.length);
                    newStartPos = valArray.length;
                    valArray = newValArray;
                } else {
                    valArray = new String[2];
                }
                
                valArray[newStartPos] = pos;
                valArray[newStartPos + 1] = lemma;
                
                result.put(flex, valArray);
            }
        }

        scanner.close();
        
        return result ;
    }
    

    /**
     * Returns lemmas for sentence.
     * @param sent
     * @return
     */
    public String[] lemmatize(String sent) {
        // 1. Tokenize
        String[] toks = tokenizer.tokenize(sent);
        // 2. POS
        String[] pos = posTagger.tag(toks);
        // 3. Lemmatize
        String[] result = lemmatize(toks, pos);

        return result;
    }

    /**
     * Returns for lemmas for sentence, whose tokens and postags are 
     * specified. 
     * 
     * @param toks
     * @param pos
     * @return
     */
    public String[] lemmatize(String[] toks, String[] pos) {
        String[] result = new String[toks.length];

        for (int i = 0; i < result.length; ++i) {
            result[i] = lemmatizeSingleToken(toks[i], pos[i]);
        }
        
        return result;
    }

    /**
     *  Find lemma for Token tok with part of speech pos.
     *  
     * @param tok
     * @param pos
     * @return The lemma, or null if not found. The lemma is always in 
     *         lower case.
     */
    private String lemmatizeSingleToken(String tok, String pos) {
        String result = tok; // fall back (aggressive)
        
        if (tok == null || pos == null) {
            throw new IllegalArgumentException();
        }
        
        String key = tok.trim().toLowerCase();

        // special cases
        if (tok.equals("suis")) {
            return "être";
        }
        
        // if lemma is single or multiple identical lemmas, skip disambig
        if (lemmaMap.containsKey(key)) {
            String[] val = lemmaMap.get(key);
            
            if (val.length > 2) {
                
                if (allTheSame(val)) {
                    result = val[1];
                } else { // real ambiguity, here
                    boolean found = false;
                    
                    for (int i = 0; i < val.length && !found; i += 2) {
                        if (arePosCompatible(val[i], pos)) {
                            result = val[i + 1];
                            found = true;
                        }
                    }
                    
                    if (!found) {
                        // pick the first one, too bad
                        result = val[1];
                    }
                }            
                
            } else {
                result = val[1];
            }
        }
        
        return result.toLowerCase();
    }


    /**
     * Returns true iff all odd-indexed values in val are the same.
     * Case insensitive comparison.
     * 
     * @param val
     * @return
     */
    private boolean allTheSame(String[] val) {
        boolean result = true;
        
        String currentValue = val[1];
        
        for (int i = 1; i < val.length && result; i += 2) {
            result = val[i].equalsIgnoreCase(currentValue);
            currentValue = val[i];
        }
        
        return result;
    }


    /**
     * Returns true iff the POS tag from the bdf ({@code bdfPos}) is compatible
     * with the OpenNlp pos ({@code openPos}).
     * 
     * @param bdfPos
     * @param openPos
     * @return
     */
    private boolean arePosCompatible(String bdfPos, String openPos) {
        boolean result = false;
        
        if (BDF2HERNANDEZ_POSMAP.containsKey(bdfPos)) {
            String[] compatibleOpenPos = BDF2HERNANDEZ_POSMAP.get(bdfPos);
            for (int i = 0; i < compatibleOpenPos.length && !result; i++) {
                result = compatibleOpenPos[i].equalsIgnoreCase(openPos);
            }
        } else {
            throw new IllegalStateException("Cannot find bdf pos " + bdfPos);
        }
        
        return result;
    }
    
    public void serializeMap(File outputFile) throws IOException {
        ObjectOutputStream outStream = new ObjectOutputStream(new FileOutputStream(outputFile));
        outStream.writeObject(lemmaMap);
        outStream.close();
    }
    
    public static Map<String, String[]> deserializeMap(File inputFile) throws IOException, ClassNotFoundException {
        ObjectInputStream inStream = new ObjectInputStream(new FileInputStream(inputFile));
        @SuppressWarnings("unchecked")
        Map<String, String[]> result = (Map<String, String[]>) inStream.readObject();
        inStream.close();
        return result;
    }

}