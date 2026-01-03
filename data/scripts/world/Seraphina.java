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
            // =================================================================
            // --- STAR SYSTEM ---
            // =================================================================
            StarSystemAPI system = sector.createStarSystem("Seraphina");
            system.getLocation().set(31500, -14500);
            system.setBackgroundTextureFilename("graphics/backgrounds/background5.jpg");

            // =================================================================
            // --- STAR: SERAPHINA (The Blue Supergiant) ---
            // =================================================================
            // Type "star_blue_giant" handles the base texture.
            // Radius 1200f: This is a monster star.
            PlanetAPI seraphina_star = system.initStar("seraphina", "star_blue_giant", 1200f, 1500f, 15f, 1.0f, 5.0f);
            
            // SYSTEM LIGHT: Cold Electric Blue
            // (200, 230, 255) creates a harsh, high-energy lighting environment.
            // Shadows in this system will look sharp and cold.
            system.setLightColor(new Color(200, 230, 255));
            
            // STABILITY FIX
            system.setCenter(seraphina_star);

            // STAR VISUALS:
            // 1. Surface: Blinding White-Blue
            // Real O-Class stars are so hot they appear white with a slight blue tint.
            // Using pure blue here would look "cartoony" and low-energy.
            seraphina_star.getSpec().setPlanetColor(new Color(240, 245, 255, 255));
            
            // 2. Corona (Glow): Deep Electric Royal Blue
            // This creates the beautiful "Halo" effect. The core is white, but the edge is deep blue.
            // We use "corona_aka_main" (standard) or "banded" depending on preference. 
            // For a giant, "banded" suggests rapid rotation.
            seraphina_star.getSpec().setGlowTexture(Global.getSettings().getSpriteName("hab_glows", "banded"));
            seraphina_star.getSpec().setGlowColor(new Color(0, 80, 255, 200));
            seraphina_star.getSpec().setUseReverseLightForGlow(true);
            
            // 3. Solar Wind / Atmosphere
            // A star this big has a visible atmosphere of escaping gas.
            seraphina_star.getSpec().setAtmosphereThickness(0.5f);
            seraphina_star.getSpec().setAtmosphereColor(new Color(0, 150, 255, 150));
            
            seraphina_star.applySpecChanges();
            seraphina_star.setCustomDescriptionId("seraphina_system");

            // =================================================================
            // --- DYSON SWARM (High Velocity & Argent Owned) ---
            // =================================================================
            for (int i = 0; i < 18; i++) {
                float angle = i * 20f;
                SectorEntityToken shade = system.addCustomEntity("dyson_alpha_" + i, "Dyson Shell Alpha " + (i+1), "stellar_shade", "argent");
                shade.setCircularOrbitPointingDown(seraphina_star, angle, 2200, 3);
            }
            for (int i = 0; i < 24; i++) {
                float angle = i * 15f + 15f; 
                SectorEntityToken shade = system.addCustomEntity("dyson_beta_" + i, "Dyson Shell Beta " + (i+1), "stellar_shade", "argent");
                shade.setCircularOrbitPointingDown(seraphina_star, angle, 2400, 6);
            }
            for (int i = 0; i < 30; i++) {
                float angle = i * 12f + 7f; 
                SectorEntityToken shade = system.addCustomEntity("dyson_gamma_" + i, "Dyson Shell Gamma " + (i+1), "stellar_shade", "argent");
                shade.setCircularOrbitPointingDown(seraphina_star, angle, 2600, 9);
            }

            // =================================================================
            // --- RINGS: THE SERAPHINA DISC ---
            // =================================================================
            
            // 1. INNER SCORCH (1800f)
            // Changed from Beige to Dark Burnt Copper. 
            // This represents dust that is being roasted by the O-Class star.
            system.addRingBand(seraphina_star, "misc", "rings_dust0", 512f, 0, new Color(100, 60, 50, 255), 256f, 1800f, 80f, Terrain.RING, "Inner Scorch");

            // 2. THE GRIME BELT (4100f)
            // Changed to Slate Blue-Grey.
            // Under a blue star, grey dust appears slightly blueish (Cold Steel).
            system.addRingBand(seraphina_star, "misc", "rings_dust0", 600f, 1, new Color(110, 120, 140, 200), 256f, 4100f, 100f, Terrain.RING, "The Grime Belt");

            // 3. MID-SYSTEM TRANSITION (4700f - 5500f)
            // These connect the Grime to the Asteroids. 
            // We give them a metallic tint (150, 160, 180).
            system.addRingBand(seraphina_star, "misc", "rings_dust0", 512f, 3, new Color(130, 140, 160), 256f, 4700f, 120f, null, null);
            system.addRingBand(seraphina_star, "misc", "rings_dust0", 512f, 2, new Color(150, 160, 180), 256f, 5500f, 140f, null, null);

            // 4. SERAPHINA'S BELT (7500f - Asteroids)
            // The main rocky debris field.
            system.addAsteroidBelt(seraphina_star, 250, 7500, 800, 30, 50, Terrain.ASTEROID_BELT, "Seraphina's Belt");
            // Highlight ring for the asteroids (faint dusty blue)
            system.addRingBand(seraphina_star, "misc", "rings_dust0", 1000f, 0, new Color(140, 150, 190, 100), 256f, 7500f, 160f, Terrain.RING, "Mid-System Dust");

            // 5. INNER FROST BARRIER (10500f)
            // The "Snow Line". Ice begins here.
            // Brighter Cyan (180, 240, 255) to catch the blue starlight.
            system.addRingBand(seraphina_star, "misc", "rings_ice0", 800f, 3, new Color(180, 240, 255, 200), 256f, 10500f, 200f, Terrain.RING, "Inner Frost Barrier");

            // 6. THE GREAT HALO (16000f)
            // The massive outer shell.
            // Pure White with a hint of electric blue glow.
            system.addRingBand(seraphina_star, "misc", "rings_ice0", 2000f, 0, new Color(220, 245, 255, 150), 256f, 16000f, 260f, Terrain.RING, "The Great Halo");

            // =================================================================
            // --- NEBULA ---
            // =================================================================
            SectorEntityToken nebula = Misc.addNebulaFromPNG("data/campaign/terrain/seraphina_nebula.png", 0, 0, system, "terrain", "nebula_blue", 4, 4, null);
            nebula.setCircularOrbit(seraphina_star, 0, 0, 100);
            try {
                NebulaEditor editor = new NebulaEditor((BaseTiledTerrain)nebula.getCustomPlugin());
                editor.clearArc(0, 0, 0, 19000f, 0, 360f);
                editor.regenNoise();
            } catch (Exception e) {}

            // =================================================================
            // --- LOGISTICS TOKENS (COMM RELAY, NAV BUOY, SENSOR ARRAY) ---
            // =================================================================
            
            // TOKEN 1: CORONAL SHUNT (Near Star)
            SectorEntityToken seraphinaTap = system.addCustomEntity("seraphina_tap", "Coronal Hyperspace Shunt", "coronal_tap", null);
            seraphinaTap.setCircularOrbitPointingDown(seraphina_star, 45, 2000, 90);
            
            // TOKEN 2: MAIN COMM RELAY (Argent Owned)
            SectorEntityToken seraphinaComm = system.addCustomEntity("seraphina_relay", "Seraphina Relay", "comm_relay", "argent");
            seraphinaComm.setCircularOrbitPointingDown(seraphina_star, 180, 6000, 200);
            
            // TOKEN 3: SENSOR ARRAY (Mid-System Coverage)
            SectorEntityToken seraphinaSensor = system.addCustomEntity("seraphina_sensor", "Seraphina Sensor Array", "sensor_array", "argent");
            seraphinaSensor.setCircularOrbitPointingDown(seraphina_star, 120, 9000, 300);
            
            // TOKEN 4: NAV BUOY (Fleet Speed Boost)
            SectorEntityToken seraphinaNav = system.addCustomEntity("seraphina_nav", "Seraphina Nav Buoy", "nav_buoy", "argent");
            seraphinaNav.setCircularOrbitPointingDown(seraphina_star, 240, 9000, 300);

            // =================================================================
            // --- ARGENT STATION ---
            // =================================================================
            SectorEntityToken station0 = system.addCustomEntity("argent_station", "Argent Station", "station_hightech2", "argent");
            station0.setCircularOrbitPointingDown(seraphina_star, 270, 3400, 120);
            station0.setCustomDescriptionId("station_seraphina");
            
            MarketAPI market0 = Global.getFactory().createMarket("argent_station_market", station0.getName(), 10);
            market0.setFactionId("argent");
            market0.setPrimaryEntity(station0);
            market0.addCondition(Conditions.POPULATION_10);
            market0.addCondition(Conditions.REGIONAL_CAPITAL);
            market0.addCondition(Conditions.ESTABLISHED_POLITY);

            market0.addIndustry(Industries.POPULATION);
            market0.addIndustry(Industries.MEGAPORT);
            market0.getIndustry(Industries.MEGAPORT).setSpecialItem(new SpecialItemData(Items.FULLERENE_SPOOL, null));
            market0.addIndustry(Industries.HEAVYBATTERIES);
            market0.getIndustry(Industries.HEAVYBATTERIES).setSpecialItem(new SpecialItemData(Items.DRONE_REPLICATOR, null));
            market0.addIndustry(Industries.HIGHCOMMAND);
            market0.addIndustry(Industries.WAYSTATION);
            market0.addIndustry(Industries.REFINING);
            market0.getIndustry(Industries.REFINING).setSpecialItem(new SpecialItemData(Items.CATALYTIC_CORE, null));
            market0.addIndustry(Industries.STARFORTRESS_HIGH);
            market0.addIndustry(Industries.ORBITALWORKS);
            market0.getIndustry(Industries.ORBITALWORKS).setSpecialItem(new SpecialItemData(Items.PRISTINE_NANOFORGE, null));
            market0.addIndustry(Industries.FUELPROD);
            market0.getIndustry(Industries.FUELPROD).setSpecialItem(new SpecialItemData(Items.SYNCHROTRON, null));
            market0.addIndustry("solvaris_uplink");
            market0.addIndustry("dyson_hub"); 
            market0.addSubmarket(Submarkets.SUBMARKET_OPEN);
            market0.addSubmarket(Submarkets.SUBMARKET_STORAGE);
            station0.setMarket(market0);
            sector.getEconomy().addMarket(market0, true);

            // =================================================================
            // --- NITRU (The Crimson Capital) ---
            // =================================================================
            PlanetAPI planet0 = system.addPlanet("nitru", seraphina_star, "Nitru", "terran", 90, 160, 14000, 500);
            
            // SURFACE: Deep Burgundy / Crimson
            planet0.getSpec().setPlanetColor(new Color(180, 50, 70, 255));
            
            // ATMOSPHERE: Thick Ozone (Cyan-White)
            planet0.getSpec().setAtmosphereColor(new Color(150, 240, 255, 160));
            planet0.getSpec().setCloudColor(new Color(255, 255, 255, 200));
            planet0.getSpec().setAtmosphereThickness(0.65f); // Thicker air
            
            // GLOW: City Lights (Warm Gold)
            planet0.getSpec().setGlowTexture(Global.getSettings().getSpriteName("hab_glows", "banded"));
            planet0.getSpec().setGlowColor(new Color(255, 220, 150, 255)); 
            planet0.getSpec().setUseReverseLightForGlow(true);
            
            planet0.applySpecChanges();
            planet0.setCustomDescriptionId("planet_nitru");

            // =================================================================
            // --- NITRU ORBITAL SHADES (Inner Defense) ---
            // =================================================================
            // 6 Shades orbiting at 490 (Inside the Sapphire Veil)
            for (int i = 0; i < 6; i++) {
                float angle = i * 60f; // 360 / 6 = 60 degrees apart
                SectorEntityToken nitruShade = system.addCustomEntity("nitru_shade_" + i, "Nitru Orbital Shade", "stellar_shade", "argent");
                // Orbiting planet0, angle, radius 490, period 25 (Orbiting fast)
                nitruShade.setCircularOrbitPointingDown(planet0, angle, 490, 25);
            }

            // RING: "The Sapphire Veil" (Natural Ring)
            // Radius 600 (Outer side of the shades)
            system.addRingBand(planet0, "misc", "rings_ice0", 256f, 1, new Color(50, 150, 255, 160), 256f, 600f, 40f, Terrain.RING, "The Sapphire Veil");
            
            Misc.initConditionMarket(planet0);
            MarketAPI Market0a = planet0.getMarket();
            Market0a.setSurveyLevel(MarketAPI.SurveyLevel.FULL);
            // Conditions tailored to a High-Tech Capital
            Market0a.addCondition(Conditions.SOLAR_ARRAY); 
            Market0a.addCondition(Conditions.POPULATION_9);
            Market0a.addCondition(Conditions.HABITABLE);
            Market0a.addCondition(Conditions.MILD_CLIMATE); 
            Market0a.addCondition(Conditions.FARMLAND_BOUNTIFUL);
            Market0a.addCondition(Conditions.ORGANICS_PLENTIFUL);

            // =================================================================
            // --- MOON: AARIS (The Silver Anvil) ---
            // =================================================================
            PlanetAPI planet0a = system.addPlanet("aaris", planet0, "Aaris", "barren", 45, 40, 900, 30);
            
            // SURFACE: Platinum / High-Albedo Regolith
            // We use (220, 220, 225) - A very bright, slightly cool white-gray.
            // Under Seraphina's blue light, this will look like polished steel.
            planet0a.getSpec().setPlanetColor(new Color(220, 220, 225, 255)); 
            
            // GLOW: None (It is airless and cold)
            planet0a.getSpec().setGlowColor(new Color(0, 0, 0, 0)); 
            
            planet0a.applySpecChanges();
            planet0a.setCustomDescriptionId("planet_aaris");

            Misc.initConditionMarket(planet0a);
            planet0a.getMarket().setSurveyLevel(MarketAPI.SurveyLevel.FULL);
            // Conditions fit the "Industrial Mine" lore
            planet0a.getMarket().addCondition(Conditions.NO_ATMOSPHERE);
            planet0a.getMarket().addCondition(Conditions.LOW_GRAVITY); 
            planet0a.getMarket().addCondition(Conditions.ORE_RICH);      // Iron/Copper
            planet0a.getMarket().addCondition(Conditions.RARE_ORE_RICH); // Platinum/Titanium

            // =================================================================
            // --- PLANET: AZURA (The Violet Storm) ---
            // =================================================================
            PlanetAPI planet1 = system.addPlanet("azura", seraphina_star, "Azura", "gas_giant", 220, 350, 18000, 1200);
            
            // SURFACE: Deep Royal Indigo
            // We make the base darker (20, 30, 100) so the glowing effects stand out.
            planet1.getSpec().setPlanetColor(new Color(25, 35, 110, 255));
            
            // ATMOSPHERE: Electric Cyan
            // The upper clouds reflecting the harsh star light.
            planet1.getSpec().setAtmosphereColor(new Color(100, 200, 255, 140));
            planet1.getSpec().setAtmosphereThickness(0.6f);
            
            // GLOW: Auroral Violet
            // This represents the constant auroral storms caused by Seraphina's solar wind.
            planet1.getSpec().setGlowTexture(Global.getSettings().getSpriteName("hab_glows", "banded"));
            planet1.getSpec().setGlowColor(new Color(180, 50, 255, 120));
            planet1.getSpec().setUseReverseLightForGlow(true);
            
            planet1.applySpecChanges();
            planet1.setCustomDescriptionId("planet_azura");

            // =================================================================
            // --- AZURA'S HALO (Ring) ---
            // =================================================================
            // A clean, icy ring contrasting the dark planet.
            system.addRingBand(planet1, "misc", "rings_ice0", 256f, 0, new Color(150, 220, 255, 255), 256f, 800f, 40f, Terrain.RING, "Azura's Halo");

            // =================================================================
            // --- MAGNETIC FIELD (The Visual Showpiece) ---
            // =================================================================
            // We tune the colors to be "Electric Violet" to match the lore.
            // Inner Color: Deep Purple (50, 0, 150)
            // Outer Color: Bright Electric Blue (50, 200, 255)
            SectorEntityToken azura_field = system.addTerrain(Terrain.MAGNETIC_FIELD,
                new MagneticFieldParams(planet1.getRadius() + 200f, // Inner Radius
                                        planet1.getRadius() + 1500f, // Outer Radius
                                        planet1, // Focus
                                        planet1.getRadius() + 200f, // Visual Inner
                                        planet1.getRadius() + 1500f, // Visual Outer
                                        new Color(50, 0, 150, 50),  // Base Color
                                        0.5f, // Probability
                                        new Color(100, 20, 200), // Aurora Color 1
                                        new Color(0, 200, 255)   // Aurora Color 2
                                        ));
            azura_field.setCircularOrbit(planet1, 0, 0, 100);

            Misc.initConditionMarket(planet1);
            planet1.getMarket().addCondition(Conditions.HIGH_GRAVITY);
            planet1.getMarket().addCondition(Conditions.VOLATILES_ABUNDANT);
            planet1.getMarket().addCondition(Conditions.EXTREME_WEATHER); // Added because of the storms
            planet1.getMarket().addCondition(Conditions.DENSE_ATMOSPHERE);

            // =================================================================
            // --- SERAPHINA GATE (The Dormant Arch) ---
            // =================================================================
            // Placed at 4400, just outside the "Grime Belt" (2nd Ring).
            // This is "near the star" but safe from the immediate scorch zone.
            SectorEntityToken seraphinaGate = system.addCustomEntity("seraphina_gate", "Seraphina Gate", "inactive_gate", null);
            
            // Orbit: Angle 300, Radius 4400, Period 600
            seraphinaGate.setCircularOrbit(seraphina_star, 300, 4100, 600);
            
            // Set the custom description
            seraphinaGate.setCustomDescriptionId("gate_seraphina");
            
            // =================================================================
            // --- JUMP POINT: SERAPHINA FRINGE ---
            // =================================================================
            // Located at Azura (planet1). This drops players into the violet magnetic storm.
            JumpPointAPI fringe = Global.getFactory().createJumpPoint("seraphina_fringe", "Seraphina Fringe");
            fringe.setCircularOrbit(planet1, 90, planet1.getRadius() + 800f, 45); 
            fringe.setStandardWormholeToHyperspaceVisual();
            system.addEntity(fringe);

            // =================================================================
            // --- SYSTEM GENERATION & CLEANUP ---
            // =================================================================
            // Generates standard jump points for other major bodies (like Nitru/Star).
            system.autogenerateHyperspaceJumpPoints(true, true);

            // Final cleanup to clear hyperspace clouds around the system.
            cleanup(system);

        } catch (Exception e) {
            Global.getLogger(Seraphina.class).error("Error generating Seraphina:", e);
        }
    }

    // =================================================================
    // --- CLEANUP FUNCTION ---
    // =================================================================
    void cleanup(StarSystemAPI system) {
        try {
            HyperspaceTerrainPlugin plugin = (HyperspaceTerrainPlugin) Misc.getHyperspaceTerrain().getPlugin();
            NebulaEditor editor = new NebulaEditor(plugin);
            float x = system.getLocation().x;
            float y = system.getLocation().y;
            editor.clearArc(x, y, 0, 3000f, 0, 360f, 0.25f);
            editor.clearArc(x, y, 0, 2400f, 0, 360f, 0.0f);
        } catch (Exception e) {}
    }
}