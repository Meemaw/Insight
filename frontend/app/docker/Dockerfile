ARG auth_api_base_url=http://localhost:8080
ARG session_api_base_url=http://localhost:8082

FROM node:14-alpine as build

ENV AUTH_API_BASE_URL=$auth_api_base_url
ENV SESSION_API_BASE_URL=$session_api_base_url

COPY . /src
WORKDIR /src

RUN yarn install
RUN yarn workspace @insight/app build

FROM node:14-alpine

WORKDIR /usr/app

COPY --from=build /src/node_modules /usr/app/node_modules
COPY --from=build /src/frontend/app/package.json /usr/app/package.json
COPY --from=build /src/frontend/app/.next /usr/app/.next

ENV NODE_ENV production
ENV AUTH_API_BASE_URL=$auth_api_base_url
ENV SESSION_API_BASE_URL=$session_api_base_url

CMD ["npm", "start"]
