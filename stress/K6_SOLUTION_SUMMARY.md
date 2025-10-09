# K6 Dashboard Solution Summary

## 🎯 Problem Solved

**Issue:** After running k6 test with `docker compose`, the container exits with code 0 (success), but the dashboard at `http://localhost:5665` is no longer accessible.

**Root Cause:** The k6 web dashboard (`--out web-dashboard`) only runs **during** the test execution. When the test completes and the container exits, the dashboard server stops.

**Solution:** Added a persistent `k6-report` service that serves the exported HTML dashboard after the test completes.

---

## ✅ What Was Added

### 1. New Docker Service: `k6-report`
Located in: `/scripts/docker-compose.stress.yml`

```yaml
k6-report:
  image: python:3.9-alpine
  container_name: ensurance-k6-report
  working_dir: /results
  command: python -m http.server 5666
  volumes:
    - k6_results:/results
  ports:
    - "5666:5666"
  restart: unless-stopped
```

This service:
- ✅ Serves the exported k6 HTML dashboard
- ✅ Stays running after tests complete
- ✅ Accessible at `http://localhost:5666/k6-dashboard`
- ✅ Similar to the existing `jmeter-report` service

### 2. Helper Script: `view-k6-report.sh`
Located in: `/stress/view-k6-report.sh`

Automatically:
- Checks if k6 results exist
- Starts the report server
- Opens the dashboard in your browser

### 3. Documentation Updates
- `/COMANDOS_RAPIDOS.md` - Quick commands for viewing k6 results
- `/stress/K6_DASHBOARD_QUICKSTART.md` - Complete guide
- `/stress/K6_SOLUTION_SUMMARY.md` - This file

---

## 🚀 How to Use

### Method 1: Helper Script (Recommended)

```bash
# 1. Run a k6 test
cd scripts
TEST_SCRIPT=sample-script.js docker compose -f docker-compose.stress.yml run --rm k6

# 2. View the dashboard
cd ../stress
./view-k6-report.sh

# Browser opens automatically to: http://localhost:5666/k6-dashboard
```

### Method 2: Manual

```bash
# 1. Run a k6 test
cd scripts
TEST_SCRIPT=sample-script.js docker compose -f docker-compose.stress.yml run --rm k6

# 2. Start report server
docker compose -f docker-compose.stress.yml up -d k6-report

# 3. Open in browser
xdg-open http://localhost:5666/k6-dashboard
```

---

## 📊 Two Dashboard Types

### Type 1: Real-Time Dashboard (During Test)
- **URL:** `http://localhost:5665`
- **When:** Only while test is running
- **Use:** Watch metrics live as test executes
- **Limitation:** Disappears when test completes

**How to use:**
```bash
# Terminal 1: Start test
cd scripts
TEST_SCRIPT=load-test.js docker compose -f docker-compose.stress.yml run --rm k6

# Terminal 2: Immediately open browser
xdg-open http://localhost:5665
```

### Type 2: Exported Dashboard (After Test) ⭐ **Recommended**
- **URL:** `http://localhost:5666/k6-dashboard`
- **When:** Available anytime after test completes
- **Use:** Review results at your convenience
- **Benefit:** Persistent, can be viewed multiple times

**How to use:**
```bash
# After any k6 test completes
cd stress
./view-k6-report.sh
```

---

## 🎓 Understanding the Flow

```
┌─────────────────────────────────────────────────────────────┐
│ DURING TEST                                                  │
│                                                              │
│  ┌──────────────┐                                           │
│  │ K6 Container │                                           │
│  │   Running    │ ──► http://localhost:5665 (Live)        │
│  └──────────────┘                                           │
│         │                                                    │
│         └──► Exports: /results/k6-dashboard (HTML file)    │
│                                                              │
└─────────────────────────────────────────────────────────────┘

                    ⬇ Test completes

┌─────────────────────────────────────────────────────────────┐
│ AFTER TEST                                                   │
│                                                              │
│  ┌──────────────┐    ❌ http://localhost:5665 (Gone)       │
│  │ K6 Container │                                           │
│  │   Exited     │                                           │
│  └──────────────┘                                           │
│                                                              │
│  ┌────────────────┐                                         │
│  │ k6-report      │                                         │
│  │ (HTTP Server)  │ ──► http://localhost:5666/k6-dashboard │
│  │   Running      │     ✅ Available anytime               │
│  └────────────────┘                                         │
│         │                                                    │
│         └──► Serves: /results/k6-dashboard                 │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

---

## 📝 Example Workflow

```bash
# Full example from start to finish
cd /home/pablopolis2016/Documents/ensurancePharmacy

# 1. Run k6 load test
cd scripts
TEST_SCRIPT=load-test.js docker compose -f docker-compose.stress.yml run --rm k6

# Test runs... shows output... completes with exit code 0
# ✅ All checks passed
# ✅ Dashboard exported to: /results/k6-dashboard

# 2. View results
cd ../stress
./view-k6-report.sh

# Output:
# ✅ K6 dashboard found!
# 🚀 Starting K6 report server...
# ✅ Server started!
# 📊 K6 Dashboard Available:
#   🌐 Dashboard: http://localhost:5666/k6-dashboard
#   📁 All Files: http://localhost:5666
# Opening dashboard in browser...

# Browser opens showing your k6 test results!
```

---

## 🔧 Management Commands

### Check if server is running
```bash
docker ps | grep k6-report
```

### Start server manually
```bash
cd scripts
docker compose -f docker-compose.stress.yml up -d k6-report
```

### Stop server
```bash
docker compose -f docker-compose.stress.yml stop k6-report
```

### View server logs
```bash
docker logs ensurance-k6-report
```

### Remove server
```bash
docker compose -f docker-compose.stress.yml down k6-report
```

---

## 🎯 Key Files

| File | Purpose |
|------|---------|
| `/scripts/docker-compose.stress.yml` | Contains k6-report service definition |
| `/stress/view-k6-report.sh` | Helper script to start server and open dashboard |
| `/stress/K6_DASHBOARD_QUICKSTART.md` | Quick reference guide |
| `/COMANDOS_RAPIDOS.md` | Quick commands reference |
| `scripts_k6_results` (Docker volume) | Stores k6 test results |

---

## 📊 What You'll See in the Dashboard

- **Test Summary**: Duration, VUs, iterations
- **Metrics Timeline**: Request rate, response time over time
- **HTTP Metrics**: Response times (p50, p90, p95, p99)
- **Throughput**: Requests per second
- **Checks**: Pass/fail status of assertions
- **Errors**: Any failed requests
- **Custom Metrics**: If defined in your test

---

## ✅ Benefits of This Solution

1. **Persistent Access**: View results anytime, even days after the test
2. **Multiple Views**: Run multiple tests and compare results
3. **Simple**: Easy one-command access via helper script
4. **Consistent**: Same pattern as JMeter report server
5. **No Data Loss**: Results preserved in Docker volume

---

## 🆘 Troubleshooting

### Issue: "No K6 results found"
**Solution:** Run a k6 test first
```bash
cd scripts
TEST_SCRIPT=sample-script.js docker compose -f docker-compose.stress.yml run --rm k6
```

### Issue: "K6 dashboard not found"
**Cause:** Test didn't complete successfully or dashboard export failed

**Solution:**
1. Check if test completed: `docker logs ensurance-k6`
2. Verify results volume: `docker volume ls | grep k6`
3. Check volume contents: `docker run --rm -v scripts_k6_results:/results alpine ls -lh /results`

### Issue: Port 5666 already in use
**Solution:** Stop the existing server
```bash
docker stop ensurance-k6-report
```

### Issue: 404 Not Found when accessing dashboard
**Check:** Make sure you're using the correct URL: `http://localhost:5666/k6-dashboard` (with `/k6-dashboard`)

---

## 🎉 Success!

Your k6 testing workflow is now complete:
- ✅ Run tests easily
- ✅ View real-time metrics during test
- ✅ Access exported dashboard after test
- ✅ Keep results for later analysis

Happy testing! 🚀
