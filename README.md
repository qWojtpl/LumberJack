<p align="center">
    <img src="https://media.discordapp.net/attachments/816647374239694849/1090391345010581595/picturetopeople.png">
</p>

# LumberJack

<p>Add lumberjack feature to your minecraft server</p>
<p>Tested Minecraft versions:</p>

`1.19.3`

# Installation

Put lumberjack.jar to your plugins folder and restart the server.

# Configuration

<details><summary>config.yml</summary>

<br>

`maxTreeHeight` - Defines max tree height. To this level always would be checked if there's leaves or wood. Default: `20`<br><br>
`woodDestroyInterval` - In ticks. This is for animation, if set to 2 then one wood block will be destroyed every 2 ticks. Default: `2`<br><br>
`leavesDestroyInterval` - In ticks. This is for animation, if set to 2 then one leaves block will be destroyed every 2 ticks. Default: `2`<br><br>
`woodRange` - Defines range from source block to every sides to check for wood. Default: `2`<br><br>
`leavesRange` - Defines range from source block to every sides to check for leaves. Default: `2`<br><br>
`requiredLeaves` - How many leaves must be found to chop this structure like lumberjack (it's protection against mistake house destroying). Default: `15`<br><br>
`leavesReduction` - By default in minecraft, if you destroy 1 leaves block it will remove 1 from item's durability. Lumberjack feature can destroy all leaves from tree, but if you don't want to destroy your axe so much you can set value to higher numbers. If this number is 18, then every 18 blocks 1 durability will be removed form axe. Default: `18`<br><br>
`permission` - Permission to use lumberjack feature. Default: `lumberjack.use`<br><br>
`axes` - YAML list of items, which can be used in lumberjack feature.<br>

</details>

# Commands & Permissions

`Use lumberjack feature` - Permission: `lumberjack.use` (unless you change it in config)<br>
`/lj` - Shows config and help `lumberjack.manage`<br>
`/lj reload` - Reload configuration `lumberjack.manage`<br>

# Additional information

- If durability can handle leaves and wood - leaves and wood will be chopped<br>
- If durability can handle wood but not leaves - only wood will be chopped<br>
- If durability cannot handle any of these, nothing will happen to don't destroy player's axe<br>
- Unbreaking enchantment affects to durability change<br>
- If item is unbreakable then nothing will happen to durability<br>
- Silktouch won't destroy leaves as a block<br>
