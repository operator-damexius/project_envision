package data.scripts.world;

import java.awt.Color;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.impl.campaign.ids.Terrain;
import com.fs.starfarer.api.impl.campaign.procgen.NebulaEditor;
import com.fs.starfarer.api.impl.campaign.procgen.StarAge;
import com.fs.starfarer.api.impl.campaign.terrain.HyperspaceTerrainPlugin;
import com.fs.starfarer.api.util.Misc;

public class Vailara implements SectorGeneratorPlugin {

    public Vailara() {}

    @Override
    public void generate(SectorAPI sector) {
        try {
            // =================================================================
            // --- STAR SYSTEM ---
            // =================================================================
            StarSystemAPI system = sector.createStarSystem("Vailara");
            system.getLocation().set(29000, -13500);
            system.setBackgroundTextureFilename("graphics/backgrounds/background5.jpg");

            // =================================================================
            // --- NEBULA ---
            // =================================================================
            Misc.addNebulaFromPNG("data/campaign/terrain/vailara_nebula.png", 0, 0, system, "terrain", "nebula_blue", 4, 4, StarAge.YOUNG);

            // =================================================================
            // --- STAR (Vailara - The Diamond Heart) ---
            // =================================================================
            // Radius increased to 700f to visually anchor the massive ring system.
            PlanetAPI vailara_star = system.initStar("vailara", "star_white", 700f, 450f, 10f, 1.0f, 2.0f);
            
            // SYSTEM LIGHT: Pure, Sterile White
            // (255, 255, 255) ensures ships look metallic and sharp, perfect for a high-tech system.
            system.setLightColor(new Color(255, 255, 255));
            
            // STABILITY FIX:
            system.setCenter(vailara_star); 

            // STAR VISUALS:
            // 1. Surface: Absolute White
            // Represents the blinding intensity of a stable A-class star.
            vailara_star.getSpec().setPlanetColor(new Color(255, 255, 255, 255));
            
            // 2. Corona (Glow): "Ice White"
            // We use a tiny hint of blue (220, 240, 255) to keep it from looking gray.
            // "banded" texture makes it look like a spinning engine of light.
            vailara_star.getSpec().setGlowTexture(Global.getSettings().getSpriteName("hab_glows", "banded"));
            vailara_star.getSpec().setGlowColor(new Color(220, 240, 255, 200));
            vailara_star.getSpec().setUseReverseLightForGlow(true);
            vailara_star.setCustomDescriptionId("vailara_system");
            
            vailara_star.applySpecChanges();

            // =================================================================
            // --- THE 40 RINGS (With Identifiers) ---
            // =================================================================
            Color colInner = new Color(255, 255, 255, 180); 
            Color colMid = new Color(220, 230, 255, 140);
            Color colOuter = new Color(180, 240, 255, 120);
            Color colFringe = new Color(200, 255, 255, 80);

            // -----------------------------------------------------------------
            // CLUSTER A: "The Inner Lattice"
            // -----------------------------------------------------------------
            // Name applied to the first ring so it shows up on the map/hover.
            system.addRingBand(vailara_star, "misc", "rings_dust0", 256f, 0, colInner, 256f, 1100f, 40f, Terrain.RING, "The Inner Lattice");
            system.addRingBand(vailara_star, "misc", "rings_dust0", 128f, 1, colInner, 256f, 1200f, 42f, Terrain.RING, null);
            system.addRingBand(vailara_star, "misc", "rings_dust0", 256f, 0, colInner, 256f, 1300f, 44f, Terrain.RING, null);
            system.addRingBand(vailara_star, "misc", "rings_dust0", 128f, 1, colInner, 256f, 1400f, 46f, Terrain.RING, null);

            // -----------------------------------------------------------------
            // CLUSTER B: "The Great Disc"
            // -----------------------------------------------------------------
            system.addRingBand(vailara_star, "misc", "rings_ice0", 256f, 2, colMid, 256f, 2800f, 80f, Terrain.RING, "The Great Disc");
            system.addRingBand(vailara_star, "misc", "rings_dust0", 300f, 0, colMid, 256f, 2900f, 82f, Terrain.RING, null);
            system.addRingBand(vailara_star, "misc", "rings_ice0", 256f, 3, colMid, 256f, 3000f, 84f, Terrain.RING, null);
            system.addRingBand(vailara_star, "misc", "rings_dust0", 300f, 1, colMid, 256f, 3100f, 86f, Terrain.RING, null);
            system.addRingBand(vailara_star, "misc", "rings_ice0", 256f, 2, colMid, 256f, 3200f, 88f, Terrain.RING, null);
            system.addRingBand(vailara_star, "misc", "rings_dust0", 300f, 0, colMid, 256f, 3300f, 90f, Terrain.RING, null);
            system.addRingBand(vailara_star, "misc", "rings_ice0", 256f, 3, colMid, 256f, 3400f, 92f, Terrain.RING, null);
            system.addRingBand(vailara_star, "misc", "rings_dust0", 300f, 1, colMid, 256f, 3500f, 94f, Terrain.RING, null);
            system.addRingBand(vailara_star, "misc", "rings_ice0", 256f, 2, colMid, 256f, 3600f, 96f, Terrain.RING, null);
            system.addRingBand(vailara_star, "misc", "rings_dust0", 300f, 0, colMid, 256f, 3700f, 98f, Terrain.RING, null);
            system.addRingBand(vailara_star, "misc", "rings_ice0", 256f, 3, colMid, 256f, 3800f, 100f, Terrain.RING, null);
            system.addRingBand(vailara_star, "misc", "rings_dust0", 300f, 1, colMid, 256f, 3900f, 102f, Terrain.RING, null);

            // -----------------------------------------------------------------
            // CLUSTER C: "The Frost Veil"
            // -----------------------------------------------------------------
            system.addRingBand(vailara_star, "misc", "rings_ice0", 512f, 0, colOuter, 256f, 5200f, 140f, Terrain.RING, "The Frost Veil");
            system.addRingBand(vailara_star, "misc", "rings_ice0", 256f, 1, colOuter, 256f, 5300f, 145f, Terrain.RING, null);
            system.addRingBand(vailara_star, "misc", "rings_ice0", 512f, 2, colOuter, 256f, 5400f, 150f, Terrain.RING, null);
            system.addRingBand(vailara_star, "misc", "rings_ice0", 256f, 3, colOuter, 256f, 5500f, 155f, Terrain.RING, null);
            system.addRingBand(vailara_star, "misc", "rings_ice0", 512f, 0, colOuter, 256f, 5600f, 160f, Terrain.RING, null);
            system.addRingBand(vailara_star, "misc", "rings_ice0", 256f, 1, colOuter, 256f, 5700f, 165f, Terrain.RING, null);
            system.addRingBand(vailara_star, "misc", "rings_ice0", 512f, 2, colOuter, 256f, 5800f, 170f, Terrain.RING, null);
            system.addRingBand(vailara_star, "misc", "rings_ice0", 256f, 3, colOuter, 256f, 5900f, 175f, Terrain.RING, null);
            system.addRingBand(vailara_star, "misc", "rings_ice0", 512f, 0, colOuter, 256f, 6000f, 180f, Terrain.RING, null);
            system.addRingBand(vailara_star, "misc", "rings_ice0", 256f, 1, colOuter, 256f, 6100f, 185f, Terrain.RING, null);

            // -----------------------------------------------------------------
            // CLUSTER D: "The Outer Halo"
            // -----------------------------------------------------------------
            // Loop for the Fringe rings. The first iteration gets the name.
            boolean isFirst = true;
            for (float r = 14000f; r <= 15950f; r += 150f) {
                // If it's the first ring (14000), give it the name. Otherwise null.
                String ringName = isFirst ? "The Outer Halo" : null;
                system.addRingBand(vailara_star, "misc", "rings_ice0", 400f, 0, colFringe, 256f, r, 300f, Terrain.RING, ringName);
                isFirst = false;
            }

            // =================================================================
            // --- STATION : NYXARA PRIME ---
            // =================================================================
            SectorEntityToken station0 = system.addCustomEntity("nyxara", "Nyxara Prime Station", "station_hightech3", "solvaris");
            station0.setCircularOrbitPointingDown(vailara_star, 110, 2000, 110);
            station0.setCustomDescriptionId("station_nyxara");

            MarketAPI station0a = Global.getFactory().createMarket("nyxara_market", station0.getName(), 7);
            station0a.setFactionId("solvaris");
            station0a.setPrimaryEntity(station0);
            station0a.addCondition(Conditions.POPULATION_7);
            station0a.addCondition(Conditions.OUTPOST);
            station0a.addIndustry(Industries.POPULATION);
            station0a.addIndustry(Industries.MEGAPORT);
            station0a.addIndustry(Industries.STARFORTRESS_HIGH);
            station0a.addIndustry(Industries.HIGHCOMMAND);
            station0a.addSubmarket(Submarkets.SUBMARKET_OPEN);
            station0a.addSubmarket(Submarkets.SUBMARKET_STORAGE);
            station0a.addSubmarket(Submarkets.SUBMARKET_BLACK);
            station0.setMarket(station0a);
            sector.getEconomy().addMarket(station0a, true);

            // =================================================================
            // --- PLANET: ZYPHIR (The Frozen Tomb) ---
            // =================================================================
            PlanetAPI planet0 = system.addPlanet("zyphir", vailara_star, "Zyphir", "desert", 20, 180, 7300, 340);
            
            // SURFACE: Deep Rust / Oxidized Iron
            // Darker red absorbs the bright white starlight, creating high contrast.
            planet0.getSpec().setPlanetColor(new Color(180, 60, 40, 255));
            
            // ATMOSPHERE: Thin, Icy Blue Haze
            // Represents the "Thin Atmosphere" and "Cold" conditions.
            planet0.getSpec().setAtmosphereColor(new Color(150, 180, 255, 80));
            planet0.getSpec().setCloudColor(new Color(255, 255, 255, 100)); // Wispy ice clouds
            planet0.getSpec().setAtmosphereThickness(0.3f); // Very thin
            
            // GLOW: Faint Frost Reflection
            // Instead of a hot glow, this represents sunlight glinting off ice/ruins.
            planet0.getSpec().setGlowTexture(Global.getSettings().getSpriteName("hab_glows", "banded"));
            planet0.getSpec().setGlowColor(new Color(255, 255, 255, 40)); 
            planet0.getSpec().setUseReverseLightForGlow(true);
            
            planet0.applySpecChanges();
            
            planet0.setCustomDescriptionId("planet_zyphir");
            
            Misc.initConditionMarket(planet0);
            MarketAPI planet0a = planet0.getMarket();
            planet0a.setSurveyLevel(MarketAPI.SurveyLevel.FULL);
            planet0a.addCondition(Conditions.THIN_ATMOSPHERE);
            planet0a.addCondition(Conditions.COLD);
            planet0a.addCondition(Conditions.ORE_RICH);
            planet0a.addCondition(Conditions.RUINS_VAST);
            planet0a.addCondition(Conditions.FARMLAND_POOR);

            // =================================================================
            // --- PLANET: ELDARA (The Opalescent Giant) ---
            // =================================================================
            PlanetAPI eldara = system.addPlanet("eldara", vailara_star, "Eldara", "ice_giant", 98, 400, 12000, 200);
            
            // SURFACE: Moonstone / Pale Pearl
            // Instead of dark blue, we use a very bright, pale cyan-white.
            // This reflects the "White Star" intensity and looks like a polished gem.
            eldara.getSpec().setPlanetColor(new Color(200, 240, 255, 255));
            
            // ATMOSPHERE: Iridescent Violet
            // This creates the "Opal" effect (Pink/Purple shifting on the edge).
            eldara.getSpec().setAtmosphereColor(new Color(180, 150, 255, 140));
            eldara.getSpec().setCloudColor(new Color(255, 255, 255, 200));
            eldara.getSpec().setAtmosphereThickness(0.5f);
            
            // GLOW: Mystical Indigo Aurora
            eldara.getSpec().setGlowTexture(Global.getSettings().getSpriteName("hab_glows", "aurorae"));
            eldara.getSpec().setGlowColor(new Color(100, 50, 255, 120));
            eldara.getSpec().setUseReverseLightForGlow(true);
            
            eldara.applySpecChanges();
            eldara.setCustomDescriptionId("planet_eldara");
            
            // =================================================================
            // --- THE ELDARA HALO (Special Ring) ---
            // =================================================================
            // "rings_special0" creates a solid, high-tech looking band.
            // We color it "Cyan Electric" to contrast the pale planet.
            system.addRingBand(eldara, "misc", "rings_special0", 256f, 2, new Color(0, 255, 255, 200), 256f, 750, 128f, Terrain.RING, "The Eldara Halo");
            
            Misc.initConditionMarket(eldara);
            eldara.getMarket().setSurveyLevel(MarketAPI.SurveyLevel.FULL);
            // Conditions for a "Special" giant
            eldara.getMarket().addCondition(Conditions.DENSE_ATMOSPHERE);
            eldara.getMarket().addCondition(Conditions.HIGH_GRAVITY);
            eldara.getMarket().addCondition(Conditions.VOLATILES_ABUNDANT); // "Abundant" fits a special resource giant
            eldara.getMarket().addCondition(Conditions.EXTREME_WEATHER);

            // =================================================================
            // --- STATION : ORVION FORTRESS ---
            // =================================================================
            SectorEntityToken station1 = system.addCustomEntity("orvion", "Orvion Fortress", "station_hightech3", "solvaris");
            station1.setCircularOrbitPointingDown(eldara, 90, 1050, 30);
            station1.setCustomDescriptionId("station_orvion");

            MarketAPI station1a = Global.getFactory().createMarket("orvion_market", "Orvion Fortress", 8);
            station1a.setFactionId("solvaris");
            station1a.setPrimaryEntity(station1);
            station1a.addCondition(Conditions.POPULATION_8);
            station1a.addCondition(Conditions.OUTPOST);
            station1a.addIndustry(Industries.POPULATION);
            station1a.addIndustry(Industries.MEGAPORT);
            station1a.addIndustry(Industries.STARFORTRESS_HIGH);
            station1a.addIndustry(Industries.FUELPROD);
            station1a.addSubmarket(Submarkets.SUBMARKET_OPEN);
            station1a.addSubmarket(Submarkets.SUBMARKET_STORAGE);
            station1a.addSubmarket(Submarkets.GENERIC_MILITARY);
            station1.setMarket(station1a);
            sector.getEconomy().addMarket(station1a, true);

            // =================================================================
            // --- LOGISTICS & JUMP POINTS ---
            // =================================================================
            SectorEntityToken vailaraComm = system.addCustomEntity("vailara_comm", "Vailara Comm Relay", "comm_relay", "solvaris");
            vailaraComm.setCircularOrbit(vailara_star, 180, 2600, 110);
            
            SectorEntityToken vailaraNav = system.addCustomEntity("vailara_nav", "Vailara Nav Buoy", "nav_buoy", "solvaris");
            vailaraNav.setCircularOrbit(vailara_star, 45, 7000, 340);
            
            SectorEntityToken vailaraSensor = system.addCustomEntity("vailara_sensor", "Vailara Sensor Array", "sensor_array", "solvaris");
            vailaraSensor.setCircularOrbit(vailara_star, 270, 4500, 200);
            
            SectorEntityToken vailaraCommOuter = system.addCustomEntity("vailara_comm_outer", "Eldara Comm Relay", "comm_relay", "solvaris");
            vailaraCommOuter.setCircularOrbit(eldara, 180, 1500, 30);
            
            SectorEntityToken vailaraNavOuter = system.addCustomEntity("vailara_nav_outer", "Mining Fringe Nav Buoy", "nav_buoy", "solvaris");
            vailaraNavOuter.setCircularOrbit(vailara_star, 220, 9800, 150);

            system.autogenerateHyperspaceJumpPoints(true, true);
            

            cleanup(system);

        } catch (Exception e) {
            Global.getLogger(Vailara.class).error("Error generating Vailara: ", e);
        }
    }

    void cleanup(StarSystemAPI system) {
        try {
            HyperspaceTerrainPlugin plugin = (HyperspaceTerrainPlugin) Misc.getHyperspaceTerrain().getPlugin();
            NebulaEditor editor = new NebulaEditor(plugin);
            float x = system.getLocation().x;
            float y = system.getLocation().y;
            // Clears arc up to 1700 to cover the 1595 ring band
            editor.clearArc(x, y, 0, 1700f, 0, 360f, 0.25f);
            editor.clearArc(x, y, 0, 1500f, 0, 360f, 0.0f);
        } catch (Exception e) {}
    }
}