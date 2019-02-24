package edu.washington.cs.knowitall.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import opennlp.tools.chunker.Chunker;
import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTagger;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import ca.umontreal.rali.reverbfr.PatchedFrenchTokenizer;
import ca.umontreal.rali.reverbfr.ReverbConfiguration;
import edu.washington.cs.knowitall.extractor.HtmlSentenceExtractor;
import edu.washington.cs.knowitall.extractor.SentenceExtractor;
import edu.washington.cs.knowitall.extractor.mapper.BracketsRemover;
import edu.washington.cs.knowitall.extractor.mapper.SentenceEndFilter;
import edu.washington.cs.knowitall.extractor.mapper.SentenceLengthFilter;
import edu.washington.cs.knowitall.extractor.mapper.SentenceStartFilter;
import edu.washington.cs.knowitall.nlp.ChunkedSentenceReader;

public class DefaultObjects {

    private static final String FRENCH_RES_DIRECTORY = "opennlp/fr-hernandez/";

    /** Default singleton objects */
    private static BracketsRemover BRACKETS_REMOVER;
    private static SentenceStartFilter SENTENCE_START_FILTER;
    private static SentenceEndFilter SENTENCE_END_FILTER;

    public static InputStream getResourceAsStream(String resource)
            throws IOException {
        InputStream in = DefaultObjects.class.getClassLoader()
                .getResourceAsStream(resource);
        if (in == null) {
            throw new IOException("Couldn't load resource: " + resource);
        } 
        return in;
    }

    public static void initializeNlpTools() throws IOException {
        getDefaultSentenceDetector();
        getDefaultTokenizer();
        getDefaultPosTagger();
        getDefaultChunker();
    }

    public static Tokenizer getDefaultTokenizer() throws IOException {
        return ReverbConfiguration.isEn() ? 
               new TokenizerME(new TokenizerModel(getResourceAsStream(DefaultObjects.getTokenizermodelfile()))) :
               new PatchedFrenchTokenizer();
    }

    public static POSTagger getDefaultPosTagger() throws IOException {
        return new POSTaggerME(new POSModel(
                getResourceAsStream(getTaggermodelfile())));
    }

    public static Chunker getDefaultChunker() throws IOException {
        return new ChunkerME(new ChunkerModel(
                getResourceAsStream(getChunkermodelfile())));
    }

    public static SentenceDetector getDefaultSentenceDetector()
            throws IOException {
        return new SentenceDetectorME(new SentenceModel(
                getResourceAsStream(getSentdetectormodelfile())));
    }

    public static void addDefaultSentenceFilters(SentenceExtractor extractor) {
        if (BRACKETS_REMOVER == null)
            BRACKETS_REMOVER = new BracketsRemover();
        if (SENTENCE_END_FILTER == null)
            SENTENCE_END_FILTER = new SentenceEndFilter();
        if (SENTENCE_START_FILTER == null)
            SENTENCE_START_FILTER = new SentenceStartFilter();
        extractor.addMapper(BRACKETS_REMOVER);
        extractor.addMapper(SENTENCE_END_FILTER);
        extractor.addMapper(SENTENCE_START_FILTER);
        extractor.addMapper(SentenceLengthFilter.minFilter(4));
    }

    public static SentenceExtractor getDefaultSentenceExtractor()
            throws IOException {
        SentenceExtractor extractor = new SentenceExtractor();
        addDefaultSentenceFilters(extractor);
        return extractor;
    }

    public static HtmlSentenceExtractor getDefaultHtmlSentenceExtractor()
            throws IOException {
        HtmlSentenceExtractor extractor = new HtmlSentenceExtractor();
        addDefaultSentenceFilters(extractor);
        return extractor;
    }

    /**
     * Return the default sentence reader.
     *
     * @param in
     * @param htmlSource
     *            - Are sentences from an html source?
     * @return
     * @throws IOException
     */
    public static ChunkedSentenceReader getDefaultSentenceReader(Reader in,
            boolean htmlSource) throws IOException {
        ChunkedSentenceReader result;
        if (htmlSource) {
            result = getDefaultSentenceReaderHtml(in);
        } else {
            result = getDefaultSentenceReader(in);
        }
        return result;
    }

    public static ChunkedSentenceReader getDefaultSentenceReader(Reader in)
            throws IOException {
        ChunkedSentenceReader reader = new ChunkedSentenceReader(in,
                getDefaultSentenceExtractor());
        return reader;
    }

    public static ChunkedSentenceReader getDefaultSentenceReaderHtml(Reader in)
            throws IOException {
        ChunkedSentenceReader reader = new ChunkedSentenceReader(in,
                getDefaultHtmlSentenceExtractor());
        return reader;
    }

    public static String getTokenizermodelfile() {
        return ReverbConfiguration.isEn() ? "en-token.bin" : FRENCH_RES_DIRECTORY + "fr-token.bin";
    }

    public static String getTaggermodelfile() {
        return ReverbConfiguration.isEn() ? "en-pos-maxent.bin" : FRENCH_RES_DIRECTORY + "fr-pos.bin";
    }

    public static String getChunkermodelfile() {
        return ReverbConfiguration.isEn() ? "en-chunker.bin" : FRENCH_RES_DIRECTORY + "fr-chunk.bin";
    }

    public static String getSentdetectormodelfile() {
        return ReverbConfiguration.isEn() ? "en-sent.bin" : FRENCH_RES_DIRECTORY + "fr-sent.bin";
    }

    public static String getConffunctionmodelfile() {
        return ReverbConfiguration.isEn() ? "reverb-conf-maxent.gz" : FRENCH_RES_DIRECTORY + "reverb-conf-maxent.gz";
    }
}
