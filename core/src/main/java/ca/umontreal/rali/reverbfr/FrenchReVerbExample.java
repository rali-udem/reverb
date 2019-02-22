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

import edu.washington.cs.knowitall.nlp.ChunkedSentence;

/* String -> ChunkedSentence */
import edu.washington.cs.knowitall.nlp.OpenNlpSentenceChunker;

/* The class that is responsible for extraction. */
import edu.washington.cs.knowitall.extractor.ReVerbExtractor;

/* The class that is responsible for assigning a confidence score to an
 * extraction.
 */
import edu.washington.cs.knowitall.extractor.conf.ConfidenceFunction;
import edu.washington.cs.knowitall.extractor.conf.ReVerbOpenNlpConfFunction;

/* A class for holding a (arg1, rel, arg2) triple. */
import edu.washington.cs.knowitall.nlp.extraction.ChunkedBinaryExtraction;

public class FrenchReVerbExample {
    
    public static void main(String[] args) throws Exception {

        ReverbConfiguration.setLocale(Locale.FRENCH);
        
        String sentStr = null;
        
        if (ReverbConfiguration.isFr()) {
            /*
            sentStr = "Denis Coderre est vraiment le maire de Montréal.";
            sentStr = "Denis Coderre n'est pas le maire de Montréal.";
            sentStr = "Bruxelles perd toute emprise sur les provinces du  nord.";
            sentStr = "Nous nommâmes cette montagne le mont Royal.";
            sentStr = "La ville est située dans les basses-terres du Saint-Laurent, une grande vallée plane située entre les Appalaches et les Laurentides.";
            sentStr = "\" Montréal \" est une ville du Québec dont elle est la métropole, le centre de la culture et des affaires.";
            sentStr = "Montréal a accueilli l'Exposition universelle de 1967 et les Jeux olympiques d'été de 1976.";
            sentStr = "Son quartier historique, le Vieux-Montréal, a été déclaré arrondissement historique en 1964.";
            sentStr = "\" Luc-Normand Tellier \" (né le 10 octobre 1944 à Montréal, Québec, Canada) est professeur émérite d'économie spatiale de l' Université du Québec";
            sentStr = "Nous sommes les robots.";
            sentStr = "Nous nous sommes perdus en ville.";
            sentStr = "Ce joli train du nord a des roues vertes du nord.";
            sentStr = "Cet homme et cette femme sont mes amis";
            sentStr = "C'est la ville de Cartier.";
            sentStr = "Un train vert emporte ses amis.";
            sentStr = "Luc Robitaille (né au Canada) est un joueur professionnel de hockey sur glace canadien.";
            sentStr = "\" Charles Gagnon \" (né le au Bic, mort le à Montréal) est un homme politique, ancien felquiste et leader communiste au Québec et au Canada.";
            sentStr = "La majeure partie de la ville est située sur l'île de Montréal, dans l'archipel d'Hochelaga, en bordure du Saint-Laurent, à proximité de l'Ontario et des États-Unis.";
            sentStr = "Le concurrent de SNC, la firme d'ingénierie Lavalin, éprouve de sérieuses difficultés financières au printemps 1991.";
            sentStr = "\" Jacques Chagnon \", né le 31 décembre 2013 à Montréal, est un homme politique québécois.";
            sentStr = " Montréal a accueilli l'Exposition universelle de 1967 et les Jeux olympiques d'été de 1976.";
            sentStr = "Montréal est la plus importante ville francophone d'Amérique.";
            sentStr = "Jules n'a pu comparer Venise à Montréal";
            sentStr = "La ville de Montréal prévoit augmenter le nombre à 800 kilomètres d'ici 2015.";
            sentStr = "En 2010, 3,3 millions de déplacements en BIXI ont été enregistrés; le réseau compte plus de 30000 abonnés.";
            sentStr = "Molson a ouvert sa septième brasserie à Moncton (Nouveau Brunswick) le 12 octobre 2007.";
            sentStr = "L'immigration est le principal moteur de la croissance démographique montréalaise.";
            sentStr = "Bell Canada a alors déposé une nouvelle demande le 4 mars 2013, qui a été approuvée le 27 juin 2013";
            sentStr = "C'est l'explorateur français Jacques Cartier, lors de son second voyage en Amérique, qui baptise la montagne qui surplombe aujourd'hui la ville.";
            sentStr = "La  couverture neigeuse  la  plus importante  a   été  mesurée  le   12  mars 1971 avec 102   cm.";
            sentStr = "Sur les 22 municipalités fusionnées en 2002 qui n'ont pas obtenu d'avoir la tenue d'un référendum.";
            sentStr = "Il est renommé pour sa poutine et ils se sont trompés, mais ils ont reconnu leur erreur.";
            sentStr = "Les Canadiens sont situés sur la rivière Stephen Harper.";
            sentStr = "Les Canadiens ont mangé de la misère pour Stephen Harper.";
            sentStr = "Les Canadiens sont renommés pour Stephen Harper.";
            sentStr = "Les Canadiens se sont aperçus de Stephen Harper.";           
            sentStr = "Les Canadiens ne se sont pas trompés sur Stephen Harper.";
            sentStr = "Le référendum tenu en Australie est basé sur une question.";
            sentStr = "Les Canadiens ne l'ont pas trompés sur Stephen Harper.";
            sentStr = "Les immigrants en Chine expédient de l'argent.";
            */
            sentStr = "« Le but est de les initier à nos classiques, à notre folklore, afin de leur donner une base à laquelle ils n’auraient pas accès si facilement », explique M. Gagnon, qui depuis 20 ans transmet notre littérature aux exilés.";
        } else {
            /*
            sentStr = "Michael McGinn is the mayor of Seattle, one of the greatest cities in the world.";
            sentStr = "We are the robots.";
            sentStr = "STM has 390 million users per year and shows an annual growth of 1%.";
            sentStr = "This train has green wheels.";
            sentStr = "This man and this woman are my friends.";
            sentStr = "Luc Robitaille (born in Canada) is a professional ice hockey player.";
            */
            sentStr = "The player is born in Canada the great.";
        }

        // Looks on the classpath for the default model files.
        OpenNlpSentenceChunker chunker = new OpenNlpSentenceChunker();
        ChunkedSentence sent = chunker.chunkSentence(sentStr);

        // Prints out the (token, tag, chunk-tag) for the sentence
        System.out.println(sentStr);
        for (int i = 0; i < sent.getLength(); i++) {
            String token = sent.getToken(i);
            String posTag = sent.getPosTag(i);
            String chunkTag = sent.getChunkTag(i);
            System.out.println(token + " " + posTag + " " + chunkTag);
        }

        // Prints out extractions from the sentence.
        ReVerbExtractor reverb = new ReVerbExtractor();
        ConfidenceFunction confFunc = ReverbConfiguration.isEn() ? new ReVerbOpenNlpConfFunction() : null;
        for (ChunkedBinaryExtraction extr : reverb.extract(sent)) {
            
            System.out.println("Arg1=" + extr.getArgument1());
            System.out.println("Rel=" + extr.getRelation());
            
            if (ReverbConfiguration.isFr()) {
                FrenchReverbUtils.addLemmas(extr.getSentence());
                System.out.println("(" + FrenchReverbUtils.formatLemmatizedRelation(extr) + ")");
                System.out.println("Canon: (" + FrenchReverbUtils.getCanonicalRelation(extr) + ")");
            }
            
            System.out.println("Arg2=" + extr.getArgument2());
            
            if (confFunc != null) {
                double conf = confFunc.getConf(extr);
                System.out.println("Conf=" + conf);
            }
        }
        
        System.out.println("\nFinished");
    }
}
