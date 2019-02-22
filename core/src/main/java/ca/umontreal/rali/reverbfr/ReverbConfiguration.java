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
import java.util.Locale;

/**
 * A <em>master switch</em> allowing to switch from English to French for
 * the ReVerb framework. By default, the locale is English.
 *
 */
public class ReverbConfiguration {

    private static Locale curLocale = Locale.ENGLISH;

    /**
     * Specifies the locale.
     * 
     * @param locale Either {@linkplain Locale#FRENCH or Locale#ENGLISH}.
     */
    public static void setLocale(Locale locale) {
        curLocale = locale;
    }
    
    public static Locale getLocale() {
        return curLocale;
    }
    
    public static boolean isEn() {
        return curLocale == Locale.ENGLISH;
    }
    
    public static boolean isFr() {
        return curLocale == Locale.FRENCH || curLocale == Locale.FRANCE;
    }
    
}
