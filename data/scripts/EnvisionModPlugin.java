package data.scripts;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorGeneratorPlugin;
import com.fs.starfarer.api.campaign.StarSystemAPI;

// Imports for your systems
import data.scripts.world.Pyralis;
import data.scripts.world.Seraphina;
import data.scripts.world.Vailara;
import data.scripts.world.Vespera;

public class EnvisionModPlugin extends BaseModPlugin {

    @Override
    public void onNewGame() {
        SectorAPI sector = Global.getSector();
        
        // Safety Check: Ensure the sector exists
        if (sector == null) {
            Global.getLogger(this.getClass()).error("Envision Mod: Sector is null. Generation aborted.");
            return;
        }

        Global.getLogger(this.getClass()).info("Envision Mod: Starting world generation flow...");

        // 1. Generate Systems with Enhanced Verification and Safety Cleanup
        generateSystem(new Vespera(),   "Vespera",   sector);
        generateSystem(new Vailara(),   "Vailara",   sector);
        generateSystem(new Seraphina(), "Seraphina", sector);
        generateSystem(new Pyralis(),   "Pyralis",   sector);

        // 2. Initialize Relations
        initRelations(sector);
        
        Global.getLogger(this.getClass()).info("Envision Mod: World generation sequence finalized.");
    }

    private void generateSystem(SectorGeneratorPlugin system, String name, SectorAPI sector) {
        Global.getLogger(this.getClass()).info("Attempting to generate system: " + name);
        try {
            // Run the generation script
            system.generate(sector);
            
            // VERIFICATION LOGGING: Check if the system actually was added to the sector
            StarSystemAPI check = sector.getStarSystem(name);
            
            if (check == null) {
                 Global.getLogger(this.getClass()).warn(" - WARNING: " + name + " script finished, but system object is MISSING.");
            } else {
                // SAFETY CHECK: Does the system have a Center?
                // If getCenter() is null, the Pirate/Path managers will crash later.
                if (check.getCenter() == null) {
                    throw new RuntimeException("System " + name + " was created but has NO CENTER entity defined.");
                }
                
                Global.getLogger(this.getClass()).info(" - SUCCESS: " + name + " verified at " + check.getLocation().toString());
            }
            
        } catch (Exception e) {
            // Captures the full stack trace for specific errors (like missing star types)
            Global.getLogger(this.getClass()).error(" - FAILED: Error generating " + name + ": " + e.getMessage(), e);
            
            // CRITICAL SAFETY FIX:
            // If the script crashed halfway through, the system might exist in a broken state.
            // We must remove it to prevent the LuddicPathBaseManager NullPointerException.
            StarSystemAPI brokenSystem = sector.getStarSystem(name);
            if (brokenSystem != null) {
                sector.removeStarSystem(brokenSystem);
                Global.getLogger(this.getClass()).info(" - SAFETY: Removed broken system '" + name + "' to prevent game crashes.");
            }
        }
    }

    private void initRelations(SectorAPI sector) {
        try {
            EnvisionFactionRelations.init(sector);
            Global.getLogger(this.getClass()).info(" - SUCCESS: Initialized Diplomacy");
        } catch (Exception e) {
            Global.getLogger(this.getClass()).error(" - FAILED: Diplomacy error: " + e.getMessage(), e);
        }
    }
}