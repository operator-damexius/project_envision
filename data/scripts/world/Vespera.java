package data.scripts.world;

import java.awt.Color;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.JumpPointAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.SectorGeneratorPlugin;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Terrain;
import com.fs.starfarer.api.impl.campaign.procgen.NebulaEditor;
import com.fs.starfarer.api.impl.campaign.terrain.HyperspaceTerrainPlugin;
import com.fs.starfarer.api.util.Misc;

public class Vespera implements SectorGeneratorPlugin {

    public Vespera() {}

    @Override
    public void generate(SectorAPI sector) {
        try {
            StarSystemAPI system = sector.createStarSystem("Vespera");
            system.setBackgroundTextureFilename("graphics/backgrounds/background05.jpg");

            PlanetAPI vespera_star = system.initStar("vespera", "star_yellow", 900f, 200f, 5f, 1.0f, 2.0f);
            
            system.setLightColor(new Color(255, 250, 240)); 
            
            system.setCenter(vespera_star);

            vespera_star.getSpec().setPlanetColor(new Color(255, 255, 245, 255));
            vespera_star.getSpec().setGlowTexture(Global.getSettings().getSpriteName("hab_glows", "banded"));
            vespera_star.getSpec().setGlowColor(new Color(255, 220, 150, 255));
            vespera_star.getSpec().setUseReverseLightForGlow(true);
            
            vespera_star.applySpecChanges();
            vespera_star.setCustomDescriptionId("vespera_system");

            system.addAsteroidBelt(vespera_star, 150, 6300, 256f, 300, 256f, Terrain.ASTEROID_BELT, null);
            system.addRingBand(vespera_star, "misc", "rings_ice0", 256f, 1, Color.BLUE, 256f, 6300, 512f, Terrain.RING, "Vespera Ring Belt");
            system.addRingBand(vespera_star, "misc", "rings_dust0", 256f, 0, new Color(100, 90, 80, 255), 512f, 23000f, 400f, Terrain.RING, "The Outer Barrier");

            PlanetAPI planet0 = system.addPlanet("blackrock", vespera_star, "Blackrock", "rocky_metallic", 177, 170, 4300, 365);
            
            planet0.getSpec().setPlanetColor(new Color(50, 50, 55, 255));
            planet0.getSpec().setGlowTexture(Global.getSettings().getSpriteName("hab_glows", "banded"));
            planet0.getSpec().setGlowColor(new Color(150, 40, 20, 100));
            planet0.getSpec().setUseReverseLightForGlow(true);
            
            planet0.applySpecChanges();
            
            planet0.setCustomDescriptionId("planet_blackrock");
            
            Misc.initConditionMarket(planet0);
            planet0.getMarket().addCondition(Conditions.ORE_ULTRARICH);
            planet0.getMarket().addCondition(Conditions.RARE_ORE_ULTRARICH);
            planet0.getMarket().addCondition(Conditions.NO_ATMOSPHERE);
            planet0.getMarket().addCondition(Conditions.VERY_HOT);

            PlanetAPI planet1 = system.addPlanet("nimoria", vespera_star, "Nimoria", "terran", 24, 130, 8000, 325);
            
            planet1.getSpec().setPlanetColor(new Color(255, 255, 255, 255));
            planet1.getSpec().setAtmosphereColor(new Color(100, 150, 255, 160));
            planet1.getSpec().setCloudColor(new Color(255, 255, 255, 200));
            planet1.getSpec().setAtmosphereThickness(0.5f);
            planet1.getSpec().setTilt(23.5f);
            planet1.getSpec().setGlowColor(new Color(0, 0, 0, 0)); 
            
            planet1.applySpecChanges();
            
            planet1.setCustomDescriptionId("planet_nimoria");
            
            Misc.initConditionMarket(planet1);
            planet1.getMarket().addCondition(Conditions.HABITABLE);
            planet1.getMarket().addCondition(Conditions.MILD_CLIMATE);
            planet1.getMarket().addCondition(Conditions.FARMLAND_BOUNTIFUL);
            planet1.getMarket().addCondition(Conditions.RUINS_SCATTERED);

            PlanetAPI planet1a = system.addPlanet("amaris", planet1, "Amaris", "barren2", 24, 50, 700, 31);
            
            planet1a.getSpec().setPlanetColor(new Color(255, 255, 255, 255));
            planet1a.getSpec().setGlowTexture(Global.getSettings().getSpriteName("hab_glows", "banded"));
            planet1a.getSpec().setGlowColor(new Color(255, 255, 255, 30)); 
            planet1a.getSpec().setUseReverseLightForGlow(true);
            
            planet1a.applySpecChanges();
            
            planet1a.setCustomDescriptionId("planet_amaris");
            
            Misc.initConditionMarket(planet1a);
            planet1a.getMarket().addCondition(Conditions.NO_ATMOSPHERE);
            planet1a.getMarket().addCondition(Conditions.ORE_RICH);
            planet1a.getMarket().addCondition(Conditions.LOW_GRAVITY);

            PlanetAPI planet2 = system.addPlanet("vesperis", vespera_star, "Vesperis", "water", 24, 290, 11000, 365);
            
            planet2.getSpec().setPlanetColor(new Color(0, 50, 150, 255));
            planet2.getSpec().setAtmosphereColor(new Color(130, 200, 255, 180));
            planet2.getSpec().setCloudColor(new Color(255, 255, 255, 200));
            planet2.getSpec().setAtmosphereThickness(0.6f);
            planet2.getSpec().setGlowTexture(Global.getSettings().getSpriteName("hab_glows", "banded"));
            planet2.getSpec().setGlowColor(new Color(0, 255, 200, 60)); 
            planet2.getSpec().setUseReverseLightForGlow(true);
            
            planet2.applySpecChanges();
            
            planet2.setCustomDescriptionId("planet_vesperis");
            
            system.addRingBand(planet2, "misc", "rings_ice0", 256f, 1, Color.BLUE, 256f, 1000, 256f, Terrain.RING, "Vesperis Ice Rings");
            
            Misc.initConditionMarket(planet2);
            planet2.getMarket().addCondition(Conditions.WATER_SURFACE);
            planet2.getMarket().addCondition(Conditions.HABITABLE);
            planet2.getMarket().addCondition(Conditions.VOLATILES_TRACE);
            planet2.getMarket().addCondition(Conditions.ORGANICS_ABUNDANT);

            PlanetAPI planet3 = system.addPlanet("aetheris", vespera_star, "Aetheris", "terran", 24, 220, 17000, 681);
            
            planet3.getSpec().setPlanetColor(new Color(255, 245, 230, 255));
            planet3.getSpec().setAtmosphereColor(new Color(255, 190, 50, 140));
            planet3.getSpec().setCloudColor(new Color(255, 240, 220, 200));
            planet3.getSpec().setAtmosphereThickness(0.8f);
            planet3.getSpec().setGlowTexture(Global.getSettings().getSpriteName("hab_glows", "aurorae"));
            planet3.getSpec().setGlowColor(new Color(50, 255, 150, 120)); 
            planet3.getSpec().setUseReverseLightForGlow(true);
            
            planet3.applySpecChanges();
            
            planet3.setCustomDescriptionId("planet_aetheris");
            
            Misc.initConditionMarket(planet3);
            planet3.getMarket().setSurveyLevel(MarketAPI.SurveyLevel.FULL);
            planet3.getMarket().addCondition(Conditions.HABITABLE);
            planet3.getMarket().addCondition(Conditions.HIGH_GRAVITY);
            planet3.getMarket().addCondition(Conditions.DENSE_ATMOSPHERE);
            planet3.getMarket().addCondition(Conditions.ORE_RICH);
            planet3.getMarket().addCondition(Conditions.FARMLAND_RICH);
            planet3.getMarket().addCondition(Conditions.ORGANICS_PLENTIFUL);
            planet3.getMarket().addCondition(Conditions.RUINS_WIDESPREAD);

            PlanetAPI planet3a = system.addPlanet("ferronox", planet3, "Ferronox", "desert", 24, 110, 1000, 31);
            
            planet3a.getSpec().setPlanetColor(new Color(255, 235, 215, 255));
            planet3a.getSpec().setAtmosphereColor(new Color(255, 240, 200, 100));
            planet3a.getSpec().setCloudColor(new Color(255, 255, 255, 150));
            planet3a.getSpec().setAtmosphereThickness(0.2f);
            planet3a.getSpec().setGlowColor(new Color(0, 0, 0, 0));
            
            planet3a.applySpecChanges();
            
            planet3a.setCustomDescriptionId("planet_ferronox");
            
            Misc.initConditionMarket(planet3a);
            planet3a.getMarket().setSurveyLevel(MarketAPI.SurveyLevel.FULL);
            planet3a.getMarket().addCondition(Conditions.HABITABLE);
            planet3a.getMarket().addCondition(Conditions.DESERT);
            planet3a.getMarket().addCondition(Conditions.LOW_GRAVITY);
            planet3a.getMarket().addCondition(Conditions.FARMLAND_POOR);
            planet3a.getMarket().addCondition(Conditions.ORGANICS_TRACE);
            planet3a.getMarket().addCondition(Conditions.HOT);
            planet3a.getMarket().addCondition(Conditions.RUINS_WIDESPREAD);

            PlanetAPI planet4 = system.addPlanet("cryon", vespera_star, "Cryon", "ice_giant", 24, 500, 20000, 1095);
            
            planet4.getSpec().setPlanetColor(new Color(150, 240, 255, 255));
            planet4.getSpec().setAtmosphereColor(new Color(100, 200, 255, 140));
            planet4.getSpec().setAtmosphereThickness(0.6f);
            planet4.getSpec().setGlowTexture(Global.getSettings().getSpriteName("hab_glows", "aurorae"));
            planet4.getSpec().setGlowColor(new Color(0, 255, 220, 100));
            planet4.getSpec().setUseReverseLightForGlow(true);
            
            planet4.applySpecChanges();
            planet4.setCustomDescriptionId("planet_cryon");
            
            system.addRingBand(planet4, "misc", "rings_dust0", 512f, 2, new Color(200, 240, 255, 100), 256f, 1100, 160f, Terrain.RING, "Cryon Rings");
            system.addRingBand(planet4, "misc", "rings_ice0", 512f, 2, Color.white, 256f, 1100, 170f, null, null);
            
            Misc.initConditionMarket(planet4);
            planet4.getMarket().setSurveyLevel(MarketAPI.SurveyLevel.FULL);
            planet4.getMarket().addCondition(Conditions.COLD);
            planet4.getMarket().addCondition(Conditions.EXTREME_WEATHER);
            planet4.getMarket().addCondition(Conditions.DENSE_ATMOSPHERE);
            planet4.getMarket().addCondition(Conditions.HIGH_GRAVITY);
            planet4.getMarket().addCondition(Conditions.VOLATILES_PLENTIFUL);

            JumpPointAPI jumpPoint = Global.getFactory().createJumpPoint("cryon_jump_point", "Cryon Point");
            jumpPoint.setCircularOrbit(planet4, 270, planet4.getRadius() + 1000f, 100);
            jumpPoint.setStandardWormholeToHyperspaceVisual();
            system.addEntity(jumpPoint);

            SectorEntityToken vesperaComm = system.addCustomEntity("vespera_comm", "Vesperis Comm Relay", "comm_relay", "solvaris");
            vesperaComm.setCircularOrbitPointingDown(planet2, 0, 780, 31);
            
            SectorEntityToken vesperaNav = system.addCustomEntity("vespera_nav", "Vespera Nav Buoy", "nav_buoy", "solvaris");
            vesperaNav.setCircularOrbit(vespera_star, 135, 6000, 200);
            
            SectorEntityToken vesperaSensor = system.addCustomEntity("vespera_sensor", "Vespera Sensor Array", "sensor_array", "solvaris");
            vesperaSensor.setCircularOrbit(vespera_star, 315, 12000, 400);

            SectorEntityToken vesperaGate = system.addCustomEntity("vespera_gate", "Solvaris Gate", "inactive_gate", null);
            
            vesperaGate.setCircularOrbitPointingDown(planet4, 45, 1500, 100);
            
            vesperaGate.setCustomDescriptionId("gate_vespera");

            system.autogenerateHyperspaceJumpPoints(true, true);

            cleanup(system);
            
        } catch (Exception e) {
            Global.getLogger(Vespera.class).error("Error generating Vespera: ", e);
        }
    }

    void cleanup(StarSystemAPI system) {
        try {
            HyperspaceTerrainPlugin plugin = (HyperspaceTerrainPlugin) Misc.getHyperspaceTerrain().getPlugin();
            NebulaEditor editor = new NebulaEditor(plugin);
            float x = system.getLocation().x;
            float y = system.getLocation().y;
            editor.clearArc(x, y, 0, 2200f, 0, 360f, 0.25f);
            editor.clearArc(x, y, 0, 2500f, 0, 360f, 0.0f);
        } catch (Exception e) {}
    }
}