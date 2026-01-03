package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class SolvarisDivineArchitecture extends BaseHullMod {

    // =========================================================================
    // --- CONFIGURATION (BALANCED) ---
    // =========================================================================
    
    // --- OFFENSIVE (Photonic & Grid) ---
    public static final float RANGE_BONUS = 60f;          
    public static final float BEAM_DAMAGE = 20f;          
    public static final float TURRET_TURN = 50f;          

    // --- FLUX & GRID (Grid) ---
    public static final float FLUX_DISSIPATION = 25f;     
    public static final float EMP_RESISTANCE = 50f;       

    // --- DEFENSIVE & DOCTRINE (Prime Doctrine) ---
    public static final float ENERGY_DMG_REDUCTION = 15f; 
    public static final float SHIELD_SPEED = 50f;         
    
    // Removed SOLAR_SHIELDING to fix the crash (Energy reduction covers the combat usage)

    // --- LOGISTICS (Prime Doctrine) ---
    public static final float AMMO_BONUS = 50f;           
    public static final float PEAK_TIME = 25f;            
    public static final float CR_RECOVERY = 25f;          

    // =========================================================================
    // --- APPLY EFFECTS ---
    // =========================================================================
    @Override
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        
        // 1. DIVINE OPTICS (Range & Beams)
        stats.getBallisticWeaponRangeBonus().modifyPercent(id, RANGE_BONUS);
        stats.getEnergyWeaponRangeBonus().modifyPercent(id, RANGE_BONUS);
        stats.getBeamWeaponDamageMult().modifyPercent(id, BEAM_DAMAGE);
        stats.getWeaponTurnRateBonus().modifyPercent(id, TURRET_TURN);
        stats.getBeamWeaponTurnRateBonus().modifyPercent(id, TURRET_TURN);

        // 2. GRID INTEGRATION (Flux & EMP)
        stats.getFluxDissipation().modifyPercent(id, FLUX_DISSIPATION);
        stats.getEmpDamageTakenMult().modifyMult(id, 1f - (EMP_RESISTANCE / 100f));

        // 3. PRIME DEFENSES (Shields & Armor)
        stats.getEnergyDamageTakenMult().modifyMult(id, 1f - (ENERGY_DMG_REDUCTION / 100f));
        stats.getShieldTurnRateMult().modifyPercent(id, SHIELD_SPEED);
        stats.getShieldUnfoldRateMult().modifyPercent(id, SHIELD_SPEED);
        
        // 4. ETERNAL MAGAZINE (Ammo & Logistics)
        stats.getBallisticAmmoBonus().modifyPercent(id, AMMO_BONUS);
        stats.getEnergyAmmoBonus().modifyPercent(id, AMMO_BONUS);
        stats.getMissileAmmoBonus().modifyPercent(id, AMMO_BONUS);
        
        stats.getPeakCRDuration().modifyPercent(id, PEAK_TIME);
        stats.getBaseCRRecoveryRatePercentPerDay().modifyPercent(id, CR_RECOVERY);
    }

    // =========================================================================
    // --- DESCRIPTION PARAMETERS ---
    // =========================================================================
    @Override
    public String getDescriptionParam(int index, HullSize hullSize) {
        if (index == 0) return "" + (int)RANGE_BONUS + "%";
        if (index == 1) return "" + (int)BEAM_DAMAGE + "%";
        if (index == 2) return "" + (int)FLUX_DISSIPATION + "%";
        if (index == 3) return "" + (int)ENERGY_DMG_REDUCTION + "%";
        if (index == 4) return "" + (int)AMMO_BONUS + "%";
        return null;
    }
}