/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.swete.models;

/**
 *
 * @author shannah
 */
public class TranslationStats {
    private int totalWords;
    private int totalPhrases;
    private int untranslatedWords;
    private int untranslatedPhrases;

    /**
     * @return the totalWords
     */
    public int getTotalWords() {
        return totalWords;
    }

    /**
     * @param totalWords the totalWords to set
     */
    public void setTotalWords(int totalWords) {
        this.totalWords = totalWords;
    }

    /**
     * @return the totalPhrases
     */
    public int getTotalPhrases() {
        return totalPhrases;
    }

    /**
     * @param totalPhrases the totalPhrases to set
     */
    public void setTotalPhrases(int totalPhrases) {
        this.totalPhrases = totalPhrases;
    }

    /**
     * @return the untranslatedWords
     */
    public int getUntranslatedWords() {
        return untranslatedWords;
    }

    /**
     * @param untranslatedWords the untranslatedWords to set
     */
    public void setUntranslatedWords(int untranslatedWords) {
        this.untranslatedWords = untranslatedWords;
    }

    /**
     * @return the untranslatedPhrases
     */
    public int getUntranslatedPhrases() {
        return untranslatedPhrases;
    }

    /**
     * @param untranslatedPhrases the untranslatedPhrases to set
     */
    public void setUntranslatedPhrases(int untranslatedPhrases) {
        this.untranslatedPhrases = untranslatedPhrases;
    }
}
