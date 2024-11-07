package pojos;

public enum Treatment {
    /**
     * The patient needs surgery
     */
    SURGERY(1, "The patient needs surgery."),
    /**
     * The patient needs to take propranolol
     */
    PROPRANOLOL(2, "The patient needs pharmacology treatment, exactly Propranolol."),
    /**
     * The patient needs primidone
     */
    PRIMIDONE(3, "The patient needs pharmacology treatment, exactly Primidone.");

    /**
     * Small description of each treatment with each identifier
     */
    private final int id;
    private final String description;

    Treatment(int id, String description) {
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
     * @return String of the treatment and its description
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
    public static Treatment getById(int id) {
        for (Treatment treatment : values()) {
            if (treatment.getId() == id) {
                return treatment;
            }
        }
        return null;
    }
}
