# Multi-stage build for static-webapp
# Stage 1: Build the React application
FROM node:22.12.0-bookworm AS build

WORKDIR /app

# Copy GraphQL schema files from webapi (needed for codegen)
COPY uservice-webapi/host/src/main/resources/graphql uservice-webapi/host/src/main/resources/graphql

# Copy package files
COPY static-webapp/package*.json ./
COPY static-webapp/.version ./

# Install dependencies
RUN --mount=type=cache,target=/root/.npm \
    npm ci

# Copy source code and config files
COPY static-webapp/graphql-codegen-config.yml ./
COPY static-webapp/tsconfig.json ./
COPY static-webapp/public ./public
COPY static-webapp/src ./src

# Generate GraphQL models and build the app
RUN npm run generate && \
    export MY_VERSION=$(cat .version) && \
    npm config set allow-same-version true && \
    npm version $MY_VERSION && \
    npm run build

# Stage 2: Serve with nginx
FROM nginx:1.25-alpine

WORKDIR /usr/share/nginx/html

# Remove default nginx static assets
RUN rm -rf ./*

# Copy built React app from build stage
COPY --from=build /app/build .

# Copy custom nginx configuration
COPY static-webapp/nginx.conf /etc/nginx/conf.d/default.conf

# Expose port 80
EXPOSE 80

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
    CMD wget --quiet --tries=1 --spider http://localhost/ || exit 1

# Start nginx
CMD ["nginx", "-g", "daemon off;"]
