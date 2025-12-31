package data.scripts.industries;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

public class DzianisPlugin extends BaseIndustry {

    @Override
    public void apply() {
        super.apply(true);

        int size = market.getSize();
        
        // --- BASE STATS (Elite Military Base) ---
        float qualityFlat = 0.50f;    // +50% Quality (Better than average)
        float defenseMult = 3.00f;    // x3.0 Defense (Strong fortification)
        float fleetSizeMult = 1.75f;  // x1.75 Fleet Size (75% larger fleets)
        
        // Patrols (Base: Light + Medium)
        int lightPatrols = 1;
        int mediumPatrols = 1;
        int heavyPatrols = 0; // Requires Alpha Core
        
        String desc = "Dzianis Outpost Protocols";

        // --- ALPHA CORE: "TOTAL WAR" ---
        if (getAICoreId() != null && getAICoreId().equals(Commodities.ALPHA_CORE)) {
            qualityFlat = 0.50f;    // +50% Quality (Equiv. to Pristine Nanoforge)
            defenseMult = 2.00f;    // x2.0 Defense (Fortress World)
            fleetSizeMult = 2.00f;  // x2.0 Fleet Size (100% larger - Massive but stable)
            
            heavyPatrols = 1;       // Unlocks Heavy Patrol
            desc = "Dzianis AI Warlord (Alpha)";
        }

        // --- DEMAND & SUPPLY ---
        demand(Commodities.SUPPLIES, size);
        demand(Commodities.FUEL, size);
        demand(Commodities.SHIPS, size);
        demand(Commodities.MARINES, size);
        demand(Commodities.HAND_WEAPONS, size);

        supply(Commodities.MARINES, size + 1);
        supply(Commodities.HAND_WEAPONS, size + 1);

        // --- APPLY MODIFIERS ---
        
        // Quality
        market.getStats().getDynamic().getMod(Stats.PRODUCTION_QUALITY_MOD)
              .modifyFlat(getModId(), qualityFlat, desc);

        // Ground Defense
        market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD)
              .modifyMult(getModId(), defenseMult, desc);

        // Fleet Size (Capped to prevent lag/crashes)
        market.getStats().getDynamic().getMod(Stats.COMBAT_FLEET_SIZE_MULT)
              .modifyMult(getModId(), fleetSizeMult, desc);
              
        // Patrols
        market.getStats().getDynamic().getMod(Stats.PATROL_NUM_LIGHT_MOD).modifyFlat(getModId(), lightPatrols);
        market.getStats().getDynamic().getMod(Stats.PATROL_NUM_MEDIUM_MOD).modifyFlat(getModId(), mediumPatrols);
        market.getStats().getDynamic().getMod(Stats.PATROL_NUM_HEAVY_MOD).modifyFlat(getModId(), heavyPatrols);
    }

    @Override
    public void unapply() {
        super.unapply();
        market.getStats().getDynamic().getMod(Stats.PRODUCTION_QUALITY_MOD).unmodify(getModId());
        market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD).unmodify(getModId());
        market.getStats().getDynamic().getMod(Stats.COMBAT_FLEET_SIZE_MULT).unmodify(getModId());
        market.getStats().getDynamic().getMod(Stats.PATROL_NUM_HEAVY_MOD).unmodify(getModId());
        market.getStats().getDynamic().getMod(Stats.PATROL_NUM_MEDIUM_MOD).unmodify(getModId());
        market.getStats().getDynamic().getMod(Stats.PATROL_NUM_LIGHT_MOD).unmodify(getModId());
    }

    @Override
    public boolean isAvailableToBuild() {
        FactionAPI solvaris = Global.getSector().getFaction("solvaris");
        if (solvaris == null) return false;

        boolean isAllied = solvaris.getRelToPlayer().getRel() >= 0.9f;
        String playerFactionId = Global.getSector().getPlayerFaction().getId();
        boolean isCommissioned = playerFactionId.equals("solvaris");

        return isAllied || isCommissioned;
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