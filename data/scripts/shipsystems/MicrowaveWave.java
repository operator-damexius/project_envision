package data.scripts.shipsystems;

import java.awt.Color;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.util.Misc;

public class MicrowaveWave extends BaseShipSystemScript {

    // --- CONFIGURATION ---
    public static final float WAVE_SPEED = 1500f;   
    public static final float MAX_RANGE = 4000f;    
    public static final float EMP_DAMAGE = 4000000f;   
    public static final float ENERGY_DAMAGE = 800000f; 
    public static final float WAVE_THICKNESS = 150f; 

    // --- COLORS ---
    public static final Color WAVE_COLOR = new Color(100, 220, 255, 200);
    public static final Color JITTER_COLOR = new Color(100, 200, 255, 50);

    // STATE VARIABLES
    private Set<ShipAPI> friedShips = new HashSet<ShipAPI>();
    private float runTime = 0f; // Tracks how long the wave has been active

    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        ShipAPI ship = null;
        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
        } else {
            return;
        }

        CombatEngineAPI engine = Global.getCombatEngine();

        // RESET LOGIC
        if (state == State.IDLE || state == State.COOLDOWN) {
            friedShips.clear();
            runTime = 0f; // Reset timer when system turns off
            return;
        }

        // ACTIVE LOGIC
        if (state == State.ACTIVE || state == State.OUT) {
            
            // 1. MANUALLY CALCULATE TIME
            runTime += engine.getElapsedInLastFrame();
            
            float currentRadius = runTime * WAVE_SPEED;

            if (currentRadius > MAX_RANGE) return;

            // 2. VISUALS: WAVE FRONT
            int particles = 15; 
            for (int i = 0; i < particles; i++) {
                float angle = (float) Math.random() * 360f;
                Vector2f spawnLoc = MathUtils_getPointOnCircumference(ship.getLocation(), currentRadius, angle);
                Vector2f velocity = Vector2f.sub(spawnLoc, ship.getLocation(), null);
                if (velocity.length() > 0) {
                     velocity.normalise();
                     velocity.scale(WAVE_SPEED * 0.4f); 
                     Vector2f.add(velocity, ship.getVelocity(), velocity);
                }

                engine.addHitParticle(
                    spawnLoc, 
                    velocity, 
                    80f + (float)Math.random() * 40f, 
                    0.8f, 
                    0.5f, 
                    WAVE_COLOR
                );
            }

            // [REMOVED "addRipple" CAUSING CRASH]
            
            // 3. COLLISION LOGIC
            List<ShipAPI> targets = engine.getShips();
            for (ShipAPI target : targets) {
                if (target == ship) continue;
                if (target.isHulk()) continue;
                if (target.getOwner() == ship.getOwner()) continue;
                if (friedShips.contains(target)) continue;

                float dist = Misc.getDistance(ship.getLocation(), target.getLocation());
                
                // Hit check: Is the ship touching the wave ring?
                if (Math.abs(dist - currentRadius) < WAVE_THICKNESS) {
                    performMicrowaveFry(engine, ship, target);
                    friedShips.add(target);
                }
                // Safety catch: If wave skipped over a close ship
                else if (dist < currentRadius && !friedShips.contains(target)) {
                     performMicrowaveFry(engine, ship, target);
                     friedShips.add(target);
                }
            }
        }
    }
    
    private void performMicrowaveFry(CombatEngineAPI engine, ShipAPI source, ShipAPI target) {
        Global.getSoundPlayer().playSound("system_emp_emitter_impact", 1.0f, 1.0f, target.getLocation(), target.getVelocity());

        engine.spawnEmpArc(
            source, source.getLocation(), 
            target, target,  
            DamageType.ENERGY,
            ENERGY_DAMAGE,  
            EMP_DAMAGE,     
            1000000f, 
            "tachyon_lance_emp_impact", 
            30f, 
            new Color(255, 255, 255, 255), 
            new Color(100, 200, 255, 255)
        );

        target.setJitter(source.getId(), JITTER_COLOR, 1.0f, 15, 10f);
        
        if (target.getShield() != null && target.getShield().isOn()) {
            target.getFluxTracker().forceOverload(0);
        }
    }

    public void unapply(MutableShipStatsAPI stats, String id) {
        friedShips.clear();
        runTime = 0f;
    }
    
    private Vector2f MathUtils_getPointOnCircumference(Vector2f center, float radius, float angle) {
        double rads = Math.toRadians(angle);
        float xOffset = (float)Math.cos(rads) * radius;
        float yOffset = (float)Math.sin(rads) * radius;
        return new Vector2f(center.x + xOffset, center.y + yOffset);
    }
    
    // --- STATUS TEXT ---
    public StatusData getStatusData(int index, State state, float effectLevel) {
        if (index == 0 && (state == State.ACTIVE || state == State.OUT)) {
            return new StatusData("MICROWAVE PULSE", false);
        }
        return null;
    }
}