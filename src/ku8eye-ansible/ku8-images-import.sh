FILE=$1
CLUSTERID=$2
if [ "$FILE" = "" ];then
 echo "miss param"
 echo "example: ./ku8-images-impoort.sh /ku8_ext_files/ku8-images/ku8-images.tar.gz"
 echo "exit"
 exit
fi
if [ "$CLUSTERID" = "" ];then
 java -jar /root/ku8eye-web.jar org.ku8eye.App image -f $FILE 
else
 java -jar /root/ku8eye-web.jar org.ku8eye.App image -f $FILE -c $CLUSTERID 
fi
  