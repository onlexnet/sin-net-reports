#!/bin/bash
# Copy all known local files used by the build and being outside of Docker build folder to local subfolder
# named .docker-externals so that later on Docker, having such files, will copy them to its original locations but
# located inside docker build process.

rm -rf .links
mkdir .links

ln -s $(realpath ../uservice-reports/main/src/main/proto) .links/uservice-reports-proto

ln -s $(realpath api.grpc) main/grpc-server/src/main/proto
