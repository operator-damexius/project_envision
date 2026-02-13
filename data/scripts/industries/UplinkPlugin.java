package data.scripts.industries;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import java.awt.Color;

public class UplinkPlugin extends BaseIndustry {

    // --- CONFIGURATION ---
    // New Safe Bonus Points (Stability & Access)
    private static final int STABILITY_BONUS = 2;          // +2 Stability
    private static final float ACCESS_BONUS = 0.20f;       // +20% Accessibility

    @Override
    public void apply() {
        super.apply(true); // Applies standard upkeep reduction
        
        // --- BASE STATS ---
        float fleetSizeMult = 1.50f;  // Base: +50% Fleet Size
        float qualityFlat = 0.50f;    // Base: +50% Quality
        float defenseMult = 1.75f;    // Base: +75% Ground Defense
        float baseStability = 0f;     // Base Stability from Alpha Core
        
        String desc = "Argent Uplink Signal";

        // --- ALPHA CORE: FLEET & STABILITY ---
        if ("alpha_core".equals(getAICoreId())) {
            baseStability = 1f;       // Set to +1 Stability (stacks with special bonus)
            fleetSizeMult += 0.50f;   // Adds 50% more Fleet Size (Total: 2.0x / +100%)
            defenseMult = 2.00f;      // Defense boost (Total: 2.0x / +100%)
            desc = "Argent AI Coordinator (Alpha)";
        }

        if (isFunctional()) {
            // --- APPLY STANDARD MODIFIERS ---
            market.getStats().getDynamic().getMod(Stats.COMBAT_FLEET_SIZE_MULT)
                  .modifyMult(getModId(), fleetSizeMult, desc);
            
            market.getStats().getDynamic().getMod(Stats.PRODUCTION_QUALITY_MOD)
                  .modifyFlat(getModId(), qualityFlat, desc);
                  
            market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD)
                  .modifyMult(getModId(), defenseMult, desc);

            // Apply Alpha Core Stability
            if (baseStability > 0) {
                market.getStability().modifyFlat(getModId() + "_core", baseStability, desc);
            }

            // --- PATROLS ---
            market.getStats().getDynamic().getMod(Stats.PATROL_NUM_HEAVY_MOD).modifyFlat(getModId(), 1);
            market.getStats().getDynamic().getMod(Stats.PATROL_NUM_MEDIUM_MOD).modifyFlat(getModId(), 1);
            market.getStats().getDynamic().getMod(Stats.PATROL_NUM_LIGHT_MOD).modifyFlat(getModId(), 1);

            // --- SPECIAL BONUS POINTS (Safe) ---
            
            // 1. Stability Bonus (Secure Comms)
            market.getStability().modifyFlat(getModId(), STABILITY_BONUS, "Uplink Signal");

            // 2. Accessibility Bonus (Traffic Control)
            market.getAccessibilityMod().modifyPercent(getModId(), ACCESS_BONUS * 100f, "Uplink Coordination");
        }
    }

    @Override
    public void unapply() {
        super.unapply();
        // Remove Standard Mods
        market.getStats().getDynamic().getMod(Stats.COMBAT_FLEET_SIZE_MULT).unmodify(getModId());
        market.getStats().getDynamic().getMod(Stats.PRODUCTION_QUALITY_MOD).unmodify(getModId());
        market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD).unmodify(getModId());
        market.getStability().unmodify(getModId() + "_core");
        
        market.getStats().getDynamic().getMod(Stats.PATROL_NUM_HEAVY_MOD).unmodify(getModId());
        market.getStats().getDynamic().getMod(Stats.PATROL_NUM_MEDIUM_MOD).unmodify(getModId());
        market.getStats().getDynamic().getMod(Stats.PATROL_NUM_LIGHT_MOD).unmodify(getModId());

        // Remove Special Bonuses
        market.getStability().unmodify(getModId());
        market.getAccessibilityMod().unmodify(getModId());
    }

    // --- TOOLTIP DISPLAY ---
    
    @Override
    protected void addRightAfterDescriptionSection(TooltipMakerAPI tooltip, IndustryTooltipMode mode) {
        float opad = 10.0F;
        Color highlight = Misc.getHighlightColor();

        // Display the Special Bonuses in the UI
        tooltip.addPara("Stability bonus: %s", opad, highlight, "+" + STABILITY_BONUS);
        tooltip.addPara("Accessibility bonus: %s", opad, highlight, "+" + (int)(ACCESS_BONUS * 100f) + "%");
    }

    @Override
    protected void addAlphaCoreDescription(TooltipMakerAPI tooltip, AICoreDescriptionMode mode) {
        float opad = 10f;
        Color highlight = Misc.getHighlightColor();
        String pre = "Alpha-level AI integration";
        if (mode == AICoreDescriptionMode.MANAGE_CORE_DIALOG_LIST || mode == AICoreDescriptionMode.INDUSTRY_TOOLTIP) {
            pre = "Alpha Core";
        }
        tooltip.addPara(pre + " coordinates defensive grids, increasing %s by %s and boosting %s by an additional %s.", 
                opad, highlight, 
                "stability", "+1", 
                "fleet size", "50%");
    }

    @Override
    public boolean isAvailableToBuild() {
        FactionAPI argent = Global.getSector().getFaction("argent");
        if (argent == null) {
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
        return "Requires 'Cooperative' (90/100) reputation with the Argent Concordat.";
    }

    @Override
    public boolean showWhenUnavailable() { return false; }
}