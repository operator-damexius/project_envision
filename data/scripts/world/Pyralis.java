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
            system.setBackgroundTextureFilename("graphics/backgrounds/background02.jpg");

            PlanetAPI pyralis_star = system.initStar("pyralis", "star_orange", 1100f, 600f);
            system.setCenter(pyralis_star);

            system.addRingBand(pyralis_star, "misc", "rings_dust0", 256f, 0, new Color(255, 100, 20), 256f, 1600f, 40f, Terrain.RING, "Furnace Wall");
            system.addRingBand(pyralis_star, "misc", "rings_dust0", 256f, 1, new Color(255, 140, 40), 256f, 2000f, 50f, Terrain.RING, "Molten Track");
            system.addRingBand(pyralis_star, "misc", "rings_dust0", 256f, 0, new Color(230, 100, 60), 256f, 2400f, 60f, Terrain.RING, "Ember Band");
            system.addRingBand(pyralis_star, "misc", "rings_dust0", 256f, 2, new Color(200, 80, 40), 256f, 2800f, 70f, Terrain.RING, "Scorched Inner");
            
            system.addAsteroidBelt(pyralis_star, 150, 3800f, 150f, 200, 220, Terrain.ASTEROID_BELT, "The Grinder");
            system.addRingBand(pyralis_star, "misc", "rings_dust0", 256f, 3, new Color(180, 160, 150), 256f, 3800f, 100f, Terrain.RING, "Grinder Dust");

            system.addRingBand(pyralis_star, "misc", "rings_dust0", 256f, 1, new Color(160, 120, 100), 256f, 4500f, 90f, Terrain.RING, "Rust Belt");
            system.addRingBand(pyralis_star, "misc", "rings_dust0", 256f, 0, new Color(140, 110, 90), 256f, 5000f, 100f, Terrain.RING, "Heavy Smog");
            system.addRingBand(pyralis_star, "misc", "rings_dust0", 256f, 2, new Color(120, 100, 80), 256f, 5500f, 110f, Terrain.RING, "Slag Stream");
            system.addRingBand(pyralis_star, "misc", "rings_dust0", 256f, 3, new Color(100, 90, 80), 256f, 6000f, 120f, Terrain.RING, "Iron Haze");
            system.addRingBand(pyralis_star, "misc", "rings_dust0", 256f, 1, new Color(90, 80, 70), 256f, 6600f, 130f, Terrain.RING, "Carbon Cloud");

            system.addAsteroidBelt(pyralis_star, 150, 7800f, 150f, 300, 330, Terrain.ASTEROID_BELT, "The Deadlands");
            system.addRingBand(pyralis_star, "misc", "rings_dust0", 256f, 0, new Color(150, 150, 150), 256f, 7800f, 140f, Terrain.RING, "Deadlands Debris");

            system.addRingBand(pyralis_star, "misc", "rings_dust0", 256f, 2, new Color(110, 110, 120), 256f, 8800f, 160f, Terrain.RING, "Titanis Wake");
            system.addRingBand(pyralis_star, "misc", "rings_dust0", 256f, 3, new Color(90, 90, 100), 256f, 9600f, 180f, Terrain.RING, "Outer Ash");
            system.addRingBand(pyralis_star, "misc", "rings_dust0", 256f, 1, new Color(70, 70, 80), 256f, 16000f, 220f, Terrain.RING, "Rim Soot");
            system.addRingBand(pyralis_star, "misc", "rings_dust0", 256f, 1, new Color(70, 70, 80), 256f, 16200f, 220f, Terrain.RING, "Rim Soot");
            system.addRingBand(pyralis_star, "misc", "rings_dust0", 256f, 1, new Color(70, 70, 80), 256f, 16400f, 220f, Terrain.RING, "Rim Soot");
            system.addRingBand(pyralis_star, "misc", "rings_dust0", 256f, 0, new Color(50, 50, 60), 256f, 16600f, 260f, Terrain.RING, "Void Edge");

            PlanetAPI planet0 = system.addPlanet("titanis", pyralis_star, "Titanis", "gas_giant", 0, 600f, 13000f, 4000f);
            planet0.getSpec().setPlanetColor(new Color(255, 140, 50, 255));
            planet0.getSpec().setAtmosphereColor(new Color(255, 160, 80, 150));
            planet0.getSpec().setCloudColor(new Color(255, 100, 50, 200));
            planet0.getSpec().setAtmosphereThickness(0.5f);
            planet0.getSpec().setGlowTexture(Global.getSettings().getSpriteName("hab_glows", "aurorae"));
            planet0.getSpec().setGlowColor(new Color(255, 60, 0, 60));
            planet0.getSpec().setUseReverseLightForGlow(true);
            planet0.applySpecChanges();
            planet0.setCustomDescriptionId("planet_titanis");
            
            SectorEntityToken magField = system.addTerrain(Terrain.MAGNETIC_FIELD, 
                new MagneticFieldParams(
                    planet0.getRadius() + 200f, 
                    planet0.getRadius() + 1500f, 
                    planet0, 
                    planet0.getRadius() + 100f, 
                    planet0.getRadius() + 1500f, 
                    new Color(255, 80, 20, 50), 
                    0.5f, 
                    new Color(255, 120, 50, 150), 
                    new Color(200, 60, 20, 150)
                )
            );
            magField.setCircularOrbit(planet0, 0, 0, 100);

            Misc.initConditionMarket(planet0);
            planet0.getMarket().setSurveyLevel(MarketAPI.SurveyLevel.FULL);
            planet0.getMarket().addCondition(Conditions.HIGH_GRAVITY);      
            planet0.getMarket().addCondition(Conditions.EXTREME_WEATHER);   
            planet0.getMarket().addCondition(Conditions.VOLATILES_PLENTIFUL);
            planet0.getMarket().addCondition(Conditions.IRRADIATED);  

            PlanetAPI planet0a = system.addPlanet("ignis", planet0, "Ignis", "barren", 0, 150f, 1700f, 30f);
            planet0a.getSpec().setPlanetColor(new Color(60, 55, 65, 255));
            planet0a.getSpec().setGlowTexture(Global.getSettings().getSpriteName("hab_glows", "banded"));
            planet0a.getSpec().setGlowColor(new Color(255, 160, 40, 120));
            planet0a.getSpec().setUseReverseLightForGlow(true);
            planet0a.applySpecChanges();
            planet0a.setCustomDescriptionId("planet_ignis");

            Misc.initConditionMarket(planet0a);
            planet0a.getMarket().setSurveyLevel(MarketAPI.SurveyLevel.FULL);
            planet0a.getMarket().addCondition(Conditions.NO_ATMOSPHERE);      
            planet0a.getMarket().addCondition(Conditions.VOLATILES_TRACE); 
            planet0a.getMarket().addCondition(Conditions.ORE_RICH);        
            planet0a.getMarket().addCondition(Conditions.RARE_ORE_RICH);   
            planet0a.getMarket().addCondition(Conditions.TECTONIC_ACTIVITY);
            
            PlanetAPI planet1 = system.addPlanet("aridus", pyralis_star, "Aridus", "desert", 120, 220f, 18000f, 1800f);
            planet1.setCustomDescriptionId("planet_aridus");
            planet1.getSpec().setPlanetColor(new Color(180, 170, 160, 255));
            planet1.getSpec().setAtmosphereColor(new Color(200, 200, 210, 140));
            planet1.getSpec().setAtmosphereThickness(0.5f); 
            planet1.getSpec().setCloudColor(new Color(255, 255, 255, 100));
            planet1.getSpec().setTilt(15f);
            planet1.applySpecChanges();

            Misc.initConditionMarket(planet1);
            planet1.getMarket().setSurveyLevel(MarketAPI.SurveyLevel.FULL);
            planet1.getMarket().addCondition(Conditions.LOW_GRAVITY);
            planet1.getMarket().addCondition(Conditions.FARMLAND_BOUNTIFUL);
            planet1.getMarket().addCondition(Conditions.ORE_ABUNDANT);

            PlanetAPI planet1a = system.addPlanet("caloris", planet1, "Caloris", "barren", 0, 80f, 750f, 25f);
            planet1a.setCustomDescriptionId("planet_caloris");
            planet1a.getSpec().setPlanetColor(new Color(140, 130, 120, 255));
            planet1a.getSpec().setGlowTexture(Global.getSettings().getSpriteName("hab_glows", "banded"));
            planet1a.getSpec().setGlowColor(new Color(255, 200, 150, 40)); 
            planet1a.getSpec().setUseReverseLightForGlow(true);
            planet1a.getSpec().setAtmosphereThickness(0f);
            planet1a.applySpecChanges();
            
            Misc.initConditionMarket(planet1a);
            planet1a.getMarket().setSurveyLevel(MarketAPI.SurveyLevel.FULL);
            planet1a.getMarket().addCondition(Conditions.NO_ATMOSPHERE);
            planet1a.getMarket().addCondition(Conditions.ORE_RICH);
            planet1a.getMarket().addCondition(Conditions.RARE_ORE_RICH);

            JumpPointAPI jumpPoint = Global.getFactory().createJumpPoint("pyralis_jump_point", "Point Pyralis Jump");
            jumpPoint.setCircularOrbit(pyralis_star, 220, 10000f, 600f);
            jumpPoint.setStandardWormholeToHyperspaceVisual();
            system.addEntity(jumpPoint);

            SectorEntityToken pyralisStable1 = system.addCustomEntity(null, null, Entities.STABLE_LOCATION, Factions.NEUTRAL);
            pyralisStable1.setCircularOrbit(pyralis_star, 90, 3100f, 200f); 
                  
            SectorEntityToken pyralisStable2 = system.addCustomEntity(null, null, Entities.STABLE_LOCATION, Factions.NEUTRAL);
            pyralisStable2.setCircularOrbit(pyralis_star, 210, 4350f, 250f); 

            SectorEntityToken pyralisStable3 = system.addCustomEntity(null, null, Entities.STABLE_LOCATION, Factions.NEUTRAL);
            pyralisStable3.setCircularOrbit(pyralis_star, 270, 7200f, 400f); 

            SectorEntityToken pyralisStable4 = system.addCustomEntity(null, null, Entities.STABLE_LOCATION, Factions.NEUTRAL);
            pyralisStable4.setCircularOrbit(pyralis_star, 150, 11000f, 500f); 

            SectorEntityToken pyralisStable5 = system.addCustomEntity(null, null, Entities.STABLE_LOCATION, Factions.NEUTRAL);
            pyralisStable5.setCircularOrbit(pyralis_star, 45, 13000f, 600f);

            system.autogenerateHyperspaceJumpPoints(true, false);
            cleanup(system);

        } catch (Exception e) {
            Global.getLogger(Pyralis.class).error("Error generating Pyralis system: ", e);
        }
    }

    void cleanup(StarSystemAPI system) {
        try {
            HyperspaceTerrainPlugin plugin = (HyperspaceTerrainPlugin) Misc.getHyperspaceTerrain().getPlugin();
            NebulaEditor editor = new NebulaEditor(plugin);
            float x = system.getLocation().x;
            float y = system.getLocation().y;
            editor.clearArc(x, y, 0, 1400f, 0, 360f, 0.25f);
            editor.clearArc(x, y, 0, 1200f, 0, 360f, 0.0f);
        } catch (Exception e) {}
    }
}