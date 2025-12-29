package data.scripts;

import com.fs.starfarer.api.Global;
import java.lang.reflect.Method;

public class NexerelinHelper {

    // SAFELY checks if the current game is running in "Corvus Mode" (Fixed Map). Uses Reflection so it compiles even if Nexerelin is missing.
    public static boolean isCorvusMode() {
        // 1. Check if mod is even loaded
        if (!Global.getSettings().getModManager().isModEnabled("nexerelin")) {
            return true; // Default to TRUE (Generate Systems) if Nexerelin isn't there.
        }

        try {
            // 2. Use Reflection to look up the class and method dynamically
            Class<?> sectorManager = Global.getSettings().getScriptClassLoader().loadClass("exerelin.campaign.SectorManager");
            Method getCorvusMode = sectorManager.getMethod("getCorvusMode");
            
            // 3. Invoke the method
            return (Boolean) getCorvusMode.invoke(null);
            
        } catch (Exception e) {
            // 4. Failsafe: If anything goes wrong, assume we should generate the systems.
            Global.getLogger(NexerelinHelper.class).warn("Envision Mod: Could not access Nexerelin API. Defaulting to Corvus Mode.", e);
            return true;
        }
    }
}