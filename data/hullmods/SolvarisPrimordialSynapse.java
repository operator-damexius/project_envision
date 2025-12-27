package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class SolvarisPrimordialSynapse extends BaseHullMod {

    // --- CONFIGURATION ---
    public static final float MANEUVER_BONUS = 50f;     
    public static final float WEAPON_TURN = 100f;       
    public static final float RANGE_BONUS = 50f;        
    public static final float SENSOR_BONUS = 60f;       
    
    // The "God Stat" - The ship experiences time faster than reality
    public static final float TIME_FLOW_BONUS = 10f;    

    @Override
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        
        // 1. Movement (Hyper-Reflexes)
        stats.getAcceleration().modifyPercent(id, MANEUVER_BONUS);
        stats.getDeceleration().modifyPercent(id, MANEUVER_BONUS);
        stats.getTurnAcceleration().modifyPercent(id, MANEUVER_BONUS);
        stats.getMaxTurnRate().modifyPercent(id, MANEUVER_BONUS);
        
        // 2. Combat Precision (Zero Recoil)
        stats.getWeaponTurnRateBonus().modifyPercent(id, WEAPON_TURN);
        stats.getBeamWeaponTurnRateBonus().modifyPercent(id, WEAPON_TURN);
        
        // Perfect aim mechanics
        stats.getMaxRecoilMult().modifyMult(id, 0f);
        stats.getRecoilPerShotMult().modifyMult(id, 0f);
        stats.getRecoilDecayMult().modifyMult(id, 10f); // Recoil recovers instantly

        // 3. Synaptic Targeting (Universal Range)
        stats.getBallisticWeaponRangeBonus().modifyPercent(id, RANGE_BONUS);
        stats.getEnergyWeaponRangeBonus().modifyPercent(id, RANGE_BONUS);
        stats.getMissileWeaponRangeBonus().modifyPercent(id, RANGE_BONUS); // NEW: Missiles added

        // 4. Omniscience (Sensors)
        stats.getSensorStrength().modifyPercent(id, SENSOR_BONUS);
        stats.getSightRadiusMod().modifyPercent(id, SENSOR_BONUS);
        
        // 5. TEMPORAL ACCELERATION (The Boss Buff)
        stats.getTimeMult().modifyPercent(id, TIME_FLOW_BONUS);
    }

    @Override
    public String getDescriptionParam(int index, HullSize hullSize) {
        if (index == 0) return "" + (int)MANEUVER_BONUS + "%";
        if (index == 1) return "" + (int)RANGE_BONUS + "%";
        if (index == 2) return "" + (int)TIME_FLOW_BONUS + "%"; 
        if (index == 3) return "Zero"; 
        return null;
    }
}