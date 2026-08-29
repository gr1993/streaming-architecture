# 인프라
kafak, flink 클러스터를 구성하는 프로젝트로 docker-compose를 통해 일괄적으로 실행시킬 수 있다.

* Flink UI : http://localhost:8081

```bash
docker compose up -d

# TaskManager를 2개로 늘림
docker compose up -d --scale taskmanager=2
```