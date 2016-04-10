#!/bin/sh

echo precd
cd /Users/vkuzel/projects/core3/quantumleap/core
echo prejava
/Library/Java/JavaVirtualMachines/jdk1.8.0_66.jdk/Contents/Home/bin/java -Djava.security.manager=jarjar.org.gradle.process.internal.child.BootstrapSecurityManager -Dfile.encoding=UTF-8 -Duser.country=US -Duser.language=en -Duser.variant -ea -cp /Users/vkuzel/.gradle/caches/2.12-20160214094628+0000/workerMain/gradle-worker.jar jarjar.org.gradle.process.internal.launcher.GradleWorkerMain 'Gradle Test Executor 1'
