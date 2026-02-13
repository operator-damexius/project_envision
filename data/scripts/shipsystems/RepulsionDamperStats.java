package data.scripts.shipsystems;

import java.util.List;
import org.lwjgl.util.vector.Vector2f;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.util.Misc;

public class RepulsionDamperStats extends BaseShipSystemScript {

    // --- CONFIGURATION ---
    public static final float RANGE = 2000f;          
    public static final float PUSH_FORCE = 3000f;     
    public static final float MISSILE_PUSH = 5000f;   
    
    // --- DEFENSIVE STATS ---
    public static final float FLUX_DISSIPATION_MULT = 2f; 
    public static final float DAMAGE_RESISTANCE = 0.1f;   // 90% Damage Reduction

    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        ShipAPI ship = null;
        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
        } else {
            return;
        }

        // 1. STAT BUFFS
        stats.getFluxDissipation().modifyMult(id, FLUX_DISSIPATION_MULT);
        stats.getHullDamageTakenMult().modifyMult(id, DAMAGE_RESISTANCE);
        stats.getArmorDamageTakenMult().modifyMult(id, DAMAGE_RESISTANCE);
        
        // 2. SHIELD LOGIC
        if (ship.getShield() != null) {
            stats.getShieldDamageTakenMult().modifyMult(id, DAMAGE_RESISTANCE);
            stats.getShieldUnfoldRateMult().modifyPercent(id, 2000f);
            stats.getShieldAbsorptionMult().modifyMult(id, 0f); 
            
            if (state == State.ACTIVE) {
                ship.getShield().toggleOn();
            }
        }

        // 3. ACTIVE LOGIC
        if (state == State.ACTIVE) {
            CombatEngineAPI engine = Global.getCombatEngine();
            Vector2f shipLoc = ship.getLocation();

            // --- VISUALS REMOVED ---
            // The "Glow" particles have been deleted. 
            // The "Lining" (Jitter) is handled by repulsion_damper.system automatically.
            
            // --- PHYSICS: PUSH SHIPS ---
            List<ShipAPI> allShips = engine.getShips();
            for (ShipAPI target : allShips) {
                if (target == ship) continue;
                if (target.isHulk()) continue;
                if (target.getOwner() == ship.getOwner()) continue;
                if (target.isPhased()) continue;
                
                float dist = Misc.getDistance(shipLoc, target.getLocation());
                if (dist > RANGE) continue;
                
                float angleToTarget = Misc.getAngleInDegrees(shipLoc, target.getLocation());
                Vector2f pushDir = Misc.getUnitVectorAtDegreeAngle(angleToTarget);
                
                float forceMod = 1f - (dist / RANGE);
                if (forceMod < 0) forceMod = 0;
                
                float massMod = 200f / (target.getMass() + 1f); 
                if (massMod > 1f) massMod = 1f;

                Vector2f force = new Vector2f(pushDir);
                force.scale(PUSH_FORCE * forceMod * massMod * 50f * engine.getElapsedInLastFrame());
                
                Vector2f.add(target.getVelocity(), force, target.getVelocity());
            }
            
            // --- PHYSICS: PUSH MISSILES ---
            List<MissileAPI> nearbyMissiles = engine.getMissiles();
            for (MissileAPI missile : nearbyMissiles) {
                if (missile.getOwner() == ship.getOwner()) continue;
                
                float dist = Misc.getDistance(shipLoc, missile.getLocation());
                if (dist > RANGE) continue;
                
                float angleToMissile = Misc.getAngleInDegrees(shipLoc, missile.getLocation());
                Vector2f pushDir = Misc.getUnitVectorAtDegreeAngle(angleToMissile);
                
                Vector2f force = new Vector2f(pushDir);
                force.scale(MISSILE_PUSH * engine.getElapsedInLastFrame());
                
                Vector2f.add(missile.getVelocity(), force, missile.getVelocity());
                missile.setAngularVelocity(missile.getAngularVelocity() + (float)Math.random() * 200f - 100f);
            }
        }
    }

    public void unapply(MutableShipStatsAPI stats, String id) {
        stats.getFluxDissipation().unmodify(id);
        stats.getHullDamageTakenMult().unmodify(id);
        stats.getArmorDamageTakenMult().unmodify(id);
        
        stats.getShieldDamageTakenMult().unmodify(id);
        stats.getShieldUnfoldRateMult().unmodify(id);
        stats.getShieldAbsorptionMult().unmodify(id);
    }

    public StatusData getStatusData(int index, State state, float effectLevel) {
        if (index == 0) return new StatusData("REPULSION FIELD ACTIVE", false);
        if (index == 1) return new StatusData("KINETIC INVERSION", false);
        return null;
    }
}