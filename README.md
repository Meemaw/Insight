# Insight

[![codecov](https://codecov.io/gh/Meemaw/insight/branch/master/graph/badge.svg)](https://codecov.io/gh/Meemaw/insight)
[![Open source: open-source](https://badges.frapsoft.com/os/v1/open-source.svg?v=103)](https://opensource.org/)

**Monorepo for Insight related services and applications.**

- [frontend](frontend) related code is managed by [Lerna](https://github.com/lerna/lerna) and [Yarn Workspaces](https://yarnpkg.com/lang/en/docs/workspaces/)
- [backend](backend) related code is managed by [Gradle](http://gradle.org/)
- [infrastructure](infrastructure) as a code is managed by [Terraform](https://www.terraform.io/)
- [CI/CD](.github/workflows) is managed using [Github Actions](https://github.com/features/actions)

## Development

**Clone the repo**

```sh
➜ git clone git@github.com:Meemaw/Insight.git
```

### Frontend

It is recommended to use VSCode for frontend development.

#### Local development

**Boostrap projects**

```sh
➜ yarn
```

**Run unit tests**

```sh
➜ yarn test
```

### Backend

It is recommended to use InteliJ IDEA for backend (Java) development.

#### Local development

Make sure you are in the [backend](backend) folder before executing gradle commands.

**Run unit tests**

```sh
➜ ./gradlew test --continue jacocoTestReport
```
