package example.config;

public class Constants {
    
    // Session keys
    public static final String OWNER_ID = "ownerId";
    public static final String PET_ID = "petId";
    public static final String VET_ID = "vetId";
    public static final String VISIT_ID = "visitId";
    
    // File paths
    public static final String OWNERS_CSV = "data/owners.csv";
    public static final String PETS_CSV = "data/pets.csv";
    public static final String VETS_CSV = "data/vets.csv";
    public static final String VISITS_CSV = "data/visits.csv";
    
    // API Endpoints
    public static final String OWNERS_API = "/api/owners";
    public static final String PETS_API = "/api/pets";
    public static final String VETS_API = "/api/vets";
    public static final String VISITS_API = "/api/visits";
    
    private Constants() {
        // Utility class - prevent instantiation
    }
}
