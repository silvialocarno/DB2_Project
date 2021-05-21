package it.polimi.db2.gma.entities.Enum;

public enum Sex {
    M("M"),
    F("F"),
    N("N");

    private String name;

    Sex(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
