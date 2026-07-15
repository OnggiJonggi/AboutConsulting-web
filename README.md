# 어바웃컨설팅 웹 API

## 🛠 기술 스택

| 영역 | 기술 |
|------|------|
| 프론트 | Thymeleaf (+ Extras Spring Security 6), Bootstrap 5.3.3 |
| 백엔드 | Java 17, Spring Boot 4.0.7, Maven |
| DB | Oracle 26, MyBatis 4.0.1 (Spring Boot Starter) |

## 📄 API 문서

Swagger UI : http://localhost:8080/swagger-ui.html

전체 요청 경로 : doc/요청 주소록.txt

## 🗃️ DB

[ERD](https://www.erdcloud.com/d/Tr2KjN4ggfjBEgnSx)

doc/sql 경로 파일들 순서대로 실행

---

## ⚙️ 환경 설정

### `src/main/resources/config/key.properties`

```properties
# AES-128 평문 키 (16자리 암호화 키)
crypto.key=

# AWS S3
aws.credentials.accessKey=    # 엑세스 키
aws.credentials.secretKey=    # 시크릿 키
aws.s3.region=ap-northeast-2  # 리전
aws.s3.bucket=                # 버킷 이름

# (미사용) HMAC 키
# src/test/java/com/ax/tool/CreateHmacKey.java 실행 후 결과값 입력
hmac.key=

# 나이스 교육개방포털 API 키
# https://open.neis.go.kr/portal/data/service/selectServicePage.do
neis-data.key=

# (미사용) 공공데이터포털 API 키 (Encoding 인증키)
# https://www.data.go.kr/data/15107737/standard.do
public-data.key=

# 네이버 클로바-OCR API
# https://guide.ncloud-docs.com/docs/clovaocr-overview
ncloud-ocr.invoke-url=  # APIGW Invoke URL
ncloud-ocr.key=         # Secret Key

# OPEN AI
# https://platform.openai.com/docs/api-reference
openai.key=
```
