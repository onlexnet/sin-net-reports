We have 3 groups of environments based on kubernetes
1) PROD environments (like PRD, STG) which are deployed to microk8s, with database on hosteg POstgreSQL instance
2) NON-PROD environments (like DEV, SIT) which are deployed to microk8s, with database run in the sme namespace as pods
3) LOCAL environment hosted on minikube, with some local modifications
    - 