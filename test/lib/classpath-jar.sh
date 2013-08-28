X=$(find lib -name '*.jar' | xargs printf ';%s')
export CLASSPATH="./test.jar$X"

