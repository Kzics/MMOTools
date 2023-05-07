package fr.tools.listeners.enums;

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
