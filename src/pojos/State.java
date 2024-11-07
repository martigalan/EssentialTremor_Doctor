package pojos;

public enum State {
    /**
     * The condition didn't evolve
     */
    STABLE(1, "The patient is stable and under control."),
    /**
     * The condition made good progress
     */
    GOOD(2, "The patient is in good health."),
    /**
     * The condition worsened
     */
    BAD(3, "The patient is in bad condition."),
    /**
     * The case is closed, due to the patient moving hospitals, dying or any other cause that would cause the patient to stop follwoing the conditions progress
     */
    CLOSED(4, "The case is closed, no further action is needed.");

    /**
     * Small description of each condition state with each identifier
     */
    private final int id;
    private final String description;

    State(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Representation of the enumeration
     * @return String of the state and its description
     */
    @Override
    public String toString() {
        return name() + " (ID: " + id + "): " + description;
    }

    /**
     * Method to get State by ID
     * @param id ID of the state
     * @return the State corresponding to the ID or null if not found
     */
    public static State getById(int id) {
        for (State state : values()) {
            if (state.getId() == id) {
                return state;
            }
        }
        return null;
    }
}
