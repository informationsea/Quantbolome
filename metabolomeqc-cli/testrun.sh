#!/bin/sh

./build/install/metabolomeqc-cli/bin/metabolomeqc-cli -d 40 -o build/test-result.csv -s build/test-sample.csv  -v  -m ../metabolomeqc-correctors/src/test/resources/jp/ac/tohoku/ecei/sb/metabolomeqc/test/basiccorrector/flat-measure.csv ../metabolomeqc-correctors/src/test/resources/jp/ac/tohoku/ecei/sb/metabolomeqc/test/basiccorrector/dummy-mapping-*.csv
./build/install/metabolomeqc-cli/bin/metabolomeqc-cli -d 40 -o build/test-result-log.csv -s build/test-sample-log.csv  -v -c log -m ../metabolomeqc-correctors/src/test/resources/jp/ac/tohoku/ecei/sb/metabolomeqc/test/basiccorrector/flat-measure.csv ../metabolomeqc-correctors/src/test/resources/jp/ac/tohoku/ecei/sb/metabolomeqc/test/basiccorrector/dummy-mapping-*.csv
./build/install/metabolomeqc-cli/bin/metabolomeqc-cli -d 40 -o ./build/test.xlsx  -v  -m ../metabolomeqc-correctors/src/test/resources/jp/ac/tohoku/ecei/sb/metabolomeqc/test/basiccorrector/flat-measure.csv ../metabolomeqc-correctors/src/test/resources/jp/ac/tohoku/ecei/sb/metabolomeqc/test/basiccorrector/dummy-mapping-*.csv