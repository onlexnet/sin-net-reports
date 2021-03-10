# SinNet Reports

Build status Frontend
[![Build Status](https://dev.azure.com/siudeks/sinnetreports/_apis/build/status/sin-net-reports%20webapp?branchName=master)](https://dev.azure.com/siudeks/sinnetreports/_build/latest?definitionId=2&branchName=master)
Build status Backend
[![Build Status](https://dev.azure.com/siudeks/sinnetreports/_apis/build/status/sin-net-reports%20webapi?branchName=master)](https://dev.azure.com/siudeks/sinnetreports/_build/latest?definitionId=1&branchName=master) 
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=net.siudek%3Asinnet-group&metric=coverage)](https://sonarcloud.io/dashboard?id=net.siudek%3Asinnet-group)

Small project created for [Sin.Net](http://www.sin.net.pl/)

Project site: [raport.sin.net.pl](https://raport.sin.net.pl/)

Project allows register customer services and produce summarized reports in form of small PDF attachments for invoices..

In scope:
- [x] Integrate login with Azure B2C
- [x] Register time and distance of provided service through Web site
- [x] Define list of clients
- [x] Create PDF attachments per client for given period on demand

## Prepare dev environment
* Use WSL2
* Install VSCode
* Install java 11 using SDKMAN
* install npm using [nvm](https://github.com/nvm-sh/nvm)

## To run locally for tests

- cd webapi/main
- TBD
- goto **http://localhost:3000** to see UI
- goto **http://localhost:8080/graphiql** to see webapi

## Used articles
- https://devhub.io/repos/JrCs-docker-letsencrypt-nginx-proxy-companion
- https://blog.linuxserver.io/2017/11/28/how-to-setup-a-reverse-proxy-with-letsencrypt-ssl-for-all-your-docker-apps/
- https://www.digitalocean.com/community/tutorials/how-to-use-traefik-as-a-reverse-proxy-for-docker-containers-on-ubuntu-16-04
- https://github.com/OfficeDev/office-ui-fabric-react/wiki/Ngrok
- https://codewithhugo.com/docker-compose-local-https/
- https://itnext.io/cluster-recipe-external-proxy-for-kubernetes-ingress-or-docker-compose-ingress-with-haproxy-on-f81e3adee5ef
