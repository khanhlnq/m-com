---------- Enroll Service Full Package ---------------
    1. If you want to use default settings, just copy dist/enrollfull.war 
to webapps directory of Java Web Service Developer Pack (JWSDP)
    2. If you want to rebuild this package. Remember to set environment variables correctly
such as: JAVA_HOME, JWSDP_HOME, ANT_HOME, CLASSPATH, PATH
    Modify build.properties according to your system, pay attention to following properties:
jwsdp.home, url, username, password (username, password of Tomcat manager), context-path, war-path,
portable-war, deployable-war
    Modify web.xml if you want
    Note: Init parameters for proxy servlet can be modified in web.xml
    Init parameters for enroll service, can only be modified in source code
    
    Run command "ant build" to build package
    Run command "ant deploy" to deploy package
    Good luck!