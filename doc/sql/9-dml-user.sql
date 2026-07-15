-- 시스템 계정에서 전체 실행

-- 조회용 계정 생성
CREATE USER AXC_DML IDENTIFIED BY AXC_DML;
GRANT CREATE SESSION TO AXC_DML;

-- ABOUTCONSULTING 테이블 DML 권한 부여
BEGIN
  FOR table_rec IN (SELECT table_name FROM all_tables WHERE owner = 'ABOUTCONSULTING') LOOP
    EXECUTE IMMEDIATE 'GRANT SELECT, INSERT, UPDATE, DELETE ON ABOUTCONSULTING.' 
      || table_rec.table_name || ' TO AXC_DML';
  END LOOP;
END;
/

-- AXC_DML 시노님 생성
BEGIN
  FOR tbl IN (SELECT table_name FROM all_tables WHERE owner = 'ABOUTCONSULTING') LOOP
    BEGIN
      EXECUTE IMMEDIATE 'CREATE SYNONYM AXC_DML.' || tbl.table_name 
        || ' FOR ABOUTCONSULTING.' || tbl.table_name;
    EXCEPTION
      WHEN OTHERS THEN
        NULL;
    END;
  END LOOP;
END;
/