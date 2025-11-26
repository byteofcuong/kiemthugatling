# Quick Start Guide - 21 Scenarios Testing

## Prerequisites Check
```powershell
# 1. Verify Docker containers are running
docker ps

# Expected output:
# - petclinic-app (port 9966)
# - mysql (port 3306)

# 2. Test API accessibility
curl http://localhost:9966/petclinic/api/vets
```

---

## Running All 21 Scenarios (Functional Suite)

### Single Command
```powershell
cd C:\Users\PC\Documents\GitHub\kiemthugatling
mvn gatling:test -Dgatling.simulationClass=example.simulations.FunctionalTestSuite
```

### What This Tests
✅ **Owner Management** (6 scenarios)
- Get all, Create, Read, Update, Delete, Validation

✅ **Pet Management** (6 scenarios)  
- Create, Read, Update, Delete, Pet Types CRUD

✅ **Vet Management** (5 scenarios)
- Get all, Create, Assign Specialties, Update/Delete, Specialties CRUD

✅ **Visit Management** (4 scenarios)
- Create, List, Update, Delete

### Expected Duration
~5 minutes (sequential execution)

---

## Running Business Workflow Test

### Command
```powershell
mvn gatling:test -Dgatling.simulationClass=example.simulations.ClinicalWorkflowTest
```

### What This Tests
- 10 users: Complete clinical workflow (Owner→Pet→Visit)
- 3 users: Emergency visit workflow
- 5 users: Routine checkup workflow
- 2 users: Admin reference data management

### Expected Duration
~5 minutes (20 concurrent users)

---

## View Test Reports

### After Test Completes
```powershell
# Report location printed at end of test output
# Example: target/gatling/functionaltestsuite-20251126210952247/index.html

# Open automatically
start target/gatling/functionaltestsuite-*/index.html
```

### What to Check in Report
- **Global Stats**: Success rate should be > 95%
- **Response Time Distribution**: Most requests < 1000ms
- **Scenario Details**: All 21 scenarios completed
- **Error Details**: Should be minimal or none

---

## Troubleshooting

### Problem: Connection Refused
```powershell
# Solution: Start Docker containers
cd C:\Users\PC\Documents\GitHub\petclinics_btl
docker-compose up -d

# Wait 30 seconds for app to start
timeout 30

# Verify app is ready
curl http://localhost:9966/petclinic/api/vets
```

### Problem: Test Data Not Found
```powershell
# Solution: Regenerate CSV files
cd C:\Users\PC\Documents\GitHub\kiemthugatling
.\generate-owners.ps1
.\generate-pets.ps1
.\generate-vets.ps1
```

### Problem: Compilation Errors
```powershell
# Solution: Clean and recompile
mvn clean compile test-compile
```

### Problem: Previous Test Data Conflicts
```powershell
# Solution: Reset database
cd C:\Users\PC\Documents\GitHub\petclinics_btl
docker-compose down -v
docker-compose up -d

# Wait for MySQL to initialize
timeout 60
```

---

## Quick Test (Smoke Test)

### Verify System is Ready
```powershell
# Test one scenario only (Owner Get All)
mvn gatling:test -Dgatling.simulationClass=example.simulations.FunctionalTestSuite `
  -Dgatling.noReports=true
```

If this passes, system is ready for full test suite.

---

## Test Results Location

### Reports
```
kiemthugatling/
  target/
    gatling/
      functionaltestsuite-<timestamp>/
        index.html          ← Main report
        req_*.html          ← Individual request stats
        js/
        style/
```

### Best Practices
1. ✅ Run functional suite first (validates all 21 scenarios)
2. ✅ Review report for failures before workflow test
3. ✅ Keep Docker containers running between tests
4. ✅ Check MySQL has enough data (5000+ owners/pets/vets)

---

## Performance Baselines

### Expected Response Times (p95)
- GET operations: < 200ms
- POST operations: < 500ms
- PUT operations: < 400ms
- DELETE operations: < 300ms

### Expected Success Rates
- All CRUD operations: 100%
- Validation tests: 100% (expecting 400 status)

---

## Advanced Options

### Run Specific Scenario
Edit simulation file to comment out unwanted scenarios.

### Change Load Profile
Edit `ClinicalWorkflowTest.java`:
```java
// Change from 10 to 50 users
rampUsers(50).during(30)
```

### Test Against Different Environment
```powershell
mvn gatling:test `
  -DBASE_URL=http://production:8080/petclinic `
  -Dgatling.simulationClass=example.simulations.FunctionalTestSuite
```

---

## Success Indicators

### ✅ Test Passed When
- Console shows "BUILD SUCCESS"
- Report shows 0 KO (failures)
- All 21 scenarios executed
- Response times within baseline

### ❌ Test Failed When
- BUILD FAILURE in Maven output
- Report shows KO > 0
- Connection errors in logs
- Timeout errors

---

## Files Created

### Scenario Files (src/test/java/example/scenarios/)
1. `OwnerScenario.java` - 8 methods (scenarios 1-6)
2. `PetScenario.java` - 9 methods (scenarios 7-12)
3. `VetScenario.java` - 11 methods (scenarios 13-17)
4. `VisitScenario.java` - 10 methods (scenarios 18-21)

### Simulation Files (src/test/java/example/simulations/)
1. `FunctionalTestSuite.java` - Sequential validation
2. `ClinicalWorkflowTest.java` - Concurrent workflows

### Supporting Files
1. `BaseSimulation.java` - Base class with HTTP config
2. `Constants.java` - Session keys and paths
3. Data files: owners.csv, pets.csv, vets.csv

---

## Next Steps After Successful Run

1. 📊 **Analyze Report** - Check response times and success rates
2. 🔧 **Tune Parameters** - Adjust load if needed
3. 📈 **Baseline Metrics** - Record current performance
4. 🚀 **Scale Testing** - Increase users for stress test
5. 📝 **Document Results** - Save report for comparison
