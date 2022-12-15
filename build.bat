call mvn clean package

if exist %cd%\NovaCore-1_17-Plus.jar del %cd%\NovaCore-1_17-Plus.jar

copy %cd%\NovaCore-1_17-Plus\target\NovaCore-1_17-Plus-1.0.0-SNAPSHOT.jar %cd%\NovaCore-1_17-Plus.jar

timeout /T 5