package data.scripts.world;

import java.awt.Color;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.JumpPointAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.SectorGeneratorPlugin; 
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Entities;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Terrain;
import com.fs.starfarer.api.impl.campaign.procgen.NebulaEditor;
import com.fs.starfarer.api.impl.campaign.terrain.HyperspaceTerrainPlugin;
import com.fs.starfarer.api.impl.campaign.terrain.MagneticFieldTerrainPlugin.MagneticFieldParams;
import com.fs.starfarer.api.util.Misc;

public class Pyralis implements SectorGeneratorPlugin {

    public Pyralis() {}

    @Override
    public void generate(SectorAPI sector) {
        try {
            StarSystemAPI system = sector.createStarSystem("Pyralis");
            system.getLocation().set(32000, -10000); 
            system.setBackgroundTextureFilename("graphics/backgrounds/background3.jpg"); 

            // --- PRIMARY STAR ---
            PlanetAPI star = system.initStar("pyralis", "star_orange", 1100f, 600f);

            // --- 15 RINGS OF PYRALIS (Industrial/Heat Gradient) ---
            
            // [Zone 1: The Furnace - Molten/Hot]
            system.addRingBand(star, "misc", "rings_dust0", 256f, 0, new Color(255, 100, 20), 256f, 1600f, 40f, Terrain.RING, "Furnace Wall");
            system.addRingBand(star, "misc", "rings_dust0", 256f, 1, new Color(255, 140, 40), 256f, 2000f, 50f, Terrain.RING, "Molten Track");
            system.addRingBand(star, "misc", "rings_dust0", 256f, 0, new Color(230, 100, 60), 256f, 2400f, 60f, Terrain.RING, "Ember Band");
            system.addRingBand(star, "misc", "rings_dust0", 256f, 2, new Color(200, 80, 40), 256f, 2800f, 70f, Terrain.RING, "Scorched Inner");
            
            // [Zone 2: The Grinder Belt Area]
            // Belt Range: 3400 to 4200 (Center 3800, Width 800)
            system.addAsteroidBelt(star, 150, 3800f, 800f, 200, 300, Terrain.ASTEROID_BELT, "The Grinder");
            system.addRingBand(star, "misc", "rings_dust0", 256f, 3, new Color(180, 160, 150), 256f, 3800f, 100f, Terrain.RING, "Grinder Dust");

            // [Zone 3: The Smog - Industrial/Rust]
            // Starts at 4500 to leave a gap for "The Forge"
            system.addRingBand(star, "misc", "rings_dust0", 256f, 1, new Color(160, 120, 100), 256f, 4500f, 90f, Terrain.RING, "Rust Belt");
            system.addRingBand(star, "misc", "rings_dust0", 256f, 0, new Color(140, 110, 90), 256f, 5000f, 100f, Terrain.RING, "Heavy Smog");
            system.addRingBand(star, "misc", "rings_dust0", 256f, 2, new Color(120, 100, 80), 256f, 5500f, 110f, Terrain.RING, "Slag Stream");
            system.addRingBand(star, "misc", "rings_dust0", 256f, 3, new Color(100, 90, 80), 256f, 6000f, 120f, Terrain.RING, "Iron Haze");
            system.addRingBand(star, "misc", "rings_dust0", 256f, 1, new Color(90, 80, 70), 256f, 6600f, 130f, Terrain.RING, "Carbon Cloud");

            // [Zone 4: The Deadlands Area]
            // Belt Range: 7550 to 8050 (Center 7800, Width 500)
            system.addAsteroidBelt(star, 150, 7800f, 500f, 300, 256f, Terrain.ASTEROID_BELT, "The Deadlands");
            system.addRingBand(star, "misc", "rings_dust0", 256f, 0, new Color(150, 150, 150), 256f, 7800f, 140f, Terrain.RING, "Deadlands Debris");

            // [Zone 5: The Ash - Cold/Grey]
            system.addRingBand(star, "misc", "rings_dust0", 256f, 2, new Color(110, 110, 120), 256f, 8800f, 160f, Terrain.RING, "Titanis Wake");
            system.addRingBand(star, "misc", "rings_dust0", 256f, 3, new Color(90, 90, 100), 256f, 9600f, 180f, Terrain.RING, "Outer Ash");
            // Jump Point will be at 10000, Titanis at 12000
            
            system.addRingBand(star, "misc", "rings_dust0", 256f, 1, new Color(70, 70, 80), 256f, 14000f, 220f, Terrain.RING, "Rim Soot");
            system.addRingBand(star, "misc", "rings_dust0", 256f, 0, new Color(50, 50, 60), 256f, 15500f, 260f, Terrain.RING, "Void Edge");


            // --- PLANETS ---

            // Titanis (Gas Giant) - 12000 Dist
            PlanetAPI giant = system.addPlanet("titanis", star, "Titanis", "gas_giant", 0, 600f, 12000f, 4000f);
            giant.getSpec().setPlanetColor(new Color(255, 140, 50, 255)); 
            giant.getSpec().setAtmosphereColor(new Color(255, 160, 80, 150)); 
            giant.getSpec().setCloudColor(new Color(255, 100, 50, 200)); 
            giant.getSpec().setAtmosphereThickness(0.5f);
            giant.getSpec().setGlowTexture(Global.getSettings().getSpriteName("hab_glows", "aurorae"));
            giant.getSpec().setGlowColor(new Color(255, 60, 0, 60)); 
            giant.getSpec().setUseReverseLightForGlow(true);
            giant.applySpecChanges();
            
            // Titanis Magnetic Field (Reddish-Orange)
            SectorEntityToken magField = system.addTerrain(Terrain.MAGNETIC_FIELD, 
                new MagneticFieldParams(
                    giant.getRadius() + 200f, 
                    giant.getRadius() + 1500f, 
                    giant, 
                    giant.getRadius() + 100f, 
                    giant.getRadius() + 1500f, 
                    new Color(255, 80, 20, 50), 
                    0.5f, 
                    new Color(255, 120, 50, 150), 
                    new Color(200, 60, 20, 150)
                )
            );
            magField.setCircularOrbit(giant, 0, 0, 100);

            // Ignis (Moon)
            PlanetAPI moon = system.addPlanet("ignis", giant, "Ignis", "lava", 0, 150f, 2100f, 30f);
            moon.getSpec().setPlanetColor(new Color(255, 255, 255, 255)); 
            moon.getSpec().setGlowTexture(Global.getSettings().getSpriteName("hab_glows", "banded"));
            moon.getSpec().setGlowColor(new Color(255, 60, 0, 255)); 
            moon.getSpec().setUseReverseLightForGlow(true);
            moon.getSpec().setAtmosphereThickness(0.0f);
            moon.applySpecChanges();

            // Markets
            Misc.initConditionMarket(giant); 
            giant.getMarket().setSurveyLevel(MarketAPI.SurveyLevel.FULL);
            giant.getMarket().addCondition(Conditions.HIGH_GRAVITY);       
            giant.getMarket().addCondition(Conditions.EXTREME_WEATHER);    
            giant.getMarket().addCondition(Conditions.VOLATILES_PLENTIFUL);
            giant.getMarket().addCondition(Conditions.IRRADIATED);         

            Misc.initConditionMarket(moon);
            moon.getMarket().setSurveyLevel(MarketAPI.SurveyLevel.FULL);
            moon.getMarket().addCondition(Conditions.VERY_HOT);            
            moon.getMarket().addCondition(Conditions.TECTONIC_ACTIVITY);   
            moon.getMarket().addCondition(Conditions.NO_ATMOSPHERE);       
            moon.getMarket().addCondition(Conditions.RARE_ORE_RICH);       
            moon.getMarket().addCondition(Conditions.ORE_RICH);            

            // --- CUSTOM JUMP POINT ---
            // Distance 10000 - Sits safely between Ring 13 (9600) and Titanis (12000)
            JumpPointAPI jumpPoint = Global.getFactory().createJumpPoint("pyralis_jump_point", "Point Pyralis Jump");
            jumpPoint.setCircularOrbit(star, 220, 10000f, 600f);
            jumpPoint.setStandardWormholeToHyperspaceVisual();
            system.addEntity(jumpPoint);

            // --- ENTITIES (5 Stable Locations - Neutral) ---
            // Adjusted locations to prevent belt collisions!
            
            // 1. "The Anvil" - Inner Gap (Moved to 3100 to avoid Grinder Belt start at 3400)
            system.addCustomEntity(null, null, Entities.STABLE_LOCATION, Factions.NEUTRAL)
                  .setCircularOrbit(star, 90, 3100f, 200f);  
                  
            // 2. "The Forge" - Gap between Grinder and Smog
            // Grinder ends at 4200. Rust Belt starts at 4500. Center is 4350.
            // Changed from 4150 to 4350 to ensure safety.
            system.addCustomEntity(null, null, Entities.STABLE_LOCATION, Factions.NEUTRAL)
                  .setCircularOrbit(star, 210, 4350f, 250f); 

            // 3. "Mid-System Anchor" - Gap between Smog and Deadlands
            // Carbon Cloud at 6600. Deadlands start at ~7550.
            system.addCustomEntity(null, null, Entities.STABLE_LOCATION, Factions.NEUTRAL)
                  .setCircularOrbit(star, 270, 7200f, 400f); 

            // 4. "Titanis Approach" - Safe Zone near Jump Point
            system.addCustomEntity(null, null, Entities.STABLE_LOCATION, Factions.NEUTRAL)
                  .setCircularOrbit(star, 150, 11000f, 500f); 

            // 5. "Outer Watch" - Far Rim
            system.addCustomEntity(null, null, Entities.STABLE_LOCATION, Factions.NEUTRAL)
                  .setCircularOrbit(star, 45, 13000f, 600f);

            // --- GENERATE JUMP POINTS ---
            system.autogenerateHyperspaceJumpPoints(true, false); 
            cleanup(system);

        } catch (Exception e) {
            Global.getLogger(Pyralis.class).error("Error generating Pyralis system: ", e);
        }
    }

    void cleanup(StarSystemAPI system) {
        HyperspaceTerrainPlugin plugin = (HyperspaceTerrainPlugin) Misc.getHyperspaceTerrain().getPlugin();
        NebulaEditor editor = new NebulaEditor(plugin);
        float x = system.getLocation().x;
        float y = system.getLocation().y;
        
        editor.clearArc(x, y, 0, 3000f, 0, 360f, 0.25f);
        editor.clearArc(x, y, 0, 4000f, 0, 360f, 0.0f);
    }
}