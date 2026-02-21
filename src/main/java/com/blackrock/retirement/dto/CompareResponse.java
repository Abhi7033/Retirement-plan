package com.blackrock.retirement.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.blackrock.retirement.model.SavingsByDate;
import java.util.List;

/**
 * Response body for the /returns:compare endpoint.
 * Provides a side-by-side comparison of NPS vs Index Fund returns
 * along with a recommendation to help users make informed retirement decisions.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompareResponse {

    private double totalTransactionAmount;
    private double totalCeiling;
    private double totalInvestable;

    // NPS breakdown
    private List<SavingsByDate> npsSavings;
    private double npsTotalProfit;
    private double npsTotalTaxBenefit;
    private double npsEffectiveGain;

    // Index Fund breakdown
    private List<SavingsByDate> indexSavings;
    private double indexTotalProfit;
    private double indexEffectiveGain;

    // Recommendation
    private String recommendation;
    private String riskProfile;
    private int suggestedNpsPercent;
    private int suggestedIndexPercent;
    private String reasoning;

    public CompareResponse() {
    }

    public double getTotalTransactionAmount() {
        return totalTransactionAmount;
    }

    public void setTotalTransactionAmount(double totalTransactionAmount) {
        this.totalTransactionAmount = totalTransactionAmount;
    }

    public double getTotalCeiling() {
        return totalCeiling;
    }

    public void setTotalCeiling(double totalCeiling) {
        this.totalCeiling = totalCeiling;
    }

    public double getTotalInvestable() {
        return totalInvestable;
    }

    public void setTotalInvestable(double totalInvestable) {
        this.totalInvestable = totalInvestable;
    }

    public List<SavingsByDate> getNpsSavings() {
        return npsSavings;
    }

    public void setNpsSavings(List<SavingsByDate> npsSavings) {
        this.npsSavings = npsSavings;
    }

    public double getNpsTotalProfit() {
        return npsTotalProfit;
    }

    public void setNpsTotalProfit(double npsTotalProfit) {
        this.npsTotalProfit = npsTotalProfit;
    }

    public double getNpsTotalTaxBenefit() {
        return npsTotalTaxBenefit;
    }

    public void setNpsTotalTaxBenefit(double npsTotalTaxBenefit) {
        this.npsTotalTaxBenefit = npsTotalTaxBenefit;
    }

    public double getNpsEffectiveGain() {
        return npsEffectiveGain;
    }

    public void setNpsEffectiveGain(double npsEffectiveGain) {
        this.npsEffectiveGain = npsEffectiveGain;
    }

    public List<SavingsByDate> getIndexSavings() {
        return indexSavings;
    }

    public void setIndexSavings(List<SavingsByDate> indexSavings) {
        this.indexSavings = indexSavings;
    }

    public double getIndexTotalProfit() {
        return indexTotalProfit;
    }

    public void setIndexTotalProfit(double indexTotalProfit) {
        this.indexTotalProfit = indexTotalProfit;
    }

    public double getIndexEffectiveGain() {
        return indexEffectiveGain;
    }

    public void setIndexEffectiveGain(double indexEffectiveGain) {
        this.indexEffectiveGain = indexEffectiveGain;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public String getRiskProfile() {
        return riskProfile;
    }

    public void setRiskProfile(String riskProfile) {
        this.riskProfile = riskProfile;
    }

    public int getSuggestedNpsPercent() {
        return suggestedNpsPercent;
    }

    public void setSuggestedNpsPercent(int suggestedNpsPercent) {
        this.suggestedNpsPercent = suggestedNpsPercent;
    }

    public int getSuggestedIndexPercent() {
        return suggestedIndexPercent;
    }

    public void setSuggestedIndexPercent(int suggestedIndexPercent) {
        this.suggestedIndexPercent = suggestedIndexPercent;
    }

    public String getReasoning() {
        return reasoning;
    }

    public void setReasoning(String reasoning) {
        this.reasoning = reasoning;
    }
}
