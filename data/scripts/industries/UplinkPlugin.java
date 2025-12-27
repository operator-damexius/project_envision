package data.scripts.industries;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import java.awt.Color;

public class UplinkPlugin extends BaseIndustry {

    @Override
    public void apply() {
        super.apply(true); // Applies standard upkeep reduction
        
        int size = market.getSize();
        
        // --- BALANCED STATS ---
        float fleetSizeMult = 1.25f;  // +25% Fleet Size (Strong, but fair)
        float qualityFlat = 0.20f;    // +20% Quality
        float defenseMult = 1.50f;    // +50% Ground Defense
        float stabilityFlat = 0f;     // Base stability (0)
        
        String desc = "Argent Uplink Signal";

        // --- ALPHA CORE: STABILITY FOCUS ---
        if ("alpha_core".equals(getAICoreId())) {
            stabilityFlat = 3f;       // The requested +3 Stability
            defenseMult = 2.00f;      // Slight Defense boost for having AI oversight
            desc = "Argent AI Coordinator (Alpha)";
        }

        // --- DEMANDS (Restored) ---
        // An uplink requires power and crew to function.
        demand(Commodities.SUPPLIES, size);
        demand(Commodities.FUEL, size);
        demand(Commodities.MARINES, size - 2);

        // --- SUPPLY ---
        // Generates Crew (Training) and Intelligence (Marines)
        supply(Commodities.CREW, size);
        supply(Commodities.MARINES, size);

        if (isFunctional()) {
            // --- APPLY MODIFIERS ---
            market.getStats().getDynamic().getMod(Stats.COMBAT_FLEET_SIZE_MULT)
                  .modifyMult(getModId(), fleetSizeMult, desc);
            
            market.getStats().getDynamic().getMod(Stats.PRODUCTION_QUALITY_MOD)
                  .modifyFlat(getModId(), qualityFlat, desc);
                  
            market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD)
                  .modifyMult(getModId(), defenseMult, desc);

            // Apply Stability (Only if Alpha Core is present)
            if (stabilityFlat > 0) {
                market.getStability().modifyFlat(getModId(), stabilityFlat, desc);
            }

            // --- PATROLS (Balanced) ---
            // +1 to all patrols (instead of +2)
            market.getStats().getDynamic().getMod(Stats.PATROL_NUM_HEAVY_MOD).modifyFlat(getModId(), 1);
            market.getStats().getDynamic().getMod(Stats.PATROL_NUM_MEDIUM_MOD).modifyFlat(getModId(), 1);
            market.getStats().getDynamic().getMod(Stats.PATROL_NUM_LIGHT_MOD).modifyFlat(getModId(), 1);
        }
    }

    @Override
    public void unapply() {
        super.unapply();
        market.getStats().getDynamic().getMod(Stats.COMBAT_FLEET_SIZE_MULT).unmodify(getModId());
        market.getStats().getDynamic().getMod(Stats.PRODUCTION_QUALITY_MOD).unmodify(getModId());
        market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD).unmodify(getModId());
        market.getStability().unmodify(getModId());
        
        market.getStats().getDynamic().getMod(Stats.PATROL_NUM_HEAVY_MOD).unmodify(getModId());
        market.getStats().getDynamic().getMod(Stats.PATROL_NUM_MEDIUM_MOD).unmodify(getModId());
        market.getStats().getDynamic().getMod(Stats.PATROL_NUM_LIGHT_MOD).unmodify(getModId());
    }

    @Override
    protected void addAlphaCoreDescription(TooltipMakerAPI tooltip, AICoreDescriptionMode mode) {
        float opad = 10f;
        Color highlight = Misc.getHighlightColor();
        String pre = "Alpha-level AI integration";
        if (mode == AICoreDescriptionMode.MANAGE_CORE_DIALOG_LIST || mode == AICoreDescriptionMode.INDUSTRY_TOOLTIP) {
            pre = "Alpha Core";
        }
        tooltip.addPara(pre + " optimizes local coordination, increasing colony %s by %s and fortifying defenses.", opad, highlight, "stability", "+3");
    }

    @Override
    public boolean isAvailableToBuild() {
        if (!super.isAvailableToBuild()) return false;

        FactionAPI argent = Global.getSector().getFaction("argent");
        // Safe check if faction exists
        if (argent == null) {
             // Fallback: check Solvaris if Argent is missing/same
             argent = Global.getSector().getFaction("solvaris");
             if (argent == null) return false; 
        }

        boolean isAllied = argent.getRelToPlayer().getRel() >= 0.9f;
        
        String commissionId = Misc.getCommissionFactionId();
        boolean isCommissioned = (commissionId != null && (commissionId.equals("argent") || commissionId.equals("solvaris")));
        boolean isOwner = Global.getSector().getPlayerFaction().getId().equals("argent");

        return isAllied || isCommissioned || isOwner;
    }
    
    @Override
    public String getUnavailableReason() {
        if (!super.isAvailableToBuild()) return super.getUnavailableReason();
        return "Requires 'Cooperative' (90/100) reputation with the Argent Concordat.";
    }

    @Override
    public boolean showWhenUnavailable() { return false; }
}