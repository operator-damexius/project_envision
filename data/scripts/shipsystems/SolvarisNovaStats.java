package data.scripts.shipsystems;

import java.awt.Color;
import java.util.List;
import org.lwjgl.util.vector.Vector2f;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.util.Misc;

// --- GRAPHICSLIB IMPORTS ---
import org.dark.shaders.distortion.DistortionShader;
import org.dark.shaders.distortion.RippleDistortion;
import org.dark.shaders.light.LightShader;
import org.dark.shaders.light.StandardLight;

public class SolvarisNovaStats extends BaseShipSystemScript {

    // --- CONFIGURATION ---
    public static final float RANGE = 4000f;        
    public static final float EMP_DAMAGE = 100000f; 
    public static final float HULL_DAMAGE = 1000f;  
    public static final float KICKBACK = 600f;      
    
    // --- VISUAL COLORS ---
    public static final Color PARTICLE_CORE = new Color(200, 255, 255, 255);
    public static final Color FLASH_COLOR = new Color(100, 220, 255, 255); 
    public static final Color INVISIBLE = new Color(0, 0, 0, 0);

    private boolean hasFired = false;

    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        ShipAPI ship = null;
        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
        } else {
            return;
        }

        // 1. CHARGE UP
        if (state == State.IN) {
            hasFired = false; 
            if (Math.random() > 0.5) {
                float r = 1000f * (float)Math.sqrt(Math.random()); 
                float theta = (float)Math.random() * 360f;
                Vector2f spawnLoc = Misc.getUnitVectorAtDegreeAngle(theta);
                spawnLoc.scale(r);
                Vector2f.add(ship.getLocation(), spawnLoc, spawnLoc);

                Vector2f vel = Vector2f.sub(ship.getLocation(), spawnLoc, null);
                vel.scale(2.0f);
                
                Global.getCombatEngine().addHitParticle(spawnLoc, vel, 10f, 0.5f, 0.5f, FLASH_COLOR);
            }
        }

        // 2. DETONATION
        if (state == State.ACTIVE && !hasFired) {
            CombatEngineAPI engine = Global.getCombatEngine();
            Vector2f loc = ship.getLocation();
            Vector2f vel = ship.getVelocity();

            // --- A. GRAPHICSLIB EFFECTS ---
            if (Global.getSettings().getModManager().isModEnabled("shaderLib")) {
                RippleDistortion ripple = new RippleDistortion(loc, vel);
                ripple.setSize(RANGE);              
                ripple.setIntensity(200f);          
                ripple.setFrameRate(60f);           
                ripple.setLifetime(1.0f);           
                ripple.setAutoFadeSizeTime(0.4f);   
                ripple.setAutoFadeIntensityTime(0.8f); 
                DistortionShader.addDistortion(ripple); 

                StandardLight light = new StandardLight(loc, vel, new Vector2f(0,0), null);
                light.setColor(FLASH_COLOR);
                light.setSize(RANGE * 1.5f);        
                light.setIntensity(4.0f);           
                light.setLifetime(0.4f);            
                light.setAutoFadeOutTime(0.8f);     
                LightShader.addLight(light);
            }
            
            // --- B. VANILLA VISUALS ---
            engine.addSmoothParticle(loc, vel, 2000f, 1.0f, 0.5f, FLASH_COLOR);
            engine.addSmoothParticle(loc, vel, 1500f, 1.0f, 0.3f, Color.WHITE);

            for (int i = 0; i < 360; i += 1) { 
                float angle = (float) Math.toRadians(i);
                Vector2f pVel = new Vector2f((float)Math.cos(angle) * 5000f, (float)Math.sin(angle) * 5000f);
                Vector2f.add(pVel, vel, pVel);
                engine.addHitParticle(loc, pVel, 30f, 1.0f, 1.5f, PARTICLE_CORE);
            }

            Global.getSoundPlayer().playSound("system_emp_emitter_impact", 0.5f, 2.5f, loc, new Vector2f(0,0));

            // --- C. LOGIC ---
            List<ShipAPI> targets = engine.getShips();
            for (ShipAPI target : targets) {
                if (target == ship) continue; 
                if (target.getOwner() == ship.getOwner()) continue; // <--- ALLY IMMUNITY ADDED HERE
                if (target.isHulk()) continue;
                if (target.isPhased()) continue;

                float distance = Misc.getDistance(loc, target.getLocation());
                if (distance > RANGE) continue;

                int arcs = 25; 
                for (int i = 0; i < arcs; i++) {
                    engine.spawnEmpArc(
                        ship, loc, target, target,
                        DamageType.ENERGY, 
                        HULL_DAMAGE / arcs, 
                        EMP_DAMAGE / arcs,      
                        100000f,                
                        "tachyon_lance_fire",   
                        35f,                    
                        INVISIBLE, 
                        INVISIBLE  
                    );
                }
                
                if (target.getFluxTracker() != null) {
                    target.getFluxTracker().increaseFlux(target.getFluxTracker().getMaxFlux() * 0.5f, true);
                }

                float angle = Misc.getAngleInDegrees(loc, target.getLocation());
                float angleRad = (float)Math.toRadians(angle);
                Vector2f push = new Vector2f((float)Math.cos(angleRad) * KICKBACK, (float)Math.sin(angleRad) * KICKBACK);
                Vector2f.add(target.getVelocity(), push, target.getVelocity());
            }
            hasFired = true; 
        }
    }

    public void unapply(MutableShipStatsAPI stats, String id) {
        hasFired = false;
    }

    public StatusData getStatusData(int index, State state, float effectLevel) {
        if (index == 0 && state == State.IN) return new StatusData("CHARGING NEURAL NOVA", true);
        if (index == 0 && state == State.ACTIVE) return new StatusData("SYSTEM DISCHARGE", true);
        return null;
    }
}