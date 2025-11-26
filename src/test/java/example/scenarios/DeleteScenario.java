package example.scenarios;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import example.Constants;
import io.gatling.javaapi.core.*;

/**
 * DELETE Scenario - Remove data
 * Simulates cleanup operations (use with caution in production!)
 */
public class DeleteScenario {

    /**
     * Create and Delete Pet
     */
    public static ChainBuilder createAndDeletePet = 
        exec(OwnerPetScenario.createOwnerAndPet)
        .pause(1, 2)
        .exec(
            http("Delete Pet #{" + Constants.PET_ID + "}")
                .delete(Constants.PETS_API + "/#{" + Constants.PET_ID + "}")
                .check(status().is(204))
        )
        .pause(1);

    /**
     * Create and Delete Owner (will also delete associated pets)
     */
    public static ChainBuilder createAndDeleteOwner = 
        exec(OwnerPetScenario.createOwnerAndPet)
        .pause(1, 2)
        .exec(
            http("Delete Owner #{" + Constants.OWNER_ID + "}")
                .delete(Constants.OWNERS_API + "/#{" + Constants.OWNER_ID + "}")
                .check(status().is(204))
        )
        .pause(1);

    /**
     * Full CRUD cycle - Create, Read, Update, Delete
     */
    public static ChainBuilder fullCRUDCycle = 
        // Create
        exec(OwnerPetScenario.createOwnerAndPet)
        .pause(1, 2)
        // Read
        .exec(
            http("Read Owner #{" + Constants.OWNER_ID + "}")
                .get(Constants.OWNERS_API + "/#{" + Constants.OWNER_ID + "}")
                .check(status().is(200))
        )
        .pause(1, 2)
        // Update
        .exec(UpdateScenario.updateOwner)
        .pause(1, 2)
        // Delete
        .exec(
            http("Delete Owner #{" + Constants.OWNER_ID + "}")
                .delete(Constants.OWNERS_API + "/#{" + Constants.OWNER_ID + "}")
                .check(status().is(204))
        );
}
