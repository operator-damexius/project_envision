package data.scripts.world;

// --- JAVA IMPORTS ---
import java.util.ArrayList;
import java.util.List;

// --- STARSECTOR API IMPORTS ---
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.RepLevel;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.CustomCampaignEntityAPI; 
import com.fs.starfarer.api.campaign.SectorGeneratorPlugin;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.impl.campaign.ids.Entities;

// --- PROCGEN IMPORTS ---
import com.fs.starfarer.api.impl.campaign.procgen.Constellation;
import com.fs.starfarer.api.impl.campaign.procgen.StarAge;

// --- SYSTEM IMPORTS ---
import data.scripts.world.Seraphina;
import data.scripts.world.Vailara;
import data.scripts.world.Vespera;
import data.scripts.world.Pyralis;

public class SolvarisGen implements SectorGeneratorPlugin {

    @Override
    public void generate(SectorAPI sector) {
        
        Global.getLogger(this.getClass()).info("Generating Solvaris Quadrant...");

        // 1. GENERATE SYSTEMS
        new Seraphina().generate(sector);
        new Vailara().generate(sector);
        new Vespera().generate(sector);
        new Pyralis().generate(sector); 

        // Retrieve systems safely
        StarSystemAPI seraphina = sector.getStarSystem("Seraphina");
        StarSystemAPI vailara = sector.getStarSystem("Vailara");
        StarSystemAPI vespera = sector.getStarSystem("Vespera");
        StarSystemAPI pyralis = sector.getStarSystem("Pyralis");

        // SAFETY CHECK
        if (seraphina == null || vailara == null || vespera == null || pyralis == null) {
            Global.getLogger(this.getClass()).error("CRITICAL: SolvarisGen failed to find systems.");
            return;
        }

        // 2. SET COORDINATES
        pyralis.getLocation().set(32000, -10000);   
        vespera.getLocation().set(30000, -11500);   
        vailara.getLocation().set(29000, -13500);   
        seraphina.getLocation().set(35500, -10500); 

        // 3. CREATE THE CONSTELLATION
        Constellation solvarisQuadrant = new Constellation(
            Constellation.ConstellationType.NORMAL,
            StarAge.YOUNG
        );
        solvarisQuadrant.setNameOverride("The Solvaris Quadrant");

        java.util.List<StarSystemAPI> systems = new java.util.ArrayList<StarSystemAPI>();
        systems.add(seraphina);
        systems.add(vailara);
        systems.add(vespera);
        systems.add(pyralis);

        for (StarSystemAPI system : systems) {
            solvarisQuadrant.getSystems().add(system);
            system.setConstellation(solvarisQuadrant);
        }

        // =====================================================================
        // 4. ADD THE MAP LABEL (BASE_CONSTELLATION_LABEL)
        // =====================================================================
        float centerX = (seraphina.getLocation().x + vailara.getLocation().x + 
                         vespera.getLocation().x + pyralis.getLocation().x) / 9f;
                         
        float centerY = (seraphina.getLocation().y + vailara.getLocation().y + 
                         vespera.getLocation().y + pyralis.getLocation().y) / 9f;

        // Using the standard BASE_CONSTELLATION_LABEL as requested
        SectorEntityToken label = sector.getHyperspace().addCustomEntity(
            "solvaris_label", 
            "The Solvaris Quadrant", 
            Entities.BASE_CONSTELLATION_LABEL, 
            "solvaris"
        );
        
        label.getLocation().set(centerX, centerY);

        // Ensure it has size so it isn't culled as invisible
        if (label instanceof CustomCampaignEntityAPI) {
            ((CustomCampaignEntityAPI)label).setRadius(2000f);
        }
        
        // Link the constellation object (Best practice for this label type)
        label.getMemoryWithoutUpdate().set("$constellation", solvarisQuadrant);
        
        // =====================================================================
        // 5. FACTION RELATIONSHIPS
        // =====================================================================
        if (sector.getFaction("argent") != null) {
            sector.getFaction("solvaris").setRelationship("argent", RepLevel.COOPERATIVE);
        }
        
        Global.getLogger(this.getClass()).info("Solvaris Quadrant generation complete.");
    }
}