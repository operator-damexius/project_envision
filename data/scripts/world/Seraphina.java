package data.scripts.world;

import java.awt.Color;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.JumpPointAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.SectorGeneratorPlugin; 
import com.fs.starfarer.api.campaign.SpecialItemData; 
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Items;      
import com.fs.starfarer.api.impl.campaign.ids.Submarkets; 
import com.fs.starfarer.api.impl.campaign.ids.Terrain;
import com.fs.starfarer.api.impl.campaign.procgen.NebulaEditor;
import com.fs.starfarer.api.impl.campaign.terrain.BaseTiledTerrain;
import com.fs.starfarer.api.impl.campaign.terrain.HyperspaceTerrainPlugin;
import com.fs.starfarer.api.impl.campaign.terrain.MagneticFieldTerrainPlugin.MagneticFieldParams;
import com.fs.starfarer.api.util.Misc;

public class Seraphina implements SectorGeneratorPlugin {

    public Seraphina() {}

    @Override
    public void generate(SectorAPI sector) {
        try {
            StarSystemAPI system = sector.createStarSystem("Seraphina");
            system.getLocation().set(31500, -14500); 
            system.setBackgroundTextureFilename("graphics/backgrounds/background5.jpg");

            // --- STAR ---
            PlanetAPI seraphina_star = system.initStar("seraphina", "star_blue_giant", 1200f, 1500f, 15f, 1.0f, 5.0f);
            system.setLightColor(new Color(200, 220, 255)); 

            // --- DYSON SWARM (High Velocity & Argent Owned) ---
            
            // Layer 1: Inner Shell (3 Days)
            for (int i = 0; i < 18; i++) {
                float angle = i * 20f;
                SectorEntityToken shade = system.addCustomEntity("dyson_alpha_" + i, "Dyson Shell Alpha " + (i+1), "stellar_shade", "argent");
                shade.setCircularOrbitPointingDown(seraphina_star, angle, 2200, 3); 
            }

            // Layer 2: Main Shell (6 Days)
            for (int i = 0; i < 24; i++) {
                float angle = i * 15f + 15f; 
                SectorEntityToken shade = system.addCustomEntity("dyson_beta_" + i, "Dyson Shell Beta " + (i+1), "stellar_shade", "argent");
                shade.setCircularOrbitPointingDown(seraphina_star, angle, 2400, 6); 
            }

            // Layer 3: Outer Shell (9 Days)
            for (int i = 0; i < 30; i++) {
                float angle = i * 12f + 7f; 
                SectorEntityToken shade = system.addCustomEntity("dyson_gamma_" + i, "Dyson Shell Gamma " + (i+1), "stellar_shade", "argent");
                shade.setCircularOrbitPointingDown(seraphina_star, angle, 2600, 9); 
            }

            // --- RINGS ---
            system.addRingBand(seraphina_star, "misc", "rings_dust0", 256f, 0, new Color(180, 170, 160), 256f, 1800f, 80f, Terrain.RING, "Inner Scorch");
            system.addAsteroidBelt(seraphina_star, 250, 3000, 400, 30, 50, Terrain.ASTEROID_BELT, "Seraphina's Belt");
            system.addRingBand(seraphina_star, "misc", "rings_dust0", 256f, 1, new Color(160, 160, 180), 256f, 3800f, 100f, Terrain.RING, "The Grime Belt");
            system.addRingBand(seraphina_star, "misc", "rings_dust0", 256f, 3, new Color(100, 100, 150), 256f, 4600f, 120f, null, null);
            system.addRingBand(seraphina_star, "misc", "rings_dust0", 256f, 2, new Color(190, 190, 210), 256f, 5400f, 140f, null, null);
            system.addRingBand(seraphina_star, "misc", "rings_dust0", 256f, 0, new Color(140, 140, 180), 256f, 7500f, 160f, Terrain.RING, "Mid-System Dust");
            system.addRingBand(seraphina_star, "misc", "rings_ice0", 256f, 3, new Color(210, 230, 255), 256f, 10500f, 200f, Terrain.RING, "Inner Frost Barrier");
            system.addRingBand(seraphina_star, "misc", "rings_dust0", 256f, 1, new Color(120, 120, 160), 256f, 12000f, 220f, null, null);
            system.addRingBand(seraphina_star, "misc", "rings_ice0", 256f, 0, Color.white, 256f, 16000f, 260f, Terrain.RING, "The Great Halo");
            system.addRingBand(seraphina_star, "misc", "rings_dust0", 256f, 2, new Color(100, 100, 130), 256f, 22000f, 300f, Terrain.RING, "Outer Reach");

            // --- NEBULA ---
            SectorEntityToken nebula = Misc.addNebulaFromPNG("data/campaign/terrain/seraphina_nebula.png", 0, 0, system, "terrain", "nebula_blue", 4, 4, null);
            nebula.getLocation().set(0, 0);
            nebula.setCircularOrbit(seraphina_star, 0, 0, 100);
            try {
                NebulaEditor editor = new NebulaEditor((BaseTiledTerrain)nebula.getCustomPlugin());
                editor.clearArc(0, 0, 0, 19000f, 0, 360f); 
                editor.regenNoise(); 
            } catch (Exception e) {}

            // --- ENTITIES ---
            system.addCustomEntity("seraphina_tap", "Coronal Hyperspace Shunt", "coronal_tap", null).setCircularOrbitPointingDown(seraphina_star, 45, 2000, 90);
            system.addCustomEntity("seraphina_relay", "Seraphina Relay", "comm_relay", "argent").setCircularOrbitPointingDown(seraphina_star, 180, 6000, 200);
            system.addCustomEntity("seraphina_sensor","Seraphina Sensor Array","sensor_array","argent").setCircularOrbitPointingDown(seraphina_star, 120, 9000, 300);
            system.addCustomEntity("seraphina_nav","Seraphina Nav Buoy","nav_buoy","argent").setCircularOrbitPointingDown(seraphina_star, 240, 9000, 300);

            // --- ARGENT STATION ---
            SectorEntityToken argentStation = system.addCustomEntity("argent_station", "Argent Station", "station_hightech2", "argent");
            argentStation.setCircularOrbitPointingDown(seraphina_star, 270, 3400, 120);
            argentStation.setInteractionImage("illustrations", "orbital_construction");
            argentStation.setCustomDescriptionId("station_seraphina"); 
            
            // Station Market
            MarketAPI market = Global.getFactory().createMarket("argent_station_market", argentStation.getName(), 10);
            market.setFactionId("argent");
            market.setPrimaryEntity(argentStation);
            market.addCondition(Conditions.POPULATION_10);
            market.addCondition(Conditions.REGIONAL_CAPITAL); 
            market.addCondition(Conditions.OUTPOST); 
            
            // Industries
            market.addIndustry(Industries.POPULATION);
            market.addIndustry(Industries.MEGAPORT);
            market.getIndustry(Industries.MEGAPORT).setSpecialItem(new SpecialItemData(Items.FULLERENE_SPOOL, null));
            market.addIndustry(Industries.HEAVYBATTERIES);
            market.getIndustry(Industries.HEAVYBATTERIES).setSpecialItem(new SpecialItemData(Items.DRONE_REPLICATOR, null));
            market.addIndustry(Industries.HIGHCOMMAND);
            market.addIndustry(Industries.WAYSTATION);
            market.addIndustry(Industries.REFINING);
            market.getIndustry(Industries.REFINING).setSpecialItem(new SpecialItemData(Items.CATALYTIC_CORE, null));
            market.addIndustry("solvaris_uplink");
            market.addIndustry(Industries.STARFORTRESS_HIGH);
            market.addIndustry(Industries.ORBITALWORKS);
            market.getIndustry(Industries.ORBITALWORKS).setSpecialItem(new SpecialItemData(Items.PRISTINE_NANOFORGE, null));
            market.addIndustry(Industries.FUELPROD);
            market.getIndustry(Industries.FUELPROD).setSpecialItem(new SpecialItemData(Items.SYNCHROTRON, null));
            
            // --- NEW DYSON HUB ---
            // This is the Custom Industry we created
            market.addIndustry("dyson_hub"); 
            // ---------------------

            market.addSubmarket(Submarkets.SUBMARKET_OPEN);
            market.addSubmarket(Submarkets.SUBMARKET_STORAGE);

            market.setSurveyLevel(MarketAPI.SurveyLevel.FULL);
            argentStation.setMarket(market);
            sector.getEconomy().addMarket(market, true);

            // --- PLANET NITRU ---
            PlanetAPI planet0 = system.addPlanet("nitru", seraphina_star, "Nitru", "terran", 90, 150, 14000, 500);
            planet0.getSpec().setGlowTexture(Global.getSettings().getSpriteName("hab_glows", "banded"));
            planet0.getSpec().setGlowColor(new Color(100, 255, 200, 100));
            planet0.getSpec().setUseReverseLightForGlow(true);
            planet0.applySpecChanges();
            system.addRingBand(planet0, "misc", "rings_dust0", 256f, 2, new Color(100, 200, 255, 200), 256f, 1200f, 30f, Terrain.RING, "Nitru Ring");
            
            // Nitru Market
            MarketAPI nitruMarket = Global.getFactory().createMarket("nitru_market", planet0.getName(), 8);
            nitruMarket.setFactionId("argent");
            nitruMarket.setPrimaryEntity(planet0);
            
            // Conditions
            nitruMarket.addCondition(Conditions.POPULATION_8);
            nitruMarket.addCondition(Conditions.OUTPOST);
            nitruMarket.addCondition(Conditions.HABITABLE);
            nitruMarket.addCondition(Conditions.MILD_CLIMATE);
            nitruMarket.addCondition(Conditions.FARMLAND_BOUNTIFUL);
            nitruMarket.addCondition(Conditions.ORGANICS_PLENTIFUL);
            nitruMarket.addCondition(Conditions.ORE_ULTRARICH);
            nitruMarket.addCondition(Conditions.RUINS_WIDESPREAD);
            nitruMarket.addCondition("piracy_respite");

            // Industries
            nitruMarket.addIndustry(Industries.POPULATION);
            nitruMarket.addIndustry(Industries.MEGAPORT);
            nitruMarket.getIndustry(Industries.MEGAPORT).setSpecialItem(new SpecialItemData(Items.FULLERENE_SPOOL, null));
            nitruMarket.addIndustry(Industries.HEAVYBATTERIES);
            nitruMarket.getIndustry(Industries.HEAVYBATTERIES).setSpecialItem(new SpecialItemData(Items.DRONE_REPLICATOR, null));
            nitruMarket.addIndustry(Industries.STARFORTRESS_HIGH);
            nitruMarket.addIndustry(Industries.WAYSTATION);
            nitruMarket.addIndustry(Industries.ORBITALWORKS);
            nitruMarket.addIndustry("solvaris_uplink");
            nitruMarket.addIndustry(Industries.LIGHTINDUSTRY);
            nitruMarket.getIndustry(Industries.LIGHTINDUSTRY).setSpecialItem(new SpecialItemData(Items.BIOFACTORY_EMBRYO, null));
            nitruMarket.addIndustry(Industries.FARMING);
            nitruMarket.getIndustry(Industries.FARMING).setSpecialItem(new SpecialItemData(Items.SOIL_NANITES, null));
            nitruMarket.addIndustry(Industries.MINING);

            // --- NEW DYSON HUB ---
            // This is the Custom Industry we created
            nitruMarket.addIndustry("dyson_hub"); 
            // ---------------------
            
            // Submarkets
            nitruMarket.addSubmarket(Submarkets.SUBMARKET_OPEN);
            nitruMarket.addSubmarket(Submarkets.SUBMARKET_STORAGE);
            nitruMarket.addSubmarket(Submarkets.SUBMARKET_BLACK);

            nitruMarket.setSurveyLevel(MarketAPI.SurveyLevel.FULL);
            planet0.setMarket(nitruMarket);
            sector.getEconomy().addMarket(nitruMarket, true);

            // --- MOON AARIS ---
            PlanetAPI moon = system.addPlanet("aaris", planet0, "Aaris", "barren", 45, 40, 800, 30);
            Misc.initConditionMarket(moon);
            moon.getMarket().addCondition(Conditions.NO_ATMOSPHERE);
            moon.getMarket().addCondition(Conditions.ORE_RICH);

            // --- GAS GIANT AZURA ---
            PlanetAPI giant = system.addPlanet("azura", seraphina_star, "Azura", "gas_giant", 220, 350, 18000, 1200);
            giant.getSpec().setPlanetColor(new Color(20, 50, 255, 255));      
            giant.getSpec().setAtmosphereColor(new Color(0, 200, 255, 140));  
            giant.getSpec().setGlowTexture(Global.getSettings().getSpriteName("hab_glows", "banded"));
            giant.getSpec().setGlowColor(new Color(50, 0, 255, 60));          
            giant.getSpec().setUseReverseLightForGlow(true);
            giant.applySpecChanges();
            Misc.initConditionMarket(giant);
            giant.getMarket().addCondition(Conditions.HIGH_GRAVITY);
            giant.getMarket().addCondition(Conditions.VOLATILES_ABUNDANT);
            
            system.addRingBand(giant, "misc", "rings_ice0", 256f, 0, new Color(100, 200, 255, 255), 256f, 800f, 40f, Terrain.RING, "Azura's Halo");
            system.addTerrain(Terrain.MAGNETIC_FIELD, new MagneticFieldParams(giant.getRadius()+200f, giant.getRadius()+600f, giant, giant.getRadius()+100f, giant.getRadius()+1500f, new Color(50, 0, 255, 40), 0.5f, new Color(50, 0, 200, 100), new Color(0, 200, 255, 150))).setCircularOrbit(giant, 0, 0, 100);

            system.addCustomEntity("seraphina_gate","Seraphina Gate","inactive_gate",null).setCircularOrbitPointingDown(seraphina_star, 300, 19500, 600);
            
            JumpPointAPI fringe = Global.getFactory().createJumpPoint("seraphina_fringe", "Seraphina Fringe");
            fringe.setCircularOrbit(giant, 90, giant.getRadius() + 800f, 45); 
            fringe.setStandardWormholeToHyperspaceVisual();
            system.addEntity(fringe);

            system.autogenerateHyperspaceJumpPoints(true, true);
            cleanup(system);
            
        } catch (Exception e) {
            Global.getLogger(Seraphina.class).error("CRITICAL FATAL ERROR IN SERAPHINA:", e);
        }
    }
    
    void cleanup(StarSystemAPI system) {
        try {
            HyperspaceTerrainPlugin plugin = (HyperspaceTerrainPlugin) Misc.getHyperspaceTerrain().getPlugin();
            NebulaEditor editor = new NebulaEditor(plugin);
            float x = system.getLocation().x;
            float y = system.getLocation().y;
            editor.clearArc(x, y, 0, 3500f, 0, 360f, 0.25f);
            editor.clearArc(x, y, 0, 4000f, 0, 360f, 0.0f);
        } catch (Exception e) {}
    }
}