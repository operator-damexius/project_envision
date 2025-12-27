package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class SolvarisPhaseCoils extends BaseHullMod {

    public static final float PHASE_SPEED_BONUS = 50f; // Go fast
    public static final float COOLDOWN_REDUCTION = 50f; // Re-cloak twice as fast
    public static final float FLUX_DISSIPATION = 10f;

    @Override
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        // FIX: Changed from getPhaseCloakSpeedMod to getMaxSpeed
        // This increases the ship's base speed, which also makes it faster in phase.
        stats.getMaxSpeed().modifyPercent(id, PHASE_SPEED_BONUS);
        
        stats.getPhaseCloakCooldownBonus().modifyMult(id, 1f - (COOLDOWN_REDUCTION / 100f));
        stats.getFluxDissipation().modifyPercent(id, FLUX_DISSIPATION);
    }

    @Override
    public String getDescriptionParam(int index, HullSize hullSize) {
        if (index == 0) return "" + (int) PHASE_SPEED_BONUS + "%";
        if (index == 1) return "" + (int) COOLDOWN_REDUCTION + "%";
        if (index == 2) return "" + (int) FLUX_DISSIPATION + "%";
        return null;
    }
}