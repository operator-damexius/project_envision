package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class SolvarisGrid extends BaseHullMod {

    // STATS
    public static final float EMP_RESISTANCE = 50f; // 50% Less EMP damage (Storm Immunity)
    public static final float RANGE_BONUS = 10f;    // 10% Range (AI Targeting)
    public static final float FLUX_DISSIPATION = 10f; // 10% Better Vents

    @Override
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        
        // 1. EMP Resistance
        // 1f - 0.5f = 0.5f multiplier (Half damage)
        stats.getEmpDamageTakenMult().modifyMult(id, 1f - (EMP_RESISTANCE / 100f));
        
        // 2. Range Boost (Ballistic & Energy)
        stats.getBallisticWeaponRangeBonus().modifyPercent(id, RANGE_BONUS);
        stats.getEnergyWeaponRangeBonus().modifyPercent(id, RANGE_BONUS);
        
        // 3. Flux Efficiency
        stats.getFluxDissipation().modifyPercent(id, FLUX_DISSIPATION);
    }

    @Override
    public String getDescriptionParam(int index, HullSize hullSize) {
        if (index == 0) return "" + (int) EMP_RESISTANCE + "%";
        if (index == 1) return "" + (int) RANGE_BONUS + "%";
        if (index == 2) return "" + (int) FLUX_DISSIPATION + "%";
        return null;
    }
}