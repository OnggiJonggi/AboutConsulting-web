어바웃컨설팅 웹 API


----------------------------------------------------
프론트 : thymeleaf(+ extras spring security6), bootstrap 5.3.3

웹 : java 17, spring boot 4.0.6, maven

db : oracle 26, mybatis 4.0.1(spring boot starter)

스윀어 http://localhost:8080/swagger-ui.html

----------------------------------------------------
src/main/resources/config/key.properties


ABS-128 평문 키

crypto.key= #16글자 암호화 키

HMAC 키

hmac.key= # src\test\java\com\ax\tool\CreateHmacKey.java 실행 결과


나이스 교육개방포털 API 키

https://open.neis.go.kr/portal/data/service/selectServicePage.do

nies-data.key= (인증키)


공공데이터 API키

https://www.data.go.kr/data/15107737/standard.do

public-data.key= (Encoding 인증키)