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

// --- WARNING: GRAPHICSLIB DEPENDENCY ---
import org.dark.shaders.distortion.DistortionShader;
import org.dark.shaders.distortion.RippleDistortion;

public class SingularityStats extends BaseShipSystemScript {

    // --- CONFIGURATION ---
    public static final float ACTIVE_TIME = 10f;     
    public static final float PULL_RANGE = 3500f;    
    public static final float PULL_STRENGTH = 3000f; 
    public static final float KILL_RADIUS = 450f;    
    
    // Burst Config
    public static final float BURST_FORCE = 4000f; 
    public static final float BURST_DAMAGE = 5000f; 
    public static final float BURST_EMP = 2000f;
    
    public static final float MELT_DPS = 9000f; 
    public static final float MAX_SPIN_SPEED = 30f; 
    
    // --- COLORS ---
    public static final Color VOID_COLOR = new Color(255, 255, 255, 255); 
    public static final Color PHOTON_RING = new Color(220, 240, 255, 150); 
    public static final Color RING_INNER = new Color(255, 255, 255, 210);
    public static final Color RING_MID = new Color(220, 235, 255, 140);   
    public static final Color RING_OUTER = new Color(200, 220, 240, 50);  
    public static final Color WARNING_COLOR = new Color(200, 255, 255, 220); 
    public static final Color NEBULA_COLOR = new Color(100, 220, 255, 100); 
    public static final Color NEBULA_CORE = new Color(255, 255, 255, 200);  
    public static final Color ARC_CORE = new Color(255, 255, 255, 255);
    public static final Color ARC_FRINGE = new Color(180, 220, 255, 200);
    public static final Color MELT_COLOR = new Color(255, 255, 255, 180);
    public static final Color JITTER_COLOR = new Color(200, 255, 255, 100);

    // --- STATE & CACHE ---
    private Vector2f targetLocation = null;
    private boolean hasBurst = false;
    
    // Reusable vectors
    private final Vector2f vTmp = new Vector2f();
    private final Vector2f vTmp2 = new Vector2f();
    private final Vector2f vSpawn = new Vector2f();

    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        ShipAPI ship = null;
        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
        } else {
            return;
        }
        
        CombatEngineAPI engine = Global.getCombatEngine();

        // 1. CHARGE UP
        if (state == State.IN) {
            hasBurst = false;
            
            if (targetLocation == null) {
                if (ship == engine.getPlayerShip()) {
                    targetLocation = new Vector2f(ship.getMouseTarget());
                } else {
                    if (ship.getShipTarget() != null) {
                        targetLocation = new Vector2f(ship.getShipTarget().getLocation());
                    } else {
                        targetLocation = new Vector2f();
                        Vector2f dir = Misc.getUnitVectorAtDegreeAngle(ship.getFacing());
                        dir.scale(1500f);
                        Vector2f.add(ship.getLocation(), dir, targetLocation);
                    }
                }
            }
            
            // Charge Visuals
            if (Math.random() > 0.6) {
                engine.spawnEmpArcVisual(ship.getLocation(), ship, targetLocation, null, 25f, ARC_CORE, ARC_FRINGE);
            }
            if (Math.random() > 0.5) {
                setRandomPointInCircle(targetLocation, 900f * effectLevel, vSpawn);
                Vector2f.sub(targetLocation, vSpawn, vTmp); 
                vTmp.scale(4.0f); 
                engine.addHitParticle(vSpawn, vTmp, 25f, 2f, 0.5f, WARNING_COLOR);
            }
            Global.getSoundPlayer().playLoop("system_entropy_loop", ship, 1.0f + effectLevel, 0.6f, targetLocation, new Vector2f(0,0));
        }

        // 2. SINGULARITY ACTIVE
        if (state == State.ACTIVE) {
            if (targetLocation == null) return; 
            
            // --- VISUALS ---
            spawnRingParticles(engine, targetLocation, 450f, 15, 500f, 200f, RING_INNER, 60f);
            spawnRingParticles(engine, targetLocation, 750f, 10, 300f, 100f, RING_MID, 80f);
            spawnRingParticles(engine, targetLocation, 1200f, 5, 150f, 50f, RING_OUTER, 120f);
            engine.addSmoothParticle(targetLocation, new Vector2f(0,0), 450f, 1f, 0.1f, PHOTON_RING);
            engine.addSmoothParticle(targetLocation, new Vector2f(0,0), 380f, 1f, 0.1f, VOID_COLOR);

            // --- PHYSICS (PULL) ---
            applySingularityPhysics(engine, ship, engine.getElapsedInLastFrame());
        }

        // 3. BURST PHASE (State OUT)
        if (state == State.OUT) {
            if (!hasBurst) {
                triggerNebulaBurst(engine, ship);
                hasBurst = true;
            }
        }
    }

    // --- THE BIG BANG LOGIC ---
    private void triggerNebulaBurst(CombatEngineAPI engine, ShipAPI source) {
        if (targetLocation == null) return;

        // 1. SOUND
        Global.getSoundPlayer().playSound("system_entropy_off", 1f, 0.7f, targetLocation, new Vector2f(0,0));
        Global.getSoundPlayer().playSound("mine_explosion", 1f, 0.8f, targetLocation, new Vector2f(0,0));

        // 2. VISUALS: NEBULA CLOUD
        engine.spawnExplosion(targetLocation, new Vector2f(0,0), VOID_COLOR, 2000f, 3f); 
        
        for (int i = 0; i < 40; i++) {
            setRandomPointInCircle(targetLocation, 300f, vSpawn);
            Vector2f.sub(vSpawn, targetLocation, vTmp); 
            vTmp.normalise();
            float speed = 200f + (float)Math.random() * 600f;
            vTmp.scale(speed);
            
            float size = 200f + (float)Math.random() * 300f;
            Color cloudColor = (Math.random() > 0.5f) ? NEBULA_COLOR : NEBULA_CORE;
            
            engine.addSmokeParticle(vSpawn, vTmp, size, 0.2f, 4f, cloudColor);
        }

        // 3. SHOCKWAVE DISTORTION
        if (Global.getSettings().getModManager().isModEnabled("shaderLib")) {
            RippleDistortion wave = new RippleDistortion(targetLocation, new Vector2f(0,0));
            wave.setSize(3000f);
            wave.setIntensity(1000f);
            wave.setFrameRate(60f);
            wave.setLifetime(0.5f);
            DistortionShader.addDistortion(wave);
        }

        // 4. PHYSICS: REPULSION SHOCKWAVE
        List<ShipAPI> targets = engine.getShips();
        for (ShipAPI target : targets) {
            if (target == source) continue;
            if (target.isPhased()) continue;

            float dist = Misc.getDistance(targetLocation, target.getLocation());
            if (dist < PULL_RANGE) {
                
                // Damage Falloff
                float power = 1f - (dist / PULL_RANGE);
                if (dist < KILL_RADIUS * 2f) power = 1f; 

                // Apply Damage
                if (target.getOwner() != source.getOwner()) {
                    engine.applyDamage(target, target.getLocation(), BURST_DAMAGE * power, DamageType.HIGH_EXPLOSIVE, BURST_EMP * power, true, false, source);
                }

                // Apply KNOCKBACK
                Vector2f.sub(target.getLocation(), targetLocation, vTmp);
                vTmp.normalise();
                vTmp.scale(BURST_FORCE * power);
                
                // Mass Dampener
                float massDampen = 1000f / (target.getMass() + 100f);
                if (massDampen > 1f) massDampen = 1f;
                vTmp.scale(massDampen);

                Vector2f.add(target.getVelocity(), vTmp, target.getVelocity());
                
                if (dist < 1000f) {
                    target.getEngineController().forceFlameout(true);
                }
            }
        }
    }

    // --- PHYSICS (PULL) ---
    private void applySingularityPhysics(CombatEngineAPI engine, ShipAPI ship, float amount) {
        List<ShipAPI> targets = engine.getShips();
        
        for (ShipAPI target : targets) {
            if (target == ship) continue; 
            if (target.getOwner() == ship.getOwner()) continue;
            if (target.isPhased()) continue; 
            
            float dist = Misc.getDistance(targetLocation, target.getLocation());
            
            if (dist < PULL_RANGE) {
                Vector2f.sub(targetLocation, target.getLocation(), vTmp);
                vTmp.normalise();
                
                float strength = PULL_STRENGTH * (1f - (dist / PULL_RANGE));
                strength *= strength; 
                strength += 200f; 
                
                vTmp2.set(-vTmp.y, vTmp.x);
                vTmp2.scale(strength * 0.5f); 
                
                vTmp.scale(strength);
                Vector2f.add(vTmp, vTmp2, vTmp);
                vTmp.scale(amount);
                
                Vector2f.add(target.getVelocity(), vTmp, target.getVelocity());
                
                // Spin Dampener
                float spin = target.getAngularVelocity();
                if (spin > MAX_SPIN_SPEED) target.setAngularVelocity(MAX_SPIN_SPEED);
                else if (spin < -MAX_SPIN_SPEED) target.setAngularVelocity(-MAX_SPIN_SPEED);
                
                // Melt Logic
                if (dist < KILL_RADIUS) {
                    if (!target.isHulk()) {
                        engine.applyDamage(target, target.getLocation(), MELT_DPS * amount, DamageType.ENERGY, 0f, true, false, ship);
                        target.setJitter("singularity_melt", JITTER_COLOR, 1.0f, 3, 5f);
                    } else {
                         if (Math.random() > 0.5) {
                            setRandomPointInCircle(target.getLocation(), target.getCollisionRadius(), vSpawn);
                            Vector2f.sub(targetLocation, vSpawn, vTmp); 
                            vTmp.scale(2.0f); 
                            engine.addHitParticle(vSpawn, vTmp, 10f, 0.5f, 0.4f, Color.white);
                        }
                        float currentAlpha = target.getExtraAlphaMult();
                        target.setExtraAlphaMult(currentAlpha * 0.995f);
                        if (currentAlpha < 0.1f) {
                            engine.removeEntity(target);
                            engine.spawnExplosion(targetLocation, new Vector2f(0,0), VOID_COLOR, 150f, 1.0f);
                        }
                    }
                }
            }
        }
    }

    // --- HELPERS ---
    private void spawnRingParticles(CombatEngineAPI engine, Vector2f center, float radius, int count, float orbitSpeed, float suckSpeed, Color color, float sizeBase) {
        for (int i = 0; i < count; i++) {
            float r = radius + (float)Math.random() * 80f; 
            float theta = (float)Math.random() * 360f;
            float angleRad = (float)Math.toRadians(theta);
            
            vSpawn.set(center.x + (float)Math.cos(angleRad) * r, center.y + (float)Math.sin(angleRad) * r);
            
            float orbitX = -(float)Math.sin(angleRad) * orbitSpeed;
            float orbitY = (float)Math.cos(angleRad) * orbitSpeed;
            
            Vector2f.sub(center, vSpawn, vTmp);
            vTmp.normalise();
            vTmp.scale(suckSpeed);
            vTmp.x += orbitX;
            vTmp.y += orbitY;
            
            float pSize = sizeBase + (float)Math.random() * 30f;
            engine.addHitParticle(vSpawn, vTmp, pSize, 0.6f, 0.4f + (float)Math.random()*0.4f, color);
        }
    }

    private void setRandomPointInCircle(Vector2f center, float radius, Vector2f dest) {
        float r = radius * (float)Math.sqrt(Math.random());
        float theta = (float)Math.random() * 360f;
        float angleRad = (float)Math.toRadians(theta);
        dest.set(center.x + (float)Math.cos(angleRad) * r, center.y + (float)Math.sin(angleRad) * r);
    }

    public void unapply(MutableShipStatsAPI stats, String id) {
        targetLocation = null;
    }

    // --- FIX HERE: Check index == 0 ---
    public StatusData getStatusData(int index, State state, float effectLevel) {
        if (index == 0) {
            if (state == State.IN) return new StatusData("CONJURING STORM", true);
            if (state == State.ACTIVE) return new StatusData("SINGULARITY ACTIVE", true);
            if (state == State.OUT) return new StatusData("CRITICAL COLLAPSE", true);
        }
        return null;
    }
}