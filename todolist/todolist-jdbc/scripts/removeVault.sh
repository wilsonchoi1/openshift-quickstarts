#!/bin/sh

while true
do
   sleep 10
   echo "start to checking readiness"
   curl --noproxy '*' -s -u dQjQX35g:5TsOmvm0 'http://localhost:8080/manager/jmxproxy/?get=Catalina%3Atype%3DServer&att=stateName' |grep -iq 'stateName *= *STARTED'
   if [ $? == 0 ]
   then
     break;
   fi
done
rm /opt/webserver/tomcat-vault/VAULT.dat
echo "finish to remove vault"
exit(1)
