# K6 Dashboard Quick Reference

## 📊 Two Ways to View K6 Metrics

### 1️⃣ Real-Time Dashboard (During Test) 
**URL:** http://localhost:5665  
**Status:** Only available WHILE test is running

```bash
# Terminal 1: Run test
cd scripts
TEST_SCRIPT=sample-script.js docker-compose -f docker-compose.stress.yml run --rm k6

# Terminal 2 or Browser: Open immediately
xdg-open http://localhost:5665
```

⚠️ **Important:** This dashboard disappears when the test finishes!

---

### 2️⃣ Exported HTML Dashboard (After Test) ⭐ **Recommended**
**URL:** http://localhost:5666/k6-dashboard  
**Status:** Available anytime after test completes

```bash
# Step 1: Run a K6 test
cd scripts
TEST_SCRIPT=sample-script.js docker-compose -f docker-compose.stress.yml run --rm k6

# Step 2: Start the report server
cd ../stress
./view-k6-report.sh

# Opens automatically: http://localhost:5666/k6-dashboard
```

**Or manually:**
```bash
cd scripts
docker-compose -f docker-compose.stress.yml up -d k6-report
xdg-open http://localhost:5666/k6-dashboard
```

---

## 🎯 Complete Workflow Example

```bash
# 1. Navigate to scripts directory
cd /path/to/ensurancePharmacy/scripts

# 2. Run K6 test
TEST_SCRIPT=load-test.js docker-compose -f docker-compose.stress.yml run --rm k6

# 3. View results (after test completes)
cd ../stress
./view-k6-report.sh

# 4. Browser opens automatically to: http://localhost:5666/k6-dashboard
```

---

## 🔧 Management Commands

### Start Report Server
```bash
cd scripts
docker-compose -f docker-compose.stress.yml up -d k6-report
```

### Stop Report Server
```bash
docker-compose -f docker-compose.stress.yml stop k6-report
```

### Check Status
```bash
docker ps | grep k6-report
```

### View Logs
```bash
docker logs ensurance-k6-report
```

---

## 📈 What You'll See in the Dashboard

- **Overview Tab:** Test summary, duration, VUs
- **Timeline:** Request rate over time
- **HTTP Metrics:** Response times, throughput
- **Checks:** Passed/failed assertions
- **Thresholds:** Performance criteria met/failed
- **Custom Metrics:** Any custom metrics defined

---

## ❓ Troubleshooting

### "Dashboard not found" or 404 error
**Cause:** Test hasn't been run yet or didn't complete successfully

**Solution:**
```bash
# Run a test first
cd scripts
TEST_SCRIPT=sample-script.js docker-compose -f docker-compose.stress.yml run --rm k6

# Then start report server
docker-compose -f docker-compose.stress.yml up -d k6-report
```

### Port 5666 already in use
**Solution:**
```bash
# Stop existing server
docker stop ensurance-k6-report

# Or change port in docker-compose.stress.yml:
# ports:
#   - "5667:5666"  # Changed from 5666:5666
```

### Can't access http://localhost:5665 after test
**This is normal!** The real-time dashboard (port 5665) only runs during the test.  
Use the exported dashboard at http://localhost:5666/k6-dashboard instead.

---

## 📚 Additional Resources

- **All K6 Scripts:** `/stress/k6/scripts/`
- **Full Documentation:** `/stress/STRESS_TESTING_GUIDE.md`
- **Examples:** `/stress/EXAMPLES.md`
- **Quick Commands:** `/COMANDOS_RAPIDOS.md`

---

## 🚀 Quick Test Commands

```bash
cd /path/to/ensurancePharmacy/scripts

# Quick test (30 seconds)
TEST_SCRIPT=sample-script.js docker-compose -f docker-compose.stress.yml run --rm k6

# Load test (5 minutes)
TEST_SCRIPT=load-test.js docker-compose -f docker-compose.stress.yml run --rm k6

# Stress test (8 minutes)
TEST_SCRIPT=stress-test.js docker-compose -f docker-compose.stress.yml run --rm k6

# View any test results
cd ../stress && ./view-k6-report.sh
```
