package example.simulations;

import static io.gatling.javaapi.core.CoreDsl.*;

import example.BaseSimulation;
import example.scenarios.*;
import io.gatling.javaapi.core.*;

/**
 * Comprehensive Functional Test Suite
 * Executes all 21 scenarios systematically to validate complete API functionality
 * 
 * Scenario Coverage:
 * Owner Management (1-6): Get, Create, Read, Update, Delete, Validation
 * Pet Management (7-12): Create, Read, Update, Delete, Pet Types CRUD
 * Vet Management (13-17): Get, Create, Assign Specialties, Update/Delete, Specialties CRUD
 * Visit Management (18-21): Create, List, Update, Delete
 * 
 * Test Profile:
 * - Type: Functional validation test
 * - Users: 1 sequential user executing all scenarios
 * - Duration: ~5 minutes (sequential execution)
 * - Purpose: Validate complete API coverage
 */
public class FunctionalTestSuite extends BaseSimulation {

    // Scenario 1-6: Owner Management
    ScenarioBuilder ownerManagement = scenario("Owner Management Tests")
        .exec(OwnerScenario.getAllOwners)           // Scenario 1
        .pause(2)
        .exec(OwnerScenario.createOwner)            // Scenario 2
        .pause(2)
        .exec(OwnerScenario.getOwnerById)           // Scenario 3
        .pause(2)
        .exec(OwnerScenario.updateOwner)            // Scenario 4
        .pause(2)
        .exec(OwnerScenario.deleteOwner)            // Scenario 5
        .pause(2)
        .exec(OwnerScenario.createOwnerInvalidData);// Scenario 6

    // Scenario 7-12: Pet Management
    ScenarioBuilder petManagement = scenario("Pet Management Tests")
        // First create owner for pet
        .exec(OwnerScenario.createOwner)
        .pause(2)
        .exec(PetScenario.createPetForOwner)        // Scenario 7
        .pause(2)
        .exec(PetScenario.getPetById)               // Scenario 8
        .pause(2)
        .exec(PetScenario.updatePet)                // Scenario 9
        .pause(2)
        .exec(PetScenario.deletePet)                // Scenario 10
        .pause(2)
        .exec(PetScenario.getAllPetTypes)           // Scenario 11
        .pause(2)
        // Admin operations for pet types (Scenario 12)
        .exec(PetScenario.createPetType)
        .pause(1)
        .exec(PetScenario.updatePetType)
        .pause(1)
        .exec(PetScenario.deletePetType);

    // Scenario 13-17: Vet Management
    ScenarioBuilder vetManagement = scenario("Vet Management Tests")
        .exec(VetScenario.getAllVets)               // Scenario 13
        .pause(2)
        .exec(VetScenario.createVet)                // Scenario 14
        .pause(2)
        .exec(VetScenario.assignSpecialtyToVet)     // Scenario 15
        .pause(2)
        .exec(VetScenario.updateVet)                // Scenario 16a
        .pause(2)
        .exec(VetScenario.deleteVet)                // Scenario 16b
        .pause(2)
        // Admin operations for specialties (Scenario 17)
        .exec(VetScenario.getAllSpecialties)        // Scenario 17a
        .pause(1)
        .exec(VetScenario.createSpecialty)          // Scenario 17b
        .pause(1)
        .exec(VetScenario.updateSpecialty)          // Scenario 17c
        .pause(1)
        .exec(VetScenario.deleteSpecialty);         // Scenario 17d

    // Scenario 18-21: Visit Management
    ScenarioBuilder visitManagement = scenario("Visit Management Tests")
        // Create owner and pet first
        .exec(OwnerScenario.createOwner)
        .pause(1)
        .exec(PetScenario.createPetForOwner)
        .pause(2)
        .exec(VisitScenario.createVisitForPet)      // Scenario 18
        .pause(2)
        .exec(VisitScenario.getAllVisits)           // Scenario 19
        .pause(2)
        .exec(VisitScenario.updateVisit)            // Scenario 20
        .pause(2)
        .exec(VisitScenario.deleteVisit);           // Scenario 21

    {
        setUp(
            ownerManagement.injectOpen(atOnceUsers(1)),
            petManagement.injectOpen(
                nothingFor(60),  // Wait for owner tests to complete
                atOnceUsers(1)
            ),
            vetManagement.injectOpen(
                nothingFor(120), // Wait for pet tests to complete
                atOnceUsers(1)
            ),
            visitManagement.injectOpen(
                nothingFor(180), // Wait for vet tests to complete
                atOnceUsers(1)
            )
        ).protocols(httpProtocol);
    }
}
