_default: test

build *args:
    ./gradlew build {{args}}

test *args:
    ./gradlew test {{args}}

setup: gradle-wrapper

gradle-wrapper:
    gradle wrapper --gradle-version 9.2.1

clean-whitespace:
    sed 's/[ \t]*$//' lib/src/main/java/io/github/ahmadnull/dataserialization/* -i
    sed 's/[ \t]*$//' lib/src/test/java/io/github/ahmadnull/dataserialization/* -i
