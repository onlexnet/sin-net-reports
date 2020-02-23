# SinNet Reports

[![Build Status](https://dev.azure.com/sinnetapps/sinnetapps/_apis/build/status/siudeks.sin-net-reports?branchName=master)](https://dev.azure.com/sinnetapps/sinnetapps/_build/latest?definitionId=1&branchName=master)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=net.siudek%3Asinnet-group&metric=coverage)](https://sonarcloud.io/dashboard?id=net.siudek%3Asinnet-group)

Small project created for [Sin.Net](http://www.sin.net.pl/)

Project allows register customer services and produce summarized reports.

In scope:

- Define list of clients
- Register time and distance of provided service through Web site
- Create PDF attachments per client for given period on demand

## To run locally for tests

- **docker-compose up**
- goto **http://localhost:3000** to see UI
- goto **http://localhost:8080/graphiql** to see webapi

## Used articles
- https://itnext.io/cluster-recipe-external-proxy-for-kubernetes-ingress-or-docker-compose-ingress-with-haproxy-on-f81e3adee5ef
