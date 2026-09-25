# Etapa 1: Compilar o Angular
FROM node:20-alpine AS build
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci
COPY . .
RUN npm run build

# Etapa 2: Servir com Nginx
FROM nginx:alpine
# Copia os arquivos compilados para a pasta padrão do Nginx
# (Angular 17+ usa a pasta 'browser' dentro de dist)
COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html
EXPOSE 80
