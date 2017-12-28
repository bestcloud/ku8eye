### Target: upgrade kubernetes-client version from v1.3.63 to v3.0.3 to support Kubernetes 1.7+

environment: eclipse

1. modify /ku8eye-web/pom.xml line 70: version

2. Update maven. Execute Maven --> Update Project

3. modify specific lines and correct errors 

   We made the older code as comments to help the further downgrade to support older versions

    

   [Path to file **K8sAPIService.java**](https://github.com/bestcloud/ku8eye/blob/master/src/ku8eye-web/src/main/java/org/ku8eye/service/k8s/K8sAPIService.java)

