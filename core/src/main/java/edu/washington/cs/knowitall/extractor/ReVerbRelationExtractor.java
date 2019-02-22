package edu.washington.cs.knowitall.extractor;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import ca.umontreal.rali.reverbfr.ReverbConfiguration;

import com.google.common.collect.Iterables;

import edu.washington.cs.knowitall.extractor.mapper.ReVerbRelationDictionaryFilter;
import edu.washington.cs.knowitall.extractor.mapper.ReVerbRelationMappers;
import edu.washington.cs.knowitall.nlp.ChunkedSentence;
import edu.washington.cs.knowitall.nlp.ChunkedSentenceReader;
import edu.washington.cs.knowitall.nlp.extraction.ChunkedBinaryExtraction;
import edu.washington.cs.knowitall.nlp.extraction.ChunkedExtraction;
import edu.washington.cs.knowitall.sequence.SequenceException;
import edu.washington.cs.knowitall.util.DefaultObjects;


public abstract class ReVerbRelationExtractor extends RelationFirstNpChunkExtractor {

    /**
     * Definition of the "verb" of the relation pattern.
     */
    public static final String VERB = ReverbConfiguration.isEn() ?
        // Optional adverb
        "RB_pos? " +
        // Modal or other verbs
        "[MD_pos VB_pos VBD_pos VBP_pos VBZ_pos VBG_pos VBN_pos] " +
        // Optional particle/adverb
        "RP_pos? RB_pos?" :
        
        // ============ FRENCH
        // Attention au blanc apres chaque morceau de string sauf le dernier
            
        // Adverbe optionnel
        "ADV_pos? " +
        // clitique reflexif ou objet (se, s', nous, etc.)
        "[CLO_pos CLR_pos]? " +
        // Verbe + participe passé? + ADV + participe passé?   verbe infinitif
        "[V_pos] VPP_pos? ADV_pos? VPP_pos? (VINF_pos|P_pos VINF_pos)? " +
        // Adverbe optionnel
        "ADV_pos? " +
        // Some prepositions
        "[P_pos P+D_pos]?";

    /**
     * Definition of the "non-verb/prep" part of the relation pattern.
     */
    public static final String WORD =         
        "[$_pos PRP$_pos CD_pos DT_pos JJ_pos JJS_pos JJR_pos NN_pos " +
        "NNS_pos NNP_pos NNPS_pos POS_pos PRP_pos RB_pos RBR_pos RBS_pos " +
        "VBN_pos VBG_pos]";

    /**
     * Definition of the "preposition" part of the relation pattern.
     */
    public static final String PREP = "RB_pos? [IN_pos TO_pos RP_pos] RB_pos?";


    /**
     * The pattern (V(W*P)?)+
     */
    public static final String LONG_RELATION_PATTERN =
        String.format("(%s (%s* (%s)+)?)+", VERB, WORD, PREP);

    /**
     * The pattern (VP?)+
     */
    public static final String SHORT_RELATION_PATTERN =
        String.format("(%s (%s)?)+", VERB, PREP);
    
    /**
     * French simple pattern
     */
    public static final String FRENCH_RELATION_PATTERN_01 =
            String.format("(%s)+", VERB);

    /**
     * Constructs a new extractor using the default relation pattern,
     * relation mappers, and argument mappers.
     * @throws ExtractorException if unable to initialize the extractor
     */
    public ReVerbRelationExtractor() throws ExtractorException {
        initializeRelationExtractor();
        initializeArgumentExtractors();
    }


    /**
     * Constructs a new extractor using the default relation pattern,
     * relation mappers, and argument mappers.
     * @param minFreq - The minimum distinct arguments to be observed in a large collection for the relation to be deemed valid.
     * @param useLexSynConstraints - Use syntactic and lexical constraints that are part of Reverb?
     * @param mergeOverlapRels - Merge overlapping relations?
     * @param allowUnary - Allow relations with one argument to be output.
     * @throws ExtractorException if unable to initialize the extractor
     */
    public ReVerbRelationExtractor(int minFreq, boolean useLexSynConstraints, boolean mergeOverlapRels, boolean allowUnary) throws ExtractorException {
        initializeRelationExtractor(minFreq, useLexSynConstraints, mergeOverlapRels, allowUnary);
        initializeArgumentExtractors();
    }

    protected abstract void initializeArgumentExtractors();

    /**
     * Wrapper for default initialization of the reverb relation extractor.
     * Use lexical and syntactic constraints, merge overlapping relations,require a minimum of 20 distinct arguments for support, and
     * do not allow unary relations.
     * @throws ExtractorException
     */
    protected void initializeRelationExtractor() throws ExtractorException {

        initializeRelationExtractor(ReVerbRelationDictionaryFilter.defaultMinFreq, true, true, false);
    }

    /**
     * Initialize relation extractor.
     * @param minFreq - The minimum distinct arguments to be observed in a large collection for the relation to be deemed valid.
	 * @param useLexSynConstraints - Use syntactic and lexical constraints that are part of Reverb?
	 * @param mergeOverlapRels - Merge overlapping relations?
	 * @param allowUnary - Allow relations with one argument to be output.
     * @throws ExtractorException if unable to initialize the extractor
     */
    protected void initializeRelationExtractor(int minFreq, boolean useLexSynConstraints, boolean mergeOverlapRels, boolean allowUnary) throws ExtractorException {

        ExtractorUnion<ChunkedSentence, ChunkedExtraction> relExtractor =
            new ExtractorUnion<ChunkedSentence, ChunkedExtraction>();

        if (ReverbConfiguration.isEn()) {
            try {
                relExtractor.addExtractor(new RegexExtractor(SHORT_RELATION_PATTERN));
            } catch (SequenceException e) {
                throw new ExtractorException(
                    "Unable to initialize short pattern extractor", e);
            }
    
            try {
                relExtractor.addExtractor(new RegexExtractor(LONG_RELATION_PATTERN));
            } catch (SequenceException e) {
                throw new ExtractorException(
                    "Unable to initialize long pattern extractor", e);
            }
        }
        
        if (ReverbConfiguration.isFr()) {
            try {
                relExtractor.addExtractor(new RegexExtractor(FRENCH_RELATION_PATTERN_01));
            } catch (SequenceException e) {
                throw new ExtractorException(
                    "Unable to initialize French relation pattern 01", e);
            }
        }
        
        try {
            if (ReverbConfiguration.isEn()) {
                relExtractor.addMapper(new ReVerbRelationMappers(minFreq, useLexSynConstraints, mergeOverlapRels));
            } else {
                relExtractor.addMapper(new ReVerbRelationMappers(0, false, true));
            }
	    } catch (IOException e) {
            throw new ExtractorException(
                "Unable to initialize relation mappers", e);
        }

        // Hack to allow unary relations.
        super.setAllowUnary(allowUnary);

        setRelationExtractor(relExtractor);
    }

    /**
     * Extracts from the given text using the default sentence reader returned
     * by {@link DefaultObjects#getDefaultSentenceReader(java.io.Reader)}.
     * @param text
     * @return an iterable object over the extractions
     * @throws ExtractorException if unable to extract
     */
    public Iterable<ChunkedBinaryExtraction> extractFromString(String text)
        throws ExtractorException {
        try {
            StringReader in = new StringReader(text);
            return extractUsingReader(
                DefaultObjects.getDefaultSentenceReader(in));
        } catch (IOException e) {
            throw new ExtractorException(e);
        }

    }

    /**
     * Extracts from the given html using the default sentence reader returned
     * by {@link DefaultObjects#.getDefaultSentenceReaderHtml(java.io.Reader)}.
     * @param html
     * @return an iterable object over the extractions
     * @throws ExtractorException if unable to extract
     */
    public Iterable<ChunkedBinaryExtraction> extractFromHtml(String html)
        throws ExtractorException {
        StringReader in = new StringReader(html);
        try {
            return extractUsingReader(
                DefaultObjects.getDefaultSentenceReaderHtml(in));
        } catch (IOException e) {
            throw new ExtractorException(e);
        }
    }

    /**
     * Extracts from the given reader
     * @param reader
     * @return extractions
     * @throws ExtractorException if unable to extract
     */
    private Iterable<ChunkedBinaryExtraction> extractUsingReader(
            ChunkedSentenceReader reader) throws ExtractorException {

        ArrayList<ChunkedBinaryExtraction> results =
            new ArrayList<ChunkedBinaryExtraction>();

        Iterable<ChunkedSentence> sents =
            reader.getSentences();
        for (ChunkedSentence sent : sents) {
            Iterables.addAll(results, extract(sent));
        }
        return results;
    }
}

