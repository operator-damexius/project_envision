package data.scripts.industries;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import java.awt.Color;

public class NehulPlugin extends BaseIndustry {

    // --- CONFIGURATION ---
    // Special Bonus Points (Logistical Efficiency)
    private static final float UPKEEP_REDUCTION = 0.15f;    // -15% Upkeep (Cheaper to run colony)
    private static final float TRADE_FLEET_SIZE = 0.25f;    // +25% Bigger Trade Fleets

    @Override
    public void apply() {
        super.apply(true); // Applies standard upkeep & demand reduction
        
        // --- BALANCED STATS (Logistics & Defense Focus) ---
        
        // BASE: A heavily fortified supply hub.
        float stabilityFlat = 3f;    // +3 Stability (Strong peacekeeping)
        float accessPercent = 15f;   // +15% Access (Logistics hub)
        float defenseMult = 1.30f;   // +30% Ground Defense (Militia garrisons)
        String desc = "Nehul Logistics Support";

        // --- ALPHA CORE: "MARTIAL LAW PROTOCOLS" ---
        // Turns the colony into a fortress.
        if (getAICoreId() != null && getAICoreId().equals(Commodities.ALPHA_CORE)) {
            stabilityFlat = 5f;    // +5 Stability (Absolute Order)
            accessPercent = 30f;   // +30% Access (AI-Driven Routing)
            defenseMult = 1.60f;   // +60% Ground Defense (Automated Killzones)
            desc = "Nehul AI Overseer (Alpha)";
        }
        
        // --- APPLY BONUSES ---
        if (isFunctional()) {
            market.getAccessibilityMod().modifyPercent(getModId(), accessPercent, "Nehul Logistics Frame");
            
            market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD)
                  .modifyMult(getModId(), defenseMult, "Nehul Transit Security");
            
            if (stabilityFlat > 0) {
                market.getStability().modifyFlat(getModId(), stabilityFlat, "Nehul Supply Chain");
            }
            
            // --- SPECIAL BONUS POINTS ---
            // 1. Upkeep Reduction (Optimization)
            market.getUpkeepMult().modifyMult(getModId(), 1f - UPKEEP_REDUCTION, desc);
            
            // 2. Trade Fleet Size (Massive Convoys)
            // FIXED: Used raw string "trade_fleet_size_mult" because it doesn't exist in Stats.java
            market.getStats().getDynamic().getMod("trade_fleet_size_mult")
                  .modifyFlat(getModId(), TRADE_FLEET_SIZE, desc);
        }
    }

    @Override
    public void unapply() {
        super.unapply();
        market.getAccessibilityMod().unmodify(getModId());
        market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD).unmodify(getModId());
        market.getStability().unmodify(getModId());
        
        // Remove Special Bonuses
        market.getUpkeepMult().unmodify(getModId());
        // FIXED: Used raw string here too
        market.getStats().getDynamic().getMod("trade_fleet_size_mult").unmodify(getModId());
    }
    
    // --- CUSTOM BONUS TOOLTIP ---
    
    @Override
    protected void addRightAfterDescriptionSection(TooltipMakerAPI tooltip, IndustryTooltipMode mode) {
        float opad = 10.0F;
        Color highlight = Misc.getHighlightColor();

        // Display Special Bonuses
        tooltip.addPara("Colony upkeep: %s", opad, highlight, "-" + (int)(UPKEEP_REDUCTION * 100f) + "%");
        tooltip.addPara("Trade fleet size: %s", opad, highlight, "+" + (int)(TRADE_FLEET_SIZE * 100f) + "%");
    }

    @Override
    protected void addAlphaCoreDescription(TooltipMakerAPI tooltip, AICoreDescriptionMode mode) {
        float opad = 10f;
        Color highlight = Misc.getHighlightColor();
        String pre = "Alpha-level AI Overseer";
        if (mode == AICoreDescriptionMode.MANAGE_CORE_DIALOG_LIST || mode == AICoreDescriptionMode.INDUSTRY_TOOLTIP) {
            pre = "Alpha Core";
        }
        
        tooltip.addPara(pre + " enforces martial law protocols, increasing %s by %s, boosting %s by %s, and reinforcing defenses.", 
                opad, highlight, 
                "stability", "+5", 
                "accessibility", "+30%");
    }

    @Override
    public boolean isAvailableToBuild() {
        PlanetAPI planet = market.getPlanetEntity();
        if (planet == null) return false; 
        
        String type = planet.getTypeId();
        boolean isHabitable = type.contains("terran") || type.contains("tundra") || 
                              type.contains("arid") || type.contains("desert") || 
                              type.contains("jungle") || type.contains("water");
        boolean isIndustrial = market.hasCondition(Conditions.NO_ATMOSPHERE) || 
                               market.hasCondition(Conditions.TECTONIC_ACTIVITY);
        boolean isIceHell = type.contains("cryovolcanic") || type.contains("frozen");
        boolean planetValid = (isHabitable || isIndustrial) && !isIceHell;

        FactionAPI solvaris = Global.getSector().getFaction("solvaris");
        if (solvaris == null) return false;
        
        boolean isAllied = solvaris.getRelToPlayer().getRel() >= 0.9f;
        String playerFaction = Global.getSector().getPlayerFaction().getId();
        boolean isCommissioned = playerFaction.equals("solvaris");

        return planetValid && (isAllied || isCommissioned);
    }
    
    @Override
    public String getUnavailableReason() {
        return "Requires 'Cooperative' reputation with Solvaris and a stable planetary surface.";
    }
    
    @Override
    public boolean showWhenUnavailable() { return false; }
}