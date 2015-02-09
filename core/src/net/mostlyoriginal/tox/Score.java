package net.mostlyoriginal.tox;

/**
 * @author Daan van Yperen
 */
public class Score {
    public static final int STARTING_FLOOR = 20;

    public static final int POINTS_MONSTER_KILL = 100;
    public static final int POINTS_MONSTER_KILL_PER_LEVEL = 10;
    public static final int POINTS_NEXT_LEVEL = 1000;
    public static final int POINTS_GRAB_ITEM = 150;
    public static final int POINTS_KILL_JAILER = 5000;

    public int floor = STARTING_FLOOR;
    public String killer;
    public int stars;
    public int points = 0;
    public int playerLevel=1;


    public String floorName() {

        if (floor == 1) return "1st floor";
        if (floor == 2) return "2nd floor";
        if (floor == 3) return "3rd floor";

        return floor + "th floor";
    }
}
