mvn test -Dkrati.test.jvm.args="-Xloggc:target/logs/krati.gc -XX:+PrintGCDetails" -Dkrati.test.idCount=100000 -Dkrati.test.keyCount=100000 -Dkrati.test.segFileSizeMB=64 -Dkrati.test.runTimeSeconds=300 -Dkrati.test.initLevel=4