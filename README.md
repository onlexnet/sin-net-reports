# SinNet Reports

| Pipeline | Status |
|----------|--------|
| WebApp | [![Build Status](https://github.com/onlexnet/sin-net-reports/actions/workflows/static-webapp.yml/badge.svg)](https://github.com/onlexnet/sin-net-reports/actions/workflows/static-webapp.yml?query=branch:main) |
| WebApi | [![Build Status](https://dev.azure.com/onlex/sinnet/_apis/build/status/onlex-sinnet-webapi?branchName=master)](https://dev.azure.com/onlex/sinnet/_build/latest?definitionId=4&branchName=master) |
| Customers | [![Build Status](https://dev.azure.com/onlex/sinnet/_apis/build/status/onlex-sinnet-customers?branchName=master)](https://dev.azure.com/onlex/sinnet/_build/latest?definitionId=11&branchName=master) |
| Helm | [![Build Status](https://dev.azure.com/onlex/sinnet/_apis/build/status/onlex-sinnet-helm?branchName=master)](https://dev.azure.com/onlex/sinnet/_build/latest?definitionId=12&branchName=master) |
| Deploy| [![Build Status](https://dev.azure.com/onlex/sinnet/_apis/build/status/onlex-sinnet-deploy?branchName=master)](https://dev.azure.com/onlex/sinnet/_build/latest?definitionId=15&branchName=master) |


## Goal
- A small project created for [Sin.Net](http://www.sin.net.pl/)
- My testing sandbox for experimenting with various technologies

## Business
This project allows users to register service actions provided to customers and produce summarized reports in the form of small PDF attachments for invoices.

In scope:
- [x] Integrate login with Azure B2C
- [x] Register time and distance of provided services through the website
- [x] Define a list of clients
- [x] Create PDF attachments per client for a given period on demand

## Used articles
- [https://code.visualstudio.com/docs/devcontainers/create-dev-container]
- [How to export OpenTelemetry to Application Insight](https://docs.dapr.io/operations/monitoring/tracing/open-telemetry-collector-appinsights/)
- https://devhub.io/repos/JrCs-docker-letsencrypt-nginx-proxy-companion
- https://blog.linuxserver.io/2017/11/28/how-to-setup-a-reverse-proxy-with-letsencrypt-ssl-for-all-your-docker-apps/
- https://www.digitalocean.com/community/tutorials/how-to-use-traefik-as-a-reverse-proxy-for-docker-containers-on-ubuntu-16-04
- https://github.com/OfficeDev/office-ui-fabric-react/wiki/Ngrok
- https://codewithhugo.com/docker-compose-local-https/
- https://itnext.io/cluster-recipe-external-proxy-for-kubernetes-ingress-or-docker-compose-ingress-with-haproxy-on-f81e3adee5ef

