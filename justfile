_default: test-show-only-failed

[group('git')]
[arg('type', pattern='feat|fix|refactor|perf|style|test|docs|build|ops|chore')]
[arg('scope', short='s', long='scope')]
[arg('breaking', short='B', long='breaking', value='1')]
[arg('body', short='b', long='body')]
[arg('footer', short='f', long='footer')]
commit type description scope='' breaking='0' body='' footer='': format add-all
    #!/bin/env sh
    if [ -n "{{scope}}" ]; then scope="({{scope}})"; fi
    if [ "{{breaking}}" -eq "1" ]; then breaking="!"; fi
    git commit -m "{{type}}${scope}${breaking}: {{description}}" -m "{{body}}" -m "{{footer}}"

[group('git')]
add-all:
    git add -A

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

[group('chore')]
format:
    ./gradlew spotlessApply

[group('setup')]
setup: gradle-wrapper

[group('setup')]
gradle-wrapper:
    gradle wrapper --gradle-version 9.2.1
