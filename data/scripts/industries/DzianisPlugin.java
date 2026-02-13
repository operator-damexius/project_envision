package data.scripts.industries;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import java.awt.Color;

public class DzianisPlugin extends BaseIndustry {

    // --- CONFIGURATION ---
    private static final int OFFICER_LEVEL_BONUS = 1;     // +1 Officer Level to patrols
    private static final float DRONE_DEFENSE_MULT = 1.5f; // x1.5 Defense from Replicator item

    @Override
    public void apply() {
        super.apply(true);

        int size = market.getSize();
        
        // --- BASE STATS ---
        float qualityFlat = 0.50f;    // +50% Quality
        float defenseMult = 3.00f;    // x3.0 Defense (Very high base)
        float stabilityFlat = 1f;     // +1 Base Stability (Replaced Repair Rate)
        float fleetSizeMult = 2.00f;  // Base Fleet Size +100% (2x)
        
        // Patrols (Base)
        int lightPatrols = 1;
        int mediumPatrols = 1;
        int heavyPatrols = 0; 
        
        String desc = "Dzianis Outpost Protocols";

        // --- ALPHA CORE: "TOTAL WAR" ---
        if (getAICoreId() != null && getAICoreId().equals(Commodities.ALPHA_CORE)) {
            qualityFlat = 0.60f;      
            defenseMult = 4.00f;      
            stabilityFlat += 1f;      // Total +2 Stability with Alpha Core
            fleetSizeMult += 1.00f;   // Total: +200% (3x)
            heavyPatrols = 1;         
            desc = "Dzianis AI Warlord (Alpha)";
        }

        // --- SPECIAL ITEM: COMBAT DRONE REPLICATOR ---
        // Using raw string ID to avoid compilation errors
        if (getSpecialItem() != null && getSpecialItem().getId().equals("combat_drone_replicator")) {
            defenseMult *= DRONE_DEFENSE_MULT; // Stacks multiplicatively (e.g. 4.0 * 1.5 = 6.0x Defense!)
            desc = desc + " + Drone Swarm";
            
            // Drones reduce the need for human marines
            demand(Commodities.MARINES, size - 2); 
        } else {
            // Normal Marine demand
            supply(Commodities.MARINES, size + 1);
        }

        // --- DEMAND & SUPPLY ---
        demand(Commodities.CREW, size);
        demand(Commodities.SUPPLIES, size);
        supply(Commodities.HAND_WEAPONS, size + 1);

        // --- APPLY MODIFIERS ---
        if (isFunctional()) {
            market.getStats().getDynamic().getMod(Stats.PRODUCTION_QUALITY_MOD)
                  .modifyFlat(getModId(), qualityFlat, desc);

            market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD)
                  .modifyMult(getModId(), defenseMult, desc);

            market.getStats().getDynamic().getMod(Stats.COMBAT_FLEET_SIZE_MULT)
                  .modifyMult(getModId(), fleetSizeMult, desc);
            
            if (stabilityFlat > 0) {
                market.getStability().modifyFlat(getModId(), stabilityFlat, desc);
            }
                  
            market.getStats().getDynamic().getMod(Stats.PATROL_NUM_LIGHT_MOD).modifyFlat(getModId(), lightPatrols);
            market.getStats().getDynamic().getMod(Stats.PATROL_NUM_MEDIUM_MOD).modifyFlat(getModId(), mediumPatrols);
            market.getStats().getDynamic().getMod(Stats.PATROL_NUM_HEAVY_MOD).modifyFlat(getModId(), heavyPatrols);
            
            // --- SPECIAL BONUS (Replaced Repair Rate) ---
            // Officer Quality: Ensures your patrols have better captains
            // Using raw string "officer_quality_mod" to be safe
            market.getStats().getDynamic().getMod("officer_quality_mod")
                  .modifyFlat(getModId(), OFFICER_LEVEL_BONUS, desc);
        }
    }

    @Override
    public void unapply() {
        super.unapply();
        market.getStats().getDynamic().getMod(Stats.PRODUCTION_QUALITY_MOD).unmodify(getModId());
        market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD).unmodify(getModId());
        market.getStats().getDynamic().getMod(Stats.COMBAT_FLEET_SIZE_MULT).unmodify(getModId());
        market.getStability().unmodify(getModId());
        
        market.getStats().getDynamic().getMod(Stats.PATROL_NUM_HEAVY_MOD).unmodify(getModId());
        market.getStats().getDynamic().getMod(Stats.PATROL_NUM_MEDIUM_MOD).unmodify(getModId());
        market.getStats().getDynamic().getMod(Stats.PATROL_NUM_LIGHT_MOD).unmodify(getModId());
        
        // Remove Special Bonus
        market.getStats().getDynamic().getMod("officer_quality_mod").unmodify(getModId());
    }

    // --- TOOLTIPS ---
    
    @Override
    protected void addRightAfterDescriptionSection(TooltipMakerAPI tooltip, IndustryTooltipMode mode) {
        float opad = 10.0F;
        Color highlight = Misc.getHighlightColor();

        // New bonus tooltip
        tooltip.addPara("Officer quality: %s", opad, highlight, "+" + OFFICER_LEVEL_BONUS + " level");
        
        // Show item status if installed
        if (getSpecialItem() != null && getSpecialItem().getId().equals("combat_drone_replicator")) {
            tooltip.addPara("Combat Drone Replicator active: Ground defenses boosted by %s.", 
                opad, Misc.getPositiveHighlightColor(), "50%");
        }
    }
    
    @Override
    protected void addAlphaCoreDescription(TooltipMakerAPI tooltip, AICoreDescriptionMode mode) {
        float opad = 10f;
        Color highlight = Misc.getHighlightColor();
        String pre = "Alpha-level AI Warlord";
        if (mode == AICoreDescriptionMode.MANAGE_CORE_DIALOG_LIST || mode == AICoreDescriptionMode.INDUSTRY_TOOLTIP) {
            pre = "Alpha Core";
        }
        
        tooltip.addPara(pre + " assumes total command, unlocking %s, increasing %s by %s, and boosting %s by an additional %s.", 
                opad, highlight, 
                "Heavy Patrols",       
                "stability", "+1",     
                "fleet size", "100%");
    }

    @Override
    public boolean isAvailableToBuild() {
        FactionAPI solvaris = Global.getSector().getFaction("solvaris");
        if (solvaris == null) return false;

        boolean isAllied = solvaris.getRelToPlayer().getRel() >= 0.9f;
        String playerFactionId = Global.getSector().getPlayerFaction().getId();
        boolean isCommissioned = playerFactionId.equals("solvaris");
        
        String commissionId = Misc.getCommissionFactionId();
        boolean isMerc = (commissionId != null && commissionId.equals("solvaris"));

        return isAllied || isCommissioned || isMerc;
    }
    
    @Override
    public boolean showWhenUnavailable() {
        return false;
    }
    
    @Override
    public String getUnavailableReason() {
        return "Requires 'Cooperative' (90/100) reputation with the Solvaris Remnant.";
    }
}