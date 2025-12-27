package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

public class SolvarisPrimeDoctrine extends BaseHullMod {
    
    // =========================================================================
    // STATS CONFIGURATION
    // =========================================================================
    
    // Weapon Capacity
    private static final float AMMO_BONUS = 100f;           // +100% Ballistic/Energy Ammo
    private static final float MISSILE_AMMO_BONUS = 200f;   // +200% Missiles
    
    // Ship Endurance
    private static final float PEAK_TIME_BONUS = 100f;       // +100% Peak Performance Time
    private static final float DEGRADE_REDUCTION = 25f;     // -25% CR Degradation Rate
    
    // Environmental Hardening (Solar Shielding)
    private static final float CORONA_RESISTANCE = 200f;     // -200% Corona Damage (0.25 mult)
    private static final float ENERGY_DMG_REDUCTION = 10f;  // -10% Energy Damage Taken (0.9 mult)
    
    // Shield Dynamics
    private static final float SHIELD_TURN_BONUS = 100f;    // +100% Turn Speed
    private static final float SHIELD_UNFOLD_BONUS = 500f;  // +500% Raise Speed
    
    // Turret Mechanics
    private static final float TURRET_SPEED_BONUS = 75f;    // +75% Rotation Speed

    // =========================================================================
    // LOGIC
    // =========================================================================
    
    @Override
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        
        // 1. Ammo Capacity
        stats.getBallisticAmmoBonus().modifyPercent(id, AMMO_BONUS);
        stats.getEnergyAmmoBonus().modifyPercent(id, AMMO_BONUS);
        stats.getMissileAmmoBonus().modifyPercent(id, MISSILE_AMMO_BONUS);
        
        // 2. Combat Endurance
        stats.getPeakCRDuration().modifyPercent(id, PEAK_TIME_BONUS);
        stats.getCRLossPerSecondPercent().modifyMult(id, 1f - (DEGRADE_REDUCTION / 100f));
        
        // 3. Environmental Protection
        // 0.25f multiplier means 75% reduction
        stats.getDynamic().getStat(Stats.CORONA_EFFECT_MULT).modifyMult(id, 1f - (CORONA_RESISTANCE / 100f));
        
        // 0.9f multiplier means 10% reduction
        stats.getEnergyDamageTakenMult().modifyMult(id, 1f - (ENERGY_DMG_REDUCTION / 100f));
        stats.getEnergyShieldDamageTakenMult().modifyMult(id, 1f - (ENERGY_DMG_REDUCTION / 100f));
        
        // 4. Defensive Response
        stats.getShieldTurnRateMult().modifyPercent(id, SHIELD_TURN_BONUS);
        stats.getShieldUnfoldRateMult().modifyPercent(id, SHIELD_UNFOLD_BONUS);
        
        // 5. Offensive Response
        stats.getWeaponTurnRateBonus().modifyPercent(id, TURRET_SPEED_BONUS);
        stats.getBeamWeaponTurnRateBonus().modifyPercent(id, TURRET_SPEED_BONUS);
    }
    
    @Override
    public String getDescriptionParam(int index, HullSize hullSize) {
        switch (index) {
            case 0: return "" + (int) AMMO_BONUS + "%";
            case 1: return "" + (int) MISSILE_AMMO_BONUS + "%";
            case 2: return "" + (int) PEAK_TIME_BONUS + "%";
            case 3: return "" + (int) DEGRADE_REDUCTION + "%";
            case 4: return "" + (int) CORONA_RESISTANCE + "%";
            case 5: return "" + (int) ENERGY_DMG_REDUCTION + "%";
            case 6: return "" + (int) SHIELD_TURN_BONUS + "%";
            case 7: return "" + (int) SHIELD_UNFOLD_BONUS + "%";
            case 8: return "" + (int) TURRET_SPEED_BONUS + "%";
            default: return null;
        }
    }
    
    @Override
    public boolean isApplicableToShip(ShipAPI ship) {
        return true;
    }
}