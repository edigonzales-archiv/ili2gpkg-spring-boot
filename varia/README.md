mvn clean install (mvn package)

sudo ln -s  /home/stefan/Apps/ili2gpkg-spring-boot/ili2gpkg.jar /etc/init.d/ili2gpkg

sudo update-rc.d myapp defaults

/var/run/ili2gpkg/ili2gpkg.pid
/var/log/ili2gpkg.log

 ProxyPass /ili2gpkg http://85.25.185.233:8886/ili2gpkg
 ProxyPassReverse /ili2gpkg http://85.25.185.233:8886/ili2gpkg

