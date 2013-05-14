REM ========================================
REM = Project Executor on Windows Platform =
REM = todo - to use JAVA_HOME environment  =
REM ========================================

javac -d out -sourcepath src src\demo\Main.java
java -classpath out demo.Main
