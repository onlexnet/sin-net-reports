# SinNet Reports

Build status Frontend
[![Build Status](https://dev.azure.com/siudeks/sinnetreports/_apis/build/status/sin-net-reports%20webapp?branchName=master)](https://dev.azure.com/siudeks/sinnetreports/_build/latest?definitionId=2&branchName=master)
Build status Backend
[![Build Status](https://dev.azure.com/onlex/sinnet/_apis/build/status/webapi?branchName=master)](https://dev.azure.com/onlex/sinnet/_build/latest?definitionId=1&branchName=master) 
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=net.siudek%3Asinnet-group&metric=coverage)](https://sonarcloud.io/dashboard?id=net.siudek%3Asinnet-group)

Small project created for [Sin.Net](http://www.sin.net.pl/)

Project site: [raport.sin.net.pl](https://raport.sin.net.pl/)

Project allows register customer services and produce summarized reports in form of small PDF attachments for invoices..

In scope:
- [x] Integrate login with Azure B2C
- [ ] Register time and distance of provided service through Web site
- [ ] Define list of clients
- [ ] Create PDF attachments per client for given period on demand

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
