[![CI](https://github.com/Romanow/gateway-lecture/actions/workflows/build.yml/badge.svg?branch=master)](https://github.com/Romanow/gateway-lecture/actions/workflows/build.yml)
[![pre-commit](https://img.shields.io/badge/pre--commit-enabled-brightgreen?logo=pre-commit)](https://github.com/pre-commit/pre-commit)
[![Release](https://img.shields.io/github/v/release/Romanow/gateway-lecture?logo=github&sort=semver)](https://github.com/Romanow/gateway-lecture/releases/latest)
[![Echo Server](https://img.shields.io/docker/pulls/romanowalex/gateway?logo=docker)](https://hub.docker.com/r/romanowalex/gateway)
[![License](https://img.shields.io/github/license/Romanow/gateway-lecture)](https://github.com/Romanow/gateway-lecture/blob/master/LICENSE)

# Gateway

GitHub: [romanow/gateway-lecture](https://github.com/Romanow/gateway-lecture).

## Локальный запуск

Используем [docker-compose.yml](docker-compose.yml)

```shell
$ docker compose up -d --wait
$ curl http://localhost:8000/dict/v1/lego-sets
> [{ ... }]
```
