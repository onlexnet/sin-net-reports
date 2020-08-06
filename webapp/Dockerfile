FROM node:alpine as build
WORKDIR /app

COPY main/package.json package.json
COPY main/package-lock.json package-lock.json
RUN npm install

COPY main .
RUN npm run generate
RUN npm run build

FROM nginx:1.17 as target
WORKDIR /app
COPY --from=build /app/build /usr/share/nginx/html
EXPOSE 80