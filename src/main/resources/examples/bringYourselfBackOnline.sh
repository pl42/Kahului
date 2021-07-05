#!/bin/sh

# Kill the current running version of Kahului
curl http://www.peggy42.cn:17071/seppuku?pass={SECRET_HASH256}

# Remove old Kahului logs
rm nohup.out
rm logs/*.log*

# Update the binance-java-api package
cd binance-java-api
git pull
mvn clean install
# mvn clean install -DskipTests
cd ..

# Update Kahului's code and rebuild
cd kahului
git pull
mvn clean install
cd ..

# Print out updated line count for Kahului's code
cloc kahului

# Start new Kahului instance so even Control+C doesn't kill the running instance
nohup java -jar kahului/target/kahului*.jar <arg1> <..> <arg6> &

# USEFULL COMMANDS
# =======================================
# ps aux | grep -i kahului   # This command finds a running instance of Kahului
# sudo kill -9 {PID}       # Kills running Kahului instance
