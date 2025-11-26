# 21 Test Scenarios - Complete API Coverage

## Overview
This document lists all 21 test scenarios created for comprehensive PetClinic API testing, organized by business domain.

## Test Organization

### Structure
- **4 Scenario Files**: OwnerScenario, PetScenario, VetScenario, VisitScenario
- **2 Main Simulations**: FunctionalTestSuite, ClinicalWorkflowTest
- **Data**: 15,000 CSV records (5K owners, 5K pets, 5K vets)

---

## Owner Management (Scenarios 1-6)
**File**: `OwnerScenario.java`

| # | Scenario | Method | Endpoint | Description |
|---|----------|--------|----------|-------------|
| 1 | Get all owners | `getAllOwners` | GET /api/owners | Retrieve complete owner list |
| 2 | Create owner | `createOwner` | POST /api/owners | Register new owner with CSV data |
| 3 | Get owner by ID | `getOwnerById` | GET /api/owners/{id} | Retrieve specific owner details |
| 4 | Update owner | `updateOwner` | PUT /api/owners/{id} | Modify owner information |
| 5 | Delete owner | `deleteOwner` | DELETE /api/owners/{id} | Remove owner from system |
| 6 | Validation test | `createOwnerInvalidData` | POST /api/owners | Test with invalid data (400 expected) |

**Additional Methods**:
- `fullOwnerCRUD` - Complete CRUD workflow
- `searchOwnerByLastName` - Search by last name parameter

---

## Pet Management (Scenarios 7-12)
**File**: `PetScenario.java`

| # | Scenario | Method | Endpoint | Description |
|---|----------|--------|----------|-------------|
| 7 | Create pet for owner | `createPetForOwner` | POST /api/owners/{ownerId}/pets | Add new pet to owner |
| 8 | Get pet by ID | `getPetById` | GET /api/pets/{id} | Retrieve pet details |
| 9 | Update pet | `updatePet` | PUT /api/pets/{id} | Modify pet information |
| 10 | Delete pet | `deletePet` | DELETE /api/pets/{id} | Remove pet from system |
| 11 | Get all pet types | `getAllPetTypes` | GET /api/pettypes | List available pet types |
| 12 | Pet types CRUD | `createPetType`<br>`updatePetType`<br>`deletePetType` | POST/PUT/DELETE /api/pettypes | Admin: Manage pet type reference data |

**Additional Methods**:
- `fullPetCRUD` - Complete pet CRUD workflow
- `fullPetTypeCRUD` - Complete pet type admin workflow

---

## Vet Management (Scenarios 13-17)
**File**: `VetScenario.java`

| # | Scenario | Method | Endpoint | Description |
|---|----------|--------|----------|-------------|
| 13 | Get all vets | `getAllVets` | GET /api/vets | List all veterinarians |
| 14 | Create vet | `createVet` | POST /api/vets | Register new veterinarian |
| 15 | Assign specialty | `assignSpecialtyToVet` | PUT /api/vets/{id} | Link vet with specialty |
| 16 | Update/Delete vet | `updateVet`<br>`deleteVet` | PUT/DELETE /api/vets/{id} | Modify or remove vet |
| 17 | Specialties CRUD | `getAllSpecialties`<br>`createSpecialty`<br>`updateSpecialty`<br>`deleteSpecialty` | GET/POST/PUT/DELETE /api/specialties | Admin: Manage specialty reference data |

**Additional Methods**:
- `getVetById` - Retrieve vet by ID
- `fullVetCRUD` - Complete vet CRUD workflow
- `fullSpecialtyCRUD` - Complete specialty admin workflow

---

## Visit Management (Scenarios 18-21)
**File**: `VisitScenario.java`

| # | Scenario | Method | Endpoint | Description |
|---|----------|--------|----------|-------------|
| 18 | Create visit | `createVisitForPet` | POST /api/owners/{ownerId}/pets/{petId}/visits | **Critical**: Schedule pet visit |
| 19 | Get all visits | `getAllVisits` | GET /api/visits | List all scheduled visits |
| 20 | Update visit | `updateVisit` | PUT /api/visits/{id} | Modify visit details |
| 21 | Delete visit | `deleteVisit` | DELETE /api/visits/{id} | Cancel/remove visit |

**Additional Methods**:
- `getVisitById` - Retrieve visit by ID
- `createEmergencyVisit` - Emergency visit (immediate scheduling)
- `createRoutineCheckup` - Routine checkup (2 weeks advance)
- `fullVisitCRUD` - Complete visit CRUD workflow
- `completeClinicalWorkflow` - Owner → Pet → Visit chain

---

## Test Simulations

### 1. FunctionalTestSuite.java
**Purpose**: Sequential execution of all 21 scenarios for API validation

**Test Flow**:
```
1. Owner Management Tests (scenarios 1-6)
   ↓ Wait 60s
2. Pet Management Tests (scenarios 7-12)
   ↓ Wait 60s
3. Vet Management Tests (scenarios 13-17)
   ↓ Wait 60s
4. Visit Management Tests (scenarios 18-21)
```

**Configuration**:
- Users: 1 user per section (sequential)
- Duration: ~5 minutes total
- Purpose: Functional validation

### 2. ClinicalWorkflowTest.java
**Purpose**: End-to-end business process simulation

**Workflows**:
1. **Complete Workflow** (10 users)
   - Register owner → Add pet → Browse vets → Schedule visit → Update visit

2. **Emergency Workflow** (3 users)
   - Quick registration → Immediate emergency visit

3. **Routine Checkup** (5 users)
   - Register → Add pet → Schedule future checkup

4. **Admin Workflow** (2 users)
   - Manage pet types → Manage specialties → Manage vets

**Configuration**:
- Total Users: 20 concurrent
- Duration: 5 minutes
- Assertions: 
  - Response time max < 5000ms
  - Success rate > 95%

---

## Running Tests

### Run Functional Test Suite (All 21 Scenarios)
```powershell
mvn gatling:test -Dgatling.simulationClass=example.simulations.FunctionalTestSuite
```

### Run Clinical Workflow Test
```powershell
mvn gatling:test -Dgatling.simulationClass=example.simulations.ClinicalWorkflowTest
```

### Change Base URL
```powershell
mvn gatling:test -DBASE_URL=http://production-server:8080/petclinic `
  -Dgatling.simulationClass=example.simulations.FunctionalTestSuite
```

---

## Test Data

### CSV Files (src/test/resources/data/)
- **owners.csv**: 5,000 records (firstName, lastName, address, city, telephone)
- **pets.csv**: 5,000 records (name, birthDate, ownerId, typeId)
- **vets.csv**: 5,000 records (firstName, lastName)

### Data Generation
```powershell
# Regenerate test data
.\generate-owners.ps1
.\generate-pets.ps1
.\generate-vets.ps1
```

---

## Success Criteria

### Functional Tests
- ✅ All 21 scenarios execute without errors
- ✅ HTTP status codes match expectations (200, 201, 204, 400)
- ✅ Response bodies contain required fields
- ✅ Data correlation works (Owner → Pet → Visit chain)

### Performance Tests
- ✅ Response time < 5000ms (95th percentile)
- ✅ Success rate > 95%
- ✅ No server errors (5xx)
- ✅ Proper handling of concurrent requests

---

## Next Steps

1. **Run Functional Suite** to validate all 21 scenarios
2. **Run Clinical Workflow** to test realistic user journeys
3. **Analyze Reports** in `target/gatling/` directory
4. **Optimize** any failing scenarios or slow endpoints
5. **Scale Up** for load testing with more users

---

## Report Location
After test execution:
```
target/gatling/<simulation-name-timestamp>/index.html
```

Open in browser to view:
- Response time percentiles
- Success/failure rates
- Request distribution
- Detailed metrics per scenario
