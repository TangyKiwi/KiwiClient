<p align="center">
<img src="https://cdn.discordapp.com/emojis/783170477115965480.gif?v=1" width="20%"/>
</p>

<h1 align="center">KiwiClient v2</h1>

<div align="center">
    <img src="https://img.shields.io/github/last-commit/TangyKiwi/KiwiClient/v2" alt="GitHub last commit"/>
    <img src="https://img.shields.io/github/actions/workflow/status/TangyKiwi/KiwiClient/build.yml?branch=v2" alt="Build status"/>
    <img src="https://img.shields.io/badge/MC-26.1.2-brightgreen.svg" alt="Minecraft"/>
    <br>
    <img src="https://img.shields.io/github/v/release/TangyKiwi/KiwiClient.svg" alt="Release"/>
    <img src="https://img.shields.io/github/languages/code-size/TangyKiwi/KiwiClient" alt="GitHub code size in bytes"/>
    <img src="https://img.shields.io/endpoint?url=https://ghloc.vercel.app/api/TangyKiwi/KiwiClient/badge?branch=v2&filter=.java$&label=lines%20of%20code&color=blue" alt="GitHub lines of code"/>
</div>

## Installation
### Standalone Vanilla Installer (WIP, DO NOT USE)
- Download the [installer](https://github.com/TangyKiwi/KiwiClient-Installer/releases)
- Hit install, a new profile called KiwiClient for X.X.X will be created in your launcher
- Place additional mods in the .minecraft/kiwiclient-mods/X.X.X folder
### Manual
- Build the mod yourself (See Setup below), or get the latest [release](https://github.com/TangyKiwi/KiwiClient/releases)
- If you want the most up-to-date version, please build the client yourself or ask me for the jar, I do not update releases often
- **Vanilla**: Place the KiwiClient jar (build/libs/KiwiClient-X.X.X.jar) in your mods folder

## Setup

For instructions please see the [fabric wiki page](https://fabricmc.net/wiki/tutorial:setup) that relates to the IDE that you are using.

## Info

Fabric Utility Mod.

"_First I quacc, then I hacc._"

### Modules

DiscordRPC ("_You use the hacc, then proudly quacc._")

<details>
<summary>Client</summary>
- *BetterChat<br>
- *BetterTab<br>
- ClickGui<br>
    <details>
    <summary>HUD</summary>
    - ActiveMods<br>
    - Armor<br>
    - Biome<br>
    - Coords<br>
    - FPS<br>
    - Inventory<br>
    - IP<br>
    - NetherCoords<br>
    - Ping<br>
    - *Waypoints<br>
    - Speed<br>
    - TPS<br>
    </details>
- *Compass<br>
- HUD<br>  
- *NoScoreboard<br>
- *PotionTimers<br>
- *Time<br>
- Tooltips<br>
- VanillaSpoof<br>
</details>

<details>
<summary>Combat</summary>
- Criticals<br>
- *TargetHUD<br>
- TriggerBot<br>
</details>

<details>
<summary>Movement</summary>
- *BoatPhase<br>
- *ElytraFly<br>
- *EntityFly<br>
- *FastBridge<br>
- Fly<br>
- InvMove<br>
- NoFall<br>
- *NoWorldBorder<br>
- *SafeWalk<br>
- Speed<br>
</details>

<details>
<summary>Player</summary>
- AntiHunger<br>
- *AutoContainer<br>
- *AutoTool<br>
- *FastBreak<br>
</details>

<details>
<summary>Render</summary>
- ESP<br>
- Freecam<br>
- Fullbright<br>
- *ItemPhysics<br>
- *LogoutSpots<br>
- *Nametags<br>
- *NoPortal<br>
- NoRender<br>
- *Search<br>
- SeedRay<br>
- StorageESP<br>
- *TNTimer<br>
- Tracers<br>
- XRay<br>
- *Zoom<br>
</details>

<details>
<summary>Other</summary>
- *AntiHuman<br>
- *Background<br>
- Cape<br>
- *Deadmau5Ears<br>
- *LoadingScreen<br>
- *MainMenu<br>
- *NoIP<br>
- *NoLO<br>
</details>

\* to be implemented in v2 

### Commands (`,`)
- *bind/b [module] [key]
- *enchant [type/enchantment] [level]
- ez
- ff
- gamemode [gamemode]
- *give [item{nbt}] [count]
- *lookat [x][y][z]
- resetclickgui
- resethud
- *say [message]
- *searchblocks [add/rem/list] [block]
- *server
- seedray [seed]
- *target [name]
- toggle/t [module]
- unbind/ub [module]
- vclip [blocks]

\* to be implemented in v2

## Disclaimer

For educational purposes only. Use at your own risk.

## License

This project is licensed under the [GNU General Public License v3.0](https://www.gnu.org/licenses/gpl-3.0.en.html).

If you use **ANY** code from the source:
- You must disclose the source code of your modified work, and the source code you took from this project. This means you are not allowed to use code from this project (even partially) in a closed-source and/or obfuscated application.
- Your application must also be licensed under the same license.


## Credits
A lot of code has been *skidded* from these clients. Code has been adapted or improved upon to fit for KiwiClient.

**Clients**:  
[Meteor Client](https://github.com/MeteorDevelopment/meteor-client)  
[Aoba](https://github.com/Cocolots/Aoba-Client/)
[BleachHack](https://github.com/BleachDrinker420/BleachHack)  
[Atomic](https://gitlab.com/0x151/atomic)  
[JexClient](https://github.com/DustinRepo/JexClient)  
[Wurst](https://github.com/Wurst-Imperium/Wurst7)  
[ThunderHack-Recode](https://github.com/Pan4ur/ThunderHack-Recode)  
