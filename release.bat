git log --oneline -5 > log.txt
./gradlew clean
./gradlew assembleRelease
#./gradlew bundleRelease
firebase appdistribution:distribute app/build/outputs/apk/release/app-release.apk  \
--app 1:901780403692:android:f2fe057702958e3ffe3cc9  \
--release-notes-file ./log.txt --groups ViewGO