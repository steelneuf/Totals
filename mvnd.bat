@echo off
REM JVM tuning voor betere single-thread performance
set MAVEN_OPTS=-XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+UseStringDeduplication -Xms512m -Xmx1024m -XX:+UseCompressedOops -XX:+UseCompressedClassPointers
C:\Users\HP\maven-mvnd\bin\mvnd.cmd %*
