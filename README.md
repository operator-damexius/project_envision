# Project Envision
**Version 0.7.0**

> *"Expanding the sector with high-fidelity systems, intricate orbital mechanics, and deep environmental storytelling."*

## 🌌 Overview
**Project Envision** is a content expansion mod for Starsector. This update introduces the **Solvaris Trinity**—three hand-crafted star systems that serve as the home for a new high-tech industrial power. Each system features unique visual assets, custom ring shaders, and lore-accurate planetary conditions.

---

## 🚀 Update 0.7.0 Changelog

### **New Content: The Solvaris Trinity**
Three fully generated star systems have been added to the sector generation:

#### **1. Seraphina System** (The Violent Blue Giant)
* **Star:** Added a massive O-Class Blue Supergiant with custom "Electric Blue" corona and high-energy lighting.
* **Planets:**
    * **Nitru:** A "Super-Habitable" Crimson world featuring "The Sapphire Veil"—a natural blue ring reflecting the star's light.
    * **Azura:** A deep indigo gas giant with a custom **Magnetic Field** and violet auroras acting as a system shield.
    * **Aaris:** A silver-coated industrial moon ("The Silver Anvil").
* **Mechanics:** Implemented a "Thermal Gradient" ring system, transitioning from burnt copper dust near the star to frozen white ice at the edge.

#### **2. Vailara System** (The Ring Lattice)
* **Star:** Added a sterile White Star (A-Class).
* **The 40 Rings:** Implemented a massive, concentric ring system divided into 4 named clusters.
    * *Feature:* Added **Ring Identifiers**. Hovering over ring bands now displays specific names (e.g., "The Inner Lattice," "The Frost Veil").
* **Entities:** Added **Nyxara Prime**, a fortress station embedded within the ring gaps.

#### **3. Vespera System** (The Binary Hub)
* **Stars:** A dual-star system featuring a Yellow Giant and a White Dwarf.
* **Planets:** Added **Aetheris** (Gold/Green Terran) and **Cryon** (Ice Giant).
* **Visuals:** Added the "Halo of Diamonds" ring system around Cryon.

### 🛠️ Technical Fixes & Adjustments
* **Crash Fix:** Resolved a `NullPointerException` related to Faction IDs. All Solvaris markets (`Nitru`, `Aetheris`, etc.) now correctly assign `solvaris` ownership upon generation.
* **Stability:** Fixed the `cleanup()` function radius to ensure the massive 18,000+ radius systems are fully cleared of hyperspace clouds on the map.
* **Visuals:** Corrected the color blending for "The Sapphire Veil" to use a natural ice texture (`rings_ice0`) instead of artificial plating.
* **Orbit Logic:** Adjusted Gate positions in Seraphina and Vespera to prevent them from floating in deep space; they now orbit logical gravity wells (Stars/Giants).

---

## 📦 Installation
1.  Download the latest release.
2.  Unzip the folder.
3.  Move the `project_envision` folder into your Starsector `mods` directory.
4.  Launch Starsector and enable **"Project Envision"** in the mod menu.

## ⚠️ Compatibility
* **Save Compatible:** No. (Requires a **New Game** for the new star systems to generate).

## 📬 Contact & Source
* **Repo:** [GitHub - Project Envision](https://github.com/operator-damexius/project_envision)
* **Author:** operator-damexius