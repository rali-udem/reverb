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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ca.umontreal.rali.lemmatizer.FrenchLemmatizer;

import com.google.common.collect.ImmutableList;

import edu.washington.cs.knowitall.nlp.ChunkedSentence;
import edu.washington.cs.knowitall.nlp.OpenNlpUtils;
import edu.washington.cs.knowitall.nlp.extraction.ChunkedBinaryExtraction;
import edu.washington.cs.knowitall.util.DefaultObjects;

/**
 * Utilitaires variés.
 *
 */
public class FrenchReverbUtils {

    public static final String LEMMA_LAYER_NAME = "LEM";
    public static final Set<String> VARIANTES_DE = new HashSet<String>(Arrays.asList(new String[] {
            "de", "du", "des", "d'",
    }));

    private static final String LEMMA_FILE = "rali/bac_pre_trie.out";
    
    /** Adverbs useless when relation is canonicalized. */
    private static final HashSet<String> STOP_CANON_WORDS = new HashSet<String>(Arrays.asList(new String[] {
            "également", "aussi", "alors", "donc", "ainsi", 
            "très", "souvent", "notamment", "déjà", "bien", "cependant",
            "toutefois", "peut-être", 
            // == from corpus Montréal
            "pratiquement", "clairement", "réellement",
            "récemment", "graduellement", 
            "publiquement", "véritablement", "traditionnellement",
            "nettement", "indéniablement",
    }));
    
    private static final HashSet<String> VERBS_WITH_ETRE_AUX = new HashSet<String>(Arrays.asList(new String[] {
            "accourir",
            "advenir",
            "aller",
            "apparaître",
            "arriver",
            "décéder",
            "descendre",
            "devenir",
            "entrer",
            "intervenir",
            "monter",
            "mourir",
            "naître",
            "partir",
            "parvenir",
            "redescendre",
            "remonter",
            "rentrer",
            "repartir",
            "ressortir",
            "rester",
            "retomber",
            "retourner",
            "revenir",
            "sortir",
            "survenir",
            "tomber",
            "venir ",
    }));
    
    private static FrenchLemmatizer frenchLemmatizer;
    
    /**
     * Modifies <code>npChunkTags</code> so that NP chunks starting with "de"
     * are merged with the previous NP chunk.
     * <p/>
     * Adapted from {@link OpenNlpUtils#attachOfs(String[], String[])};
     *
     * @param tokens
     * @param npChunkTags
     */
    public static void attachDe(String[] tokens, String[] npChunkTags) {
        for (int i = 1; i < npChunkTags.length - 1; i++) {
            if (VARIANTES_DE.contains(tokens[i]) &&
                    OpenNlpUtils.isInNpChunk(npChunkTags[i - 1]) &&
                    OpenNlpUtils.isInNpChunk(npChunkTags[i + 1])) {
                
                npChunkTags[i] = OpenNlpUtils.IN_NP;
                npChunkTags[i + 1] = OpenNlpUtils.IN_NP;
                
            }
        }
    }

    public static String[] formatSentAndPos(ChunkedSentence sent) {
        String sentence = "";
        String posTags = "";
        String chunkTags = "";
        
        for (int i = 0; i < sent.getLength(); i++) {
            String token = sent.getToken(i);
            String posTag = sent.getPosTag(i);
            String chunkTag = sent.getChunkTag(i);
            
            int maxFieldLength = Math.max(token.length(), posTag.length());
            maxFieldLength = Math.max(maxFieldLength, chunkTag.length());
            String sentFrag = repeat((maxFieldLength -  token.length()) / 2, " ") + token + repeat((maxFieldLength -  token.length() + 1) / 2, " ");
            String posFrag =  repeat((maxFieldLength - posTag.length()) / 2, " ") + posTag + repeat((maxFieldLength - posTag.length() + 1) / 2, " ");
            String chunkFrag =  repeat((maxFieldLength - chunkTag.length()) / 2, " ") + chunkTag + repeat((maxFieldLength - chunkTag.length() + 1) / 2, " ");            
            
            sentence += sentFrag + " ";                 
            posTags += posFrag + " ";
            chunkTags += chunkFrag + " ";
        }
        
        return new String[] { sentence, posTags, chunkTags };
    }
    
    private static String repeat(int i, String string) {
        int nbReps = i;
        StringBuilder result = new StringBuilder();
        while (nbReps-- != 0) {
            result.append(string);
        }
 
        return result.toString();
    }

    /**
     * Removes PONCT from I-NP and B-NP
     * @param tokens
     * @param posTags
     * @param npChunkTags
     */
    public static void fixPonctuationChunks(String[] tokens, String[] posTags,
            String[] npChunkTags) {
        for (int i = 0; i < tokens.length; ++i) {
            // String curToken = tokens[i];
            String pos      = posTags[i];
            String chunk    = npChunkTags[i];
            
            if (pos.equalsIgnoreCase("PONCT")
                    && (chunk.equalsIgnoreCase("I-NP") || chunk.equalsIgnoreCase("B-NP"))) {
                npChunkTags[i] = "O";

                if (i < tokens.length - 1 && npChunkTags[i + 1].matches("I-.*")) {
                    npChunkTags[i + 1] = npChunkTags[i + 1].replaceFirst("I-", "B-");
                }
            }

        }
    }

    /**
     * TOKS     C'  est   la  plus importante ville francophone  d'  Amérique   .
       POS     CLS   V   DET  ADV     ADJ      NC       ADJ      P     NPP    PONCT
       CHNK    B-VN I-VN B-NP I-NP    I-NP    I-NP     B-AP     B-PP   B-NP     O
       
       Must change CLS / B-VN to CLS / B-NP and correct the following

     * @param tokens
     * @param posTags
     * @param npChunkTags
     */
    public static void fixClitics(String[] tokens, String[] posTags,
            String[] npChunkTags) {

        for (int i = 0; i < tokens.length; ++i) {
            // String curToken = tokens[i];
            String pos      = posTags[i];
            String chunk    = npChunkTags[i];
            
            if (pos.equalsIgnoreCase("CLS") && chunk.equalsIgnoreCase("B-VN")) {
                npChunkTags[i] = "B-NP";
                
                if (i < tokens.length - 1 && npChunkTags[i + 1].matches("I-.*")) {
                    npChunkTags[i + 1] = npChunkTags[i + 1].replaceFirst("I-", "B-");
                }
            }
        }
        
    }

    /**
     * Adds a layer of lemmas to the sentence. Won't do anything if
     * the layer already exists.
     * 
     * @param sentence
     */
    public static void addLemmas(ChunkedSentence sentence) {
        if (!sentence.hasLayer(LEMMA_LAYER_NAME)) {
            String[] tokens   = sentence.getTokens().toArray(new String[sentence.getLength()]);
            String[] posTags  = sentence.getPosTags().toArray(new String[sentence.getLength()]);
            FrenchLemmatizer lemmatizer = FrenchReverbUtils.getDefaultFrenchLemmatizer();
            
            String[] lemmas = lemmatizer.lemmatize(tokens, posTags);
            sentence.addLayer(LEMMA_LAYER_NAME, ImmutableList.copyOf(lemmas));
        }
    }

    /**
     * Returns a french lemmatizer.
     * @return
     */
    private synchronized static FrenchLemmatizer getDefaultFrenchLemmatizer() {
        if (frenchLemmatizer == null) {
            // load it
            try {
                Map<String, String[]> lemmaMap = FrenchLemmatizer.loadLemmaMap(FrenchReverbUtils.class.getClassLoader().getResourceAsStream(LEMMA_FILE), false);
                frenchLemmatizer = new FrenchLemmatizer(DefaultObjects.getDefaultTokenizer(), DefaultObjects.getDefaultPosTagger(), lemmaMap);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        
        return frenchLemmatizer;
    }

    /**
     * Extract lemmas for a given relation and joins them with a space.
     * @param extr
     * @return
     */
    public static String formatLemmatizedRelation(ChunkedBinaryExtraction extr) {
        String result = "";
        
        int start = extr.getRelation().getStart();
        int end   = start + extr.getRelation().getLength();
        
        for (int i = start; i < end; ++i) {
            result += extr.getSentence().get(LEMMA_LAYER_NAME, i) + " ";
        }
        
        return result.trim();
    }

    /**
     * Returns the canonical expression of the relation, i.e. lemmatized
     * and (hopefully) stripped of some useless modifier words.
     * 
     * @param extr The relation to canonicalize.
     * @return A canonical version.
     */
    public static String getCanonicalRelation(ChunkedBinaryExtraction extr) {
        String result = "";
        
        ChunkedSentence sent = extr.getSentence();
        addLemmas(sent);
        
        int start = extr.getRelation().getStart();
        int end   = start + extr.getRelation().getLength();
        
        List<String> allLemmas = new ArrayList<String>();
        for (int i = start; i < end; ++i) {
            allLemmas.add(sent.get(LEMMA_LAYER_NAME, i));
        }

        // fix n' -> ne, j' -> je, au -> à
        for (int i = 0; i < allLemmas.size(); ++i) {
            String curLemma = allLemmas.get(i);
            
            String replacement = null;
            switch (curLemma) {
            case "n'":
                replacement = "ne";
                break;
            case "j'":
                replacement = "je";
                break;
            case "qu'":
                replacement = "que";
                break;
            case "au":
                replacement = "à";
                break;
            case "du": // aggressive
                replacement = "de";
                break;             
            default:
                break;
            }
            
            if (replacement != null) {
                allLemmas.set(i, replacement);
            }
        }
        
        /*
         * detect pattern like
            ont V B-VN
            obtenu VPP I-VN

            or 
            
            sont partis
        */
        
        int auxiliaryPos = -1;
        int pastParticiplePos = -1;
        int clitiquePos = -1;
        boolean etreAux = false;
        
        for (int i = start; i < end; ++i) {
            final String curPosTag = sent.getPosTag(i);
            if (curPosTag.startsWith("CL")) {
                clitiquePos = i;
            } else if ((sent.get(LEMMA_LAYER_NAME, i).equalsIgnoreCase("avoir") ||
                        sent.get(LEMMA_LAYER_NAME, i).equalsIgnoreCase("être")) &&
                       curPosTag.equalsIgnoreCase("V")) {
                auxiliaryPos = i;
                etreAux = sent.get(LEMMA_LAYER_NAME, i).equalsIgnoreCase("être");
            } else if (curPosTag.equalsIgnoreCase("VPP")) {
                pastParticiplePos = i;
            }
        }
        
        if (auxiliaryPos >= 0 && pastParticiplePos > auxiliaryPos) {
            boolean deleteAux = true;

            if (etreAux) {
                deleteAux = false;
                
                // supprimer l'auxiliaire etre seulement dans le cas (1) de verbes
                // ne se conjugant qu'avec etre ou dans le cas (2) de tous les verbes
                // pronominaux
                final String participleLemma = sent.get(LEMMA_LAYER_NAME, pastParticiplePos).toLowerCase();
                if (VERBS_WITH_ETRE_AUX.contains(participleLemma)) {
                    deleteAux = true;
                } else if (clitiquePos > 0 && clitiquePos < auxiliaryPos) {
                    deleteAux = true;
                }
            }
            
            if (deleteAux) {
                // nullify position to delete, watch it: 2 coordinate systems
                // one indexed as word pos in the sentence, the other as indices
                // in the lemmas sequence.
                allLemmas.set(auxiliaryPos - start, null); // nullify position to delete
            }
        }
        
        // nullify common useless adverbs/preps
        for (int i = 0; i < allLemmas.size(); ++i) {
            if (STOP_CANON_WORDS.contains(allLemmas.get(i))) {
                allLemmas.set(i, null);
            }
        }
        
        // rearrange ne subir pas de -> ne pas subir de
        if (allLemmas.size() > 1 && allLemmas.get(0) != null && allLemmas.get(0).equals("ne")) {
            int pasIndex = -1;
            String pasString = null;
            for (int i = 2; i < allLemmas.size() && pasIndex == -1; ++i) {
                String curLemma = allLemmas.get(i);
                if (curLemma != null && (curLemma.equals("pas") || curLemma.equals("plus"))) {
                    pasIndex = i;
                    pasString = curLemma;
                }
            }
            
            if (pasIndex >= 0) { // change position of pas
                allLemmas.set(pasIndex, null);
                allLemmas.add(1, pasString);
            }
        }

        // create result
        for (int i = 0; i < allLemmas.size(); ++i) {
            if (allLemmas.get(i) != null) {
                result += allLemmas.get(i) + " ";
            }
        }
        
        result = result.trim();
        
        return result;
    }

}
