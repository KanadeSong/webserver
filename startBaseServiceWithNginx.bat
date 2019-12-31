cd /d %~dp0
cd service
cd redis
if not exist .data mkdir .data
cd redis
start /b redis-server redis.windows.conf
cd ../../
cd mysql64
if exist ".data" (
    echo start
    cd bin
    start /b mysqld --console
    echo end start
) else (
    mkdir .data
    cd bin
    call mysqld --initialize-insecure --user=mysql --console
    start /b mysqld --console
    call mysql -P 9763 -uroot < ../initdatabase.sql
    echo end install

)
cd ../../../
cd nginx
start nginx








