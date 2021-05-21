package it.polimi.db2.gma.entities.Enum;

public enum Expertise_level {
    LOW("LOW"),
    MEDIUM("MEDIUM"),
    HIGH("HIGH");

    private String name;

    Expertise_level(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


}
