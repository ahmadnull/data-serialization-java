_default: (test "-i")

build *args:
    ./gradlew build {{args}}

test *args:
    ./gradlew test {{args}}

setup: gradle-wrapper

gradle-wrapper:
    gradle wrapper --gradle-version 9.2.1
