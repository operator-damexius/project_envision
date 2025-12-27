package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class SolvarisPhotonic extends BaseHullMod {

    public static final float BEAM_DAMAGE = 20f; // +20% Damage
    public static final float BEAM_RANGE = 200f; // +200 Range (Flat bonus)
    public static final float TURRET_SPEED = 20f; // Helps keep beams on target

    @Override
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getBeamWeaponDamageMult().modifyPercent(id, BEAM_DAMAGE);
        stats.getBeamWeaponRangeBonus().modifyFlat(id, BEAM_RANGE);
        stats.getWeaponTurnRateBonus().modifyPercent(id, TURRET_SPEED);
    }

    @Override
    public String getDescriptionParam(int index, HullSize hullSize) {
        if (index == 0) return "" + (int) BEAM_DAMAGE + "%";
        if (index == 1) return "" + (int) BEAM_RANGE;
        if (index == 2) return "" + (int) TURRET_SPEED + "%";
        return null;
    }
}