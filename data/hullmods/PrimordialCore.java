package data.hullmods;

import java.util.List;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipEngineControllerAPI;
import com.fs.starfarer.api.combat.ShipEngineControllerAPI.ShipEngineAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.impl.campaign.ids.Personalities;
import com.fs.starfarer.api.util.IntervalUtil;

public class PrimordialCore extends BaseHullMod {

    // =========================================================================
    // --- CONFIGURATION ---
    // =========================================================================
    
    // --- OFFENSIVE (Synaptic Precision) ---
    public static final float RANGE_BONUS = 100f;        
    public static final float WEAPON_TURN = 100f;
    public static final float ATTACK_DMG_BONUS = 25f;    
    public static final float ATTACK_ROF_BONUS = 25f;
    public static final float TIME_FLOW_BONUS = 10f;     

    // --- DEFENSIVE (Carapace & Regen) ---
    public static final float HE_RESISTANCE = 20f;
    public static final float EMP_RESISTANCE = 50f;
    public static final float HULL_REPAIR_PERCENT = 0.5f; 
    public static final float ARMOR_REGEN_MIN = 10f;      
    public static final float INSTANT_FIX_CHANCE = 0.15f; 

    // --- MOBILITY (Instinct & Reflexes) ---
    public static final float BASE_MANEUVER = 50f;
    public static final float AVOID_SPEED_BONUS = 50f;
    public static final float AVOID_MANEUVER_BONUS = 100f;

    // --- INTERNAL KEYS ---
    private static final String DATA_KEY = "primordial_core_data";
    private static final String BUFF_ID = "primordial_core_buffs";
    
    // --- MODES ---
    public static final int MODE_ATTACK = 1;
    public static final int MODE_AVOID = 2;
    public static final int MODE_MANEUVER = 3;

    // =========================================================================
    // --- STATIC STATS (Refit Screen) ---
    // =========================================================================
    @Override
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        
        // 1. OMNISCIENCE
        stats.getBallisticWeaponRangeBonus().modifyPercent(id, RANGE_BONUS);
        stats.getEnergyWeaponRangeBonus().modifyPercent(id, RANGE_BONUS);
        stats.getMissileWeaponRangeBonus().modifyPercent(id, RANGE_BONUS / 2f);
        stats.getSensorStrength().modifyPercent(id, 60f);
        stats.getSightRadiusMod().modifyPercent(id, 40f);

        // 2. BIOLOGICAL AUTOMATION
        stats.getMinCrewMod().modifyMult(id, 0f);
        stats.getMaxCrewMod().modifyMult(id, 0f);
        stats.getCombatWeaponRepairTimeMult().modifyMult(id, 0.1f); 
        stats.getCombatEngineRepairTimeMult().modifyMult(id, 0.1f);

        // 3. PERFECT ACCURACY
        stats.getMaxRecoilMult().modifyMult(id, 0f);
        stats.getRecoilPerShotMult().modifyMult(id, 0f);
        stats.getRecoilDecayMult().modifyMult(id, 10f);
        stats.getWeaponTurnRateBonus().modifyPercent(id, WEAPON_TURN);

        // 4. RESISTANCES
        stats.getHighExplosiveDamageTakenMult().modifyMult(id, 1f - (HE_RESISTANCE / 100f));
        stats.getEmpDamageTakenMult().modifyMult(id, 1f - (EMP_RESISTANCE / 100f));

        // 5. HYPER-REFLEXES
        stats.getAcceleration().modifyPercent(id, BASE_MANEUVER);
        stats.getDeceleration().modifyPercent(id, BASE_MANEUVER);
        stats.getTurnAcceleration().modifyPercent(id, BASE_MANEUVER);
        stats.getMaxTurnRate().modifyPercent(id, BASE_MANEUVER);

        // 6. TEMPORAL SUPERIORITY
        stats.getTimeMult().modifyPercent(id, TIME_FLOW_BONUS);
    }

    // =========================================================================
    // --- COMBAT LOOP ---
    // =========================================================================
    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        if (!ship.isAlive()) return;

        // 1. Initialize Timer
        IntervalUtil timer = (IntervalUtil) ship.getCustomData().get(DATA_KEY);
        if (timer == null) {
            timer = new IntervalUtil(0.5f, 0.5f);
            ship.getCustomData().put(DATA_KEY, timer);
            applyMode(ship, MODE_MANEUVER);
        }

        timer.advance(amount);

        // 2. Main Logic Loop (Runs every 0.5s)
        if (timer.intervalElapsed()) {
            
            // A. Adaptive Combat Logic
            int newMode = analyzeSituation(ship);
            applyMode(ship, newMode);

            // B. Regeneration
            regenerateShip(ship);
            
            // C. Neural Reconnect (Fix disabled guns)
            if (Math.random() < INSTANT_FIX_CHANCE) {
                restoreRandomSystem(ship);
            }
        }
    }

    // =========================================================================
    // --- ADAPTIVE LOGIC ---
    // =========================================================================

    private int analyzeSituation(ShipAPI ship) {
        float myFlux = ship.getFluxTracker().getFluxLevel();
        float myHull = ship.getHullLevel();
        ShipAPI target = ship.getShipTarget();

        if (myFlux > 0.85f || myHull < 0.35f) return MODE_AVOID;

        if (target != null && !target.isAlly()) {
            float targetFlux = target.getFluxTracker().getFluxLevel();
            if (target.getFluxTracker().isOverloadedOrVenting() || targetFlux > 0.80f || target.getHullLevel() < 0.30f) {
                return MODE_ATTACK;
            }
        }
        return MODE_MANEUVER;
    }

    private void applyMode(ShipAPI ship, int newMode) {
        MutableShipStatsAPI stats = ship.getMutableStats();
        
        stats.getEnergyWeaponDamageMult().unmodify(BUFF_ID);
        stats.getBallisticWeaponDamageMult().unmodify(BUFF_ID);
        stats.getEnergyRoFMult().unmodify(BUFF_ID);
        stats.getBallisticRoFMult().unmodify(BUFF_ID);
        stats.getMaxSpeed().unmodify(BUFF_ID);
        stats.getHullDamageTakenMult().unmodify(BUFF_ID);
        stats.getFluxDissipation().unmodify(BUFF_ID);

        if (newMode == MODE_ATTACK) {
            stats.getEnergyWeaponDamageMult().modifyPercent(BUFF_ID, ATTACK_DMG_BONUS);
            stats.getBallisticWeaponDamageMult().modifyPercent(BUFF_ID, ATTACK_DMG_BONUS);
            stats.getEnergyRoFMult().modifyPercent(BUFF_ID, ATTACK_ROF_BONUS);
            stats.getBallisticRoFMult().modifyPercent(BUFF_ID, ATTACK_ROF_BONUS);
            if (ship.getCaptain() != null) ship.getCaptain().setPersonality(Personalities.AGGRESSIVE);
        
        } else if (newMode == MODE_AVOID) {
            stats.getMaxSpeed().modifyPercent(BUFF_ID, AVOID_SPEED_BONUS);
            stats.getAcceleration().modifyPercent(BUFF_ID, AVOID_MANEUVER_BONUS);
            stats.getHullDamageTakenMult().modifyMult(BUFF_ID, 0.8f); 
            if (ship.getCaptain() != null) ship.getCaptain().setPersonality(Personalities.CAUTIOUS);
            
        } else { // MANEUVER
            stats.getFluxDissipation().modifyPercent(BUFF_ID, 20f);
            if (ship.getCaptain() != null) ship.getCaptain().setPersonality(Personalities.STEADY);
        }
    }

    // =========================================================================
    // --- REGENERATION & REPAIR ---
    // =========================================================================

    private void regenerateShip(ShipAPI ship) {
        float maxHull = ship.getMaxHitpoints();
        float currentHull = ship.getHitpoints();
        
        if (currentHull < maxHull && currentHull > 0) {
            float repairAmount = maxHull * (HULL_REPAIR_PERCENT / 100f) * 0.5f;
            if (currentHull < maxHull * 0.3f) repairAmount *= 2f;
            ship.setHitpoints(Math.min(maxHull, currentHull + repairAmount));
        }

        float[][] grid = ship.getArmorGrid().getGrid();
        float maxArmor = ship.getArmorGrid().getMaxArmorInCell();
        float healPerTick = maxArmor * (ARMOR_REGEN_MIN / 100f) / 120f;
        boolean didHeal = false;

        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[0].length; y++) {
                if (grid[x][y] < maxArmor) {
                    grid[x][y] = Math.min(maxArmor, grid[x][y] + healPerTick);
                    didHeal = true;
                }
            }
        }
        if (didHeal) ship.syncWithArmorGridState();
    }

    private void restoreRandomSystem(ShipAPI ship) {
        List<WeaponAPI> weapons = ship.getAllWeapons();
        for (WeaponAPI w : weapons) {
            if (w.isDisabled()) {
                w.repair();
                w.setCurrHealth(w.getMaxHealth());
                return;
            }
        }
        ShipEngineControllerAPI ec = ship.getEngineController();
        if (ec != null) {
            for (ShipEngineAPI e : ec.getShipEngines()) {
                if (e.isDisabled()) {
                    e.repair();
                    return;
                }
            }
        }
    }

    @Override
    public String getDescriptionParam(int index, HullSize hullSize) {
        if (index == 0) return "" + (int)RANGE_BONUS + "%";
        if (index == 1) return "" + (int)TIME_FLOW_BONUS + "%";
        if (index == 2) return "" + (int)ARMOR_REGEN_MIN + "%";
        if (index == 3) return "Adaptive";
        return null;
    }
}