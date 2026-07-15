--------------------------- 로컬 ---------------------------
-- 데이터스페이스 생성
CREATE TABLESPACE ABOUTCONSULTING
DATAFILE 'aboutconsulting_data01.dbf'
SIZE 100M
AUTOEXTEND ON NEXT 10M MAXSIZE UNLIMITED;

--------------------------- RDS ---------------------------
CREATE TABLESPACE ABOUTCONSULTING;

--------------------------- 계정 생성 ---------------------------
-- DDL 계정 생성 / 권한 부여
CREATE USER ABOUTCONSULTING IDENTIFIED BY ABOUTCONSULTING
DEFAULT TABLESPACE ABOUTCONSULTING
QUOTA UNLIMITED ON ABOUTCONSULTING;
GRANT CONNECT, RESOURCE TO ABOUTCONSULTING;

--------------------------- 계정 삭제 ---------------------------
-- 계정 날리기
DROP USER ABOUTCONSULTING CASCADE;
DROP USER AXC_DML CASCADE;
-- 데이터스페이스 날리기
DROP TABLESPACE TREELINK INCLUDING CONTENTS AND DATAFILES;