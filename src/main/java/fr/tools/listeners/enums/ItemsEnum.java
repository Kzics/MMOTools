package fr.tools.listeners.enums;

import fr.tools.listeners.manager.ItemsManager;

public enum ItemsEnum {

    AXE("Axe"),
    PICKAXE("Pickaxe"),
    ROD("Rod");

    private final String id;
    ItemsEnum(final String id){
        this.id = id;
    }


    public String getId() {
        return id;
    }
}
