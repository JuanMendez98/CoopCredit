# Multi-stage Dockerfile for coopcredit-frontend
# Stage 1: Build (optional - just copy files)
FROM node:21-alpine AS builder
WORKDIR /app
COPY . .
# Simple validation - check if files exist
RUN test -f index.html && echo "Frontend files ready"

# Stage 2: Runtime
FROM node:21-alpine
WORKDIR /app
COPY --from=builder /app .
EXPOSE 3000
RUN npm install -g http-server
ENTRYPOINT ["http-server", "-p", "3000", "-c-1"]
