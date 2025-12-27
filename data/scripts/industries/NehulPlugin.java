package data.scripts.industries;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

public class NehulPlugin extends com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry {

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
        market.getAccessibilityMod().modifyPercent(getModId(), accessPercent, "Nehul Logistics Frame");
        
        // Farming removed to focus identity on Stability/Defense
              
        market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD)
              .modifyMult(getModId(), defenseMult, "Nehul Transit Security");
        
        market.getStability().modifyFlat(getModId(), stabilityFlat, "Nehul Supply Chain");
    }

    @Override
    public void unapply() {
        super.unapply();
        market.getAccessibilityMod().unmodify(getModId());
        market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD).unmodify(getModId());
        market.getStability().unmodify(getModId());
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