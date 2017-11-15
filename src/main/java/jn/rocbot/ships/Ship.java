package jn.rocbot.ships;

import jn.rocbot.info.SimpleDescBuilder;
import jn.rocbot.utils.Formatter;
import jn.rocbot.info.Stores.ShipStore;

public class Ship implements Formatter{
    public final String name;
    public final String weapon;
    public final Aura aura;
    public final Zen zen;
    public final RARITY rarity;

    public Ship(String name, String weapon, Aura aura, Zen zen, RARITY rarity) {
        this.name = name;
        this.weapon = weapon;
        this.aura = aura;
        this.zen = zen;
        this.rarity = rarity;
    }

    public String simpleToString(){
        return rarity.toEmoji() + " " + bold(name + ":") + " Weapon: " + italic(weapon) + ", Aura: " + italic(aura.name) + ", Zen: " + italic(zen.name);
    }

    public String info(){
        SimpleDescBuilder info = new SimpleDescBuilder(name);
        info.addLine("Weapon: " + italic(weapon));
        info.addLine("\nZen: " + zen.simpleDesc());
        info.addLine("\nAura: " + aura.simpleDesc());
        return info.get();
    }

    public static boolean isShip(String name) throws ShipStore.ShipNotFoundException {
        Boolean found = false;
        for (Ship s : ShipStore.SHIPS) {
            if (s.name.toLowerCase().equals(name.toLowerCase())) {
                found = true; break;
            }
        } return found;
    }
}
