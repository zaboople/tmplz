X=$(find lib -name '*.jar' | xargs printf ';%s')
export CLASSPATH="./build/classes$X"

