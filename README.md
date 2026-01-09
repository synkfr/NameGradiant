# NameGradient

A Minecraft plugin that allows players to apply beautiful gradient colors to their display names.

## Features

- **Gradient Name Colors** - Apply smooth color gradients to player names
- **GUI Selector** - Easy-to-use paginated menu for selecting gradients
- **Multiple Name Modes** - Support for PLAYERNAME, DISPLAYNAME, ESSENTIALS, CMI, and PLACEHOLDER modes
- **Override System** - Weight-based priority for forced gradients
- **PlaceholderAPI Support** - Use gradients in other plugins via placeholders
- **Fully Configurable** - Customize gradients, GUI, and messages

## Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/gradient` | Opens the gradient selection menu | `namegradient.use` |
| `/namegradient` | Shows plugin usage | - |
| `/namegradient reload` | Reloads configuration files | `namegradient.reload` |
| `/namegradient clear <player>` | Clears a player's gradient | `namegradient.others.clear` |
| `/namegradient change <player> <id>` | Changes a player's gradient | `namegradient.others.change` |

## Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `namegradient.use` | Open the gradient selector menu | true |
| `namegradient.gradient.<id>` | Use a specific gradient | op |
| `namegradient.gradient.*` | Use all gradients | op |
| `namegradient.reload` | Reload configuration | op |
| `namegradient.others.clear` | Clear another player's gradient | op |
| `namegradient.others.change` | Change another player's gradient | op |
| `namegradient.override.exempt` | Bypass override gradient restrictions | op |

## PlaceholderAPI

If PlaceholderAPI is installed, you can use:

```
%namegradient_name%
```

This returns the player's name with their active gradient applied. Falls back to the player's regular name if no gradient is active.

## Configuration

### config.yml

```yaml
name:
  mode: "PLAYERNAME"           # PLAYERNAME, DISPLAYNAME, ESSENTIALS, CMI, PLACEHOLDER
  colour-dependent: false      # Don't apply gradient if source already has colors
  placeholder-source: "%player_name%"  # PlaceholderAPI placeholder for PLACEHOLDER mode
  override-displayname: true   # Update player's display name

menu:
  title: "&8Gradients"
  size: 45                     # Must be multiple of 9
  per_page: 21                 # Gradients per page
  # ... GUI customization options
```

### gradients.yml

```yaml
gradients:
  1:
    name: "AbleGamers Name Colour"
    startColour: "#F2709C"
    endColour: "#FFB454"
    alternatePermission: "yourserver.ablegamers"      # Optional
    overridePermission: "yourserver.ablegamers.override"  # Optional
    overrideWeight: 1                                  # Priority for override
  2:
    name: "Beyond Blue $25 Name Colour"
    startColour: "#59EAA4"
    endColour: "#089CCC"
```

### messages.yml

All plugin messages are fully customizable. Supports color codes (`&`) and hex colors (`#RRGGBB`).

## Name Modes

| Mode | Description |
|------|-------------|
| `PLAYERNAME` | Uses the player's actual Minecraft username |
| `DISPLAYNAME` | Uses the player's current display name (stripped of colors) |
| `ESSENTIALS` | Uses the player's Essentials nickname |
| `CMI` | Uses the player's CMI nickname |
| `PLACEHOLDER` | Uses a PlaceholderAPI placeholder as the source |

## Override Gradients

Override gradients are automatically applied to players with the `overridePermission`. If a player has multiple override permissions, the gradient with the highest `overrideWeight` takes priority.

Players with override gradients cannot change their gradient unless an admin with `namegradient.override.exempt` permission modifies it.

## Dependencies

- **Required**: Spigot/Paper 1.20.4+
- **Optional**: PlaceholderAPI, EssentialsX, CMI

## Installation

1. Download `NameGradient-1.0.0.jar` from the `target/` folder
2. Place it in your server's `plugins/` directory
3. Restart your server
4. Configure the plugin in `plugins/NameGradient/`

## Building

```bash
mvn clean package
```

The compiled JAR will be in `target/NameGradient-1.0.0.jar`

## License

This project is open source.
