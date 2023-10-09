# jdbstart

This is just an training project in order to get familiar with
JDBC drivers (especially the postgreSQL one.

## Goals

1. Create a utility function which will take as an input a lambda function and then it will
open a DB connection and execute an SQL query. You should implement the proper error handling
and rollback mechanism, and the proper connection opening and closing as well.

2. If this is completed, then modify the application in order to support nested transactions with the
same behavior as the transactional annotation of the spring framework. 

**Note**: Connection pool should be used in order to test the auto-commit of the JDBC driver.
