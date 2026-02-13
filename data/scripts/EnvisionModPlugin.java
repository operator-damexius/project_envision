package data.scripts;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorGeneratorPlugin;
import com.fs.starfarer.api.campaign.StarSystemAPI;

import data.scripts.world.Isola;
import data.scripts.world.Pyralis;
import data.scripts.world.Seraphina;
import data.scripts.world.Vailara;
import data.scripts.world.Vespera;

public class EnvisionModPlugin extends BaseModPlugin {

    @Override
    public void onNewGame() {
        SectorAPI sector = Global.getSector();
        
        if (sector == null) {
            Global.getLogger(this.getClass()).error("Envision Mod: Sector is null. Generation aborted.");
            return;
        }

        Global.getLogger(this.getClass()).info("Envision Mod: Starting world generation flow...");

        generateSystem(new Vespera(),   "Vespera",   sector);
        generateSystem(new Vailara(),   "Vailara",   sector);
        generateSystem(new Seraphina(), "Seraphina", sector);
        generateSystem(new Pyralis(),   "Pyralis",   sector);
        generateSystem(new Isola(),     "Isola",     sector);

        Global.getLogger(this.getClass()).info("Envision Mod: World generation sequence finalized.");
    }

    private void generateSystem(SectorGeneratorPlugin system, String name, SectorAPI sector) {
        Global.getLogger(this.getClass()).info("Attempting to generate system: " + name);
        try {
            system.generate(sector);
            
            StarSystemAPI check = sector.getStarSystem(name);
            
            if (check == null) {
                 Global.getLogger(this.getClass()).warn(" - WARNING: " + name + " script finished, but system object is MISSING.");
            } else {
                if (check.getCenter() == null) {
                    throw new RuntimeException("System " + name + " was created but has NO CENTER entity defined.");
                }
                
                Global.getLogger(this.getClass()).info(" - SUCCESS: " + name + " verified at " + check.getLocation().toString());
            }
            
        } catch (Exception e) {
            Global.getLogger(this.getClass()).error(" - FAILED: Error generating " + name + ": " + e.getMessage(), e);
            
            StarSystemAPI brokenSystem = sector.getStarSystem(name);
            if (brokenSystem != null) {
                sector.removeStarSystem(brokenSystem); 
                Global.getLogger(this.getClass()).info(" - SAFETY: Removed broken system '" + name + "' to prevent game crashes.");
            }
        }
    }
}