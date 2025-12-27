package data.scripts;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorGeneratorPlugin;

// Imports for your systems
import data.scripts.world.Pyralis;
import data.scripts.world.Seraphina;
import data.scripts.world.Vailara;
import data.scripts.world.Vespera;

public class EnvisionModPlugin extends BaseModPlugin {

    @Override
    public void onNewGame() {
        SectorAPI sector = Global.getSector();
        Global.getLogger(this.getClass()).info("Envision Mod: Starting world generation...");

        // 1. Generate Systems with Crash Detection
        generateSystem(new Vespera(),   "Vespera",   sector);
        generateSystem(new Vailara(),   "Vailara",   sector);
        generateSystem(new Seraphina(), "Seraphina", sector);
        generateSystem(new Pyralis(),   "Pyralis",   sector);

        // 2. Initialize Relations with Crash Detection
        initRelations(sector);
        
        Global.getLogger(this.getClass()).info("Envision Mod: World generation complete.");
    }

    // --- HELPER METHODS (The Detection Logic) ---

    // This keeps the "Detection" you wanted. 
    // If a system fails, it catches the error and logs it instead of crashing the game.
    private void generateSystem(SectorGeneratorPlugin system, String name, SectorAPI sector) {
        try {
            system.generate(sector);
            Global.getLogger(this.getClass()).info(" - SUCCESS: Generated " + name);
        } catch (Exception e) {
            Global.getLogger(this.getClass()).error(" - FAILED: Could not generate " + name, e);
        }
    }

    private void initRelations(SectorAPI sector) {
        try {
            EnvisionFactionRelations.init(sector);
            Global.getLogger(this.getClass()).info(" - SUCCESS: Initialized Diplomacy");
        } catch (Exception e) {
            Global.getLogger(this.getClass()).error(" - FAILED: Could not initialize Diplomacy", e);
        }
    }
}