DROP DATABASE surveylance_test;
CREATE DATABASE surveylance_test;
GRANT ALL PRIVILEGES ON surveylance_test.* TO 'surveylance'@'localhost' IDENTIFIED BY 'surveylance' WITH GRANT OPTION;

DROP DATABASE surveylance;
CREATE DATABASE surveylance;
GRANT ALL PRIVILEGES ON surveylance.* TO 'surveylance'@'localhost' IDENTIFIED BY 'surveylance' WITH GRANT OPTION;
