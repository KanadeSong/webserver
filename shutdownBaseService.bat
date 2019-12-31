cd /d %~dp0
cd service
cd mysql64/bin
mysqladmin -P 9763 -uroot -p123 shutdown
cd ../../
cd redis/redis
redis-cli -h 127.0.0.1 -p 9765  shutdown
cd ../../../








