FROM node:12

WORKDIR /tmp
COPY package.json .
RUN yarn install

WORKDIR /usr/src/app
RUN mv /tmp/node_modules .

COPY . .
CMD yarn start

EXPOSE 3000