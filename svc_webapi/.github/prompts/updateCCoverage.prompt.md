---
name: updateCCoverage
description: Update code coverage values based on JaCoCo report
---
- measure code coverage in host module using JaCoCo report:
- set values of INSTRUCTION, BRANCH, LINE to 1.00, and CLASS to 0 in pom.xml
- build project usin mvn build -pl host -am
- read jacoco complains about invalid values
- use jacoco real values for INSTRUCTION, BRANCH, LINE using double precision values, and CLASS using an integer
- build project once again to be sure it pass JaCoCo requirements with updated values
