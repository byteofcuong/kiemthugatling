package example.simulations;

import static io.gatling.javaapi.core.CoreDsl.*;

import example.BaseSimulation;
import example.scenarios.*;
import io.gatling.javaapi.core.*;

/**
 * Complete Clinical Workflow Test
 * Simulates end-to-end business process in a veterinary clinic
 * 
 * Workflow Steps:
 * 1. Create Owner (new patient registration)
 * 2. Add Pet to Owner
 * 3. Browse available Vets
 * 4. Schedule Visit for Pet
 * 5. Update Visit details
 * 6. Verify Visit was recorded
 * 
 * Test Profile:
 * - Type: End-to-end business process validation
 * - Users: 10 concurrent workflows
 * - Duration: 5 minutes
 * - Purpose: Validate complete clinical process flow
 */
public class ClinicalWorkflowTest extends BaseSimulation {

    /**
     * Complete clinical workflow from registration to visit completion
     */
    ScenarioBuilder completeWorkflow = scenario("Complete Clinical Workflow")
        // Step 1: New patient registration - Create Owner
        .exec(OwnerScenario.createOwner)
        .pause(1, 3)
        
        // Step 2: Add pet to owner
        .exec(PetScenario.createPetForOwner)
        .pause(1, 2)
        
        // Step 3: Browse available vets and specialties
        .exec(VetScenario.getAllVets)
        .pause(1)
        .exec(VetScenario.getAllSpecialties)
        .pause(1, 2)
        
        // Step 4: Schedule visit for the pet
        .exec(VisitScenario.createVisitForPet)
        .pause(1, 3)
        
        // Step 5: Update visit with additional information
        .exec(VisitScenario.updateVisit)
        .pause(1, 2)
        
        // Step 6: Verify visit details
        .exec(VisitScenario.getVisitById)
        .pause(1);

    /**
     * Emergency workflow - immediate visit scheduling
     */
    ScenarioBuilder emergencyWorkflow = scenario("Emergency Visit Workflow")
        // Create owner and pet quickly
        .exec(OwnerScenario.createOwner)
        .pause(1)
        .exec(PetScenario.createPetForOwner)
        .pause(1)
        
        // Immediate emergency visit
        .exec(VisitScenario.createEmergencyVisit)
        .pause(1)
        .exec(VisitScenario.getVisitById);

    /**
     * Routine checkup workflow
     */
    ScenarioBuilder routineCheckupWorkflow = scenario("Routine Checkup Workflow")
        // Existing owner adds another pet
        .exec(OwnerScenario.createOwner)
        .pause(1)
        .exec(PetScenario.createPetForOwner)
        .pause(2)
        
        // Schedule routine checkup (2 weeks advance)
        .exec(VisitScenario.createRoutineCheckup)
        .pause(1)
        
        // Browse vets for the appointment
        .exec(VetScenario.getAllVets);

    /**
     * Administrative workflow - managing reference data
     */
    ScenarioBuilder adminWorkflow = scenario("Admin Reference Data Management")
        // Manage pet types
        .exec(PetScenario.getAllPetTypes)
        .pause(1)
        .exec(PetScenario.createPetType)
        .pause(1)
        .exec(PetScenario.updatePetType)
        .pause(2)
        
        // Manage specialties
        .exec(VetScenario.getAllSpecialties)
        .pause(1)
        .exec(VetScenario.createSpecialty)
        .pause(1)
        .exec(VetScenario.updateSpecialty)
        .pause(2)
        
        // Manage vets
        .exec(VetScenario.createVet)
        .pause(1)
        .exec(VetScenario.assignSpecialtyToVet);

    {
        setUp(
            // 10 users going through complete workflow
            completeWorkflow.injectOpen(
                rampUsers(10).during(30),
                constantUsersPerSec(2).during(120)
            ).protocols(httpProtocol),
            
            // 3 emergency cases
            emergencyWorkflow.injectOpen(
                nothingFor(30),
                rampUsers(3).during(20)
            ).protocols(httpProtocol),
            
            // 5 routine checkups
            routineCheckupWorkflow.injectOpen(
                nothingFor(60),
                rampUsers(5).during(30)
            ).protocols(httpProtocol),
            
            // 2 admin users managing reference data
            adminWorkflow.injectOpen(
                nothingFor(10),
                rampUsers(2).during(60)
            ).protocols(httpProtocol)
        )
        .protocols(httpProtocol)
        .assertions(
            global().responseTime().max().lt(5000),
            global().successfulRequests().percent().gt(95.0)
        );
    }
}