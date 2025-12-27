package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

public class SolvarisOrderCore extends BaseHullMod {

    // =========================================================================
    // STATS CONFIGURATION
    // =========================================================================
    
    // Combat & Range
    private static final float RANGE_BONUS = 200f;      // +200% Weapon Range
    private static final float PD_RANGE_BONUS = 80f;    // +80% Point Defense Range
    
    // Logistics & Movement
    private static final float BURN_LEVEL_BONUS = 10f;   // +10 Max Burn Speed
    private static final float GROUND_SUPPORT = 200f;   // +200 Raiding Power
    private static final float CREW_REQ_PERCENT = 100f; // +100% Crew Required (Trade-off)
    
    // Fitting
    private static final float LARGE_OP_REDUCTION = 20f; // -20 OP cost for Large Ballistics

    // =========================================================================
    // LOGIC
    // =========================================================================

    @Override
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        
        // 1. Range Enhancements (Super-Capital grade targeting)
        stats.getBallisticWeaponRangeBonus().modifyPercent(id, RANGE_BONUS);
        stats.getEnergyWeaponRangeBonus().modifyPercent(id, RANGE_BONUS);
        
        // 2. Point Defense Enhancements
        stats.getNonBeamPDWeaponRangeBonus().modifyPercent(id, PD_RANGE_BONUS);
        stats.getBeamPDWeaponRangeBonus().modifyPercent(id, PD_RANGE_BONUS);
        
        // 3. Strategic Mobility (Burn Drive)
        stats.getMaxBurnLevel().modifyFlat(id, BURN_LEVEL_BONUS);
        
        // 4. Planetary Operations (Ground Support)
        stats.getDynamic().getMod(Stats.FLEET_GROUND_SUPPORT).modifyFlat(id, GROUND_SUPPORT);
        
        // 5. Logistics Trade-off (Increased Maintenance/Crew)
        stats.getMinCrewMod().modifyPercent(id, CREW_REQ_PERCENT);
        
        // 6. Heavy Weapon Optimization
        stats.getDynamic().getMod(Stats.LARGE_BALLISTIC_MOD).modifyFlat(id, -LARGE_OP_REDUCTION);
        // Optional: Uncomment if you want Large Energy cheap too
        // stats.getDynamic().getMod(Stats.LARGE_ENERGY_MOD).modifyFlat(id, -LARGE_OP_REDUCTION);
    }

    @Override
    public boolean isApplicableToShip(ShipAPI ship) {
        // You might want to restrict this to Solvaris ships only later!
        return true; 
    }

    @Override
    public String getDescriptionParam(int index, HullSize hullSize) {
        switch (index) {
            case 0: return "" + (int) GROUND_SUPPORT;
            case 1: return "" + (int) RANGE_BONUS + "%";
            case 2: return "" + (int) PD_RANGE_BONUS + "%";
            case 3: return "" + (int) BURN_LEVEL_BONUS;
            case 4: return "" + (int) CREW_REQ_PERCENT + "%";
            case 5: return "" + (int) LARGE_OP_REDUCTION;
            default: return null;
        }
    }
}