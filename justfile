_default: test-show-only-failed

build *args:
    ./gradlew build {{args}}

[group('test')]
test *args:
    ./gradlew test {{args}}

[group('test')]
test-show-only-failed: (test "test" "-PtestLogEvents=failed")

[group('chore')]
clean-whitespace:
    sed 's/[ \t]*$//' lib/src/main/java/io/github/ahmadnull/dataserialization/* -i
    sed 's/[ \t]*$//' lib/src/test/java/io/github/ahmadnull/dataserialization/* -i

[group('setup')]
setup: gradle-wrapper

[group('setup')]
gradle-wrapper:
    gradle wrapper --gradle-version 9.2.1
