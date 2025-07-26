# MyAuction API Server

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-blue.svg?style=flat-square" alt="Java 21"/>
  <img src="https://img.shields.io/badge/Spring%20Boot-3.5.4-green.svg?style=flat-square" alt="Spring Boot 3.5.4"/>
  <img src="https://img.shields.io/badge/JPA%2FHibernate-orange.svg?style=flat-square" alt="JPA/Hibernate"/>
  <img src="https://img.shields.io/badge/Docker-blue.svg?style=flat-square" alt="Docker"/>
  <img src="https://img.shields.io/badge/PostgreSQL-blue.svg?style=flat-square" alt="PostgreSQL"/>
</p>

## 1. Overview

- 고성능 실시간 경매를 위한 RESTful API 서버
- 도메인 주도 설계(DDD) 원칙 기반의 모듈형 아키텍처 적용
- Docker를 통한 일관된 개발 및 배포 환경 구성

## 2. Key Design Principles

- **Domain-Driven High Cohesion:** 각 기능(도메인)에 관련된 모든 코드를 단일 패키지 내에 배치하여 모듈의 응집도를 높이고, 타 모듈과의 결합도를 최소화합니다. 이는 유지보수성과 확장성을 극대화하며, 향후 마이크로서비스 전환에 유연하게 대처할 수 있는 기반이 됩니다.

## 3. Tech Stack

| Category            | Technologies & Libraries                       |
|---------------------|------------------------------------------------|
| **Core**            | `Java 21`, `Spring Boot 3.5.4`                   |
| **Persistence**     | `PostgreSQL`, `Spring Data JPA`, `Hibernate`     |
| **Security**        | `Spring Security` (BCrypt Password Encoder)    |
| **Validation**      | `Spring Boot Starter Validation`               |
| **Infrastructure**  | `Docker`, `Docker Compose`                     |
| **Build Tool**      | `Gradle`                                       |

## 4. API Endpoints

### Member Domain
- `POST /api/members/signup`: 신규 회원 가입 (Validation, Password Encryption, Unique Constraint)

*(추가 도메인 및 API 구현 시 업데이트)*