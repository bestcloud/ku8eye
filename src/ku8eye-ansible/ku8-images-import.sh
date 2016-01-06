FILE=$1
gunzip FILE
FILE=${FILE:0:${#FILE}-3} 
cd $FILE
PATH=pwd
java -jar /root/ku8eye-web.jar org.ku8eye.App image -p $PATH
