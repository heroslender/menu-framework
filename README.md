# HMF - Heroslender Menu Framework

[![GitHub Workflow Status](https://img.shields.io/github/workflow/status/heroslender/menu-framework/Build?label=Build&logo=GitHub)](https://github.com/heroslender/menu-framework/actions/workflows/build.yml)
![Maven metadata URL](https://img.shields.io/maven-metadata/v?label=Version&metadataUrl=https%3A%2F%2Fnexus.heroslender.com%2Frepository%2Fmaven-snapshots%2Fcom%2Fheroslender%2Fhmf-bukkit%2Fmaven-metadata.xml)
[![GitHub stars](https://img.shields.io/github/stars/heroslender/menu-framework.svg?label=Stars)](https://github.com/heroslender/menu-framework/stargazers)
[![GitHub issues](https://img.shields.io/github/issues-raw/heroslender/menu-framework.svg?label=Issues)](https://github.com/heroslender/menu-framework/issues)
[![GitHub last commit](https://img.shields.io/github/last-commit/heroslender/menu-framework.svg?label=Last%20Commit)](https://github.com/heroslender/menu-framework/commit)
[![Open Source Love](https://badges.frapsoft.com/os/v2/open-source.png?v=103)](https://github.com/ellerbrock/open-source-badges/)

Innovating the way you make menus in minecraft java edition by using maps to render a custom UI and allowing the 
player to interact with it.

- [HMF - Heroslender Menu Framework](#hmf---heroslender-menu-framework)
    - [Sample](#sample)
        - [Creating a new menu](#creating-a-new-menu)
        - [Sending the menu to the player](#sending-the-menu-to-the-player)
    - [Dependency](#dependency)
        - [Gradle kts](#gradle-kts)
        - [Maven](#maven)

## Sample

### Creating a new menu

```kotlin
class SampleMenu(player: Player, manager: BukkitMenuManager) : BaseMenu(player, manager = manager) {
    override fun Composable.getUi() {
        Box(modifier = Modifier.fill().backgroundColor(Color.CYAN_4)) {
            Box(
                modifier = Modifier
                    .fixedSize(25)
                    .margin(top = 15, left = 15)
                    .backgroundColor(Color.RED_1)
                    .clickable {
                        owner.sendMessage("Clicou no vermelho!")
                    }
            ) {}
            Box(
                modifier = Modifier
                    .fixedSize(25)
                    .margin(top = 35, left = 35)
                    .backgroundColor(Color.GREEN_10)
                    .clickable {
                        owner.sendMessage("Clicou no verde!")
                    }
            ) {}
            
            Column(modifier = Modifier.margin(top = 70, left = 15)) {
                val fontStyle = FontStyle(font = MINECRAFTIA_24, Color.BLACK_1, Color.TRANSPARENT, Color.CYAN_8)
                Label("Hello World", style = fontStyle)
            }
        }
    }
}
```

Result: 

![Render Result](https://i.heroslender.com/tKWxw.png)

### Sending the menu to the player

In order to create menus you need a `MenuManager`, it is responsible to handle the cursor updates and player interactions.
A single `MenuManager` instance can be shared among multiple menus & players.

```kotlin
val manager = BukkitMenuManager(yourPlugin)

val menu = SampleMenu(player, manager)
menu.send()

// To close the menu just call the `Menu#destroy` method.
menu.destroy()
```

## Dependency

### Gradle kts

```kotlin
repositories {
    maven("https://nexus.heroslender.com/repository/maven-public/")
}

dependencies {
    implementation("com.heroslender:hmf-bukkit:0.0.1-SNAPSHOT")
}
```

### Maven

```xml
<repository>
    <id>heroslender-repo</id>
    <url>https://nexus.heroslender.com/repository/maven-public/</url>
</repository>
```

```xml
<dependency>
    <groupId>com.heroslender</groupId>
    <artifactId>hmf-bukkit</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <scope>compile</scope>
</dependency>
```
