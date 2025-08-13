mvn clean test jacoco:report sonar:sonar \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=sqp_6ac9246579c658868005ccbf2d6afc073186fd48 \
  -Dsonar.projectKey=ensurance-pharmacy \
  -Dsonar.projectName=ensurance-pharmacy