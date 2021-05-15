#!/bin/bash
# Copy all known local files used by the build and being outside of Docker build folder to local subfolder
# named .docker-externals so that later on Docker, having such files, will copy them to its original locations but
# located inside docker build process.
rm -rf .docker-externals
mkdir .docker-externals
cp -r ../uservice-reports/main/src/main/proto/ .docker-externals/proto/
# ln -s  ../uservice-reports/main/src/main/proto .docker-externals
