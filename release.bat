git log  --pretty=oneline | tail -n 10 >> log.txt
./gradlew clean
./gradlew assembleRelease
#./gradlew bundleRelease
firebase appdistribution:distribute app/build/outputs/apk/release/app-release.apk  \
--app 1:901780403692:android:f2fe057702958e3ffe3cc9  \
--release-notes-file ./log.txt --groups ViewGO