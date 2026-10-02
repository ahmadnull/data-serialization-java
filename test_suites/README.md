# Test Suites

Shared conformance corpora used by the tests of every module. They are copied
onto the test classpath by `lib/build.gradle.kts`.

## json

### json/test_parsing

Verbatim copy of [JSONTestSuite](https://github.com/nst/JSONTestSuite) (MIT),
the compliance suite for [RFC 8259](https://www.rfc-editor.org/rfc/rfc8259)
parsers, which was published as an appendix to
[Parsing JSON is a Minefield](http://seriot.ch/parsing_json.php).

The prefix of every file states the outcome a compliant parser has to produce:

| Prefix | Meaning                                    |
| ------ | ------------------------------------------ |
| `y_`   | content must be accepted                   |
| `n_`   | content must be rejected                   |
| `i_`   | parsers are free to accept or reject       |

Consumed by `TestJsonTestSuite`.

### json/test_transform

Same origin, contains values parsers may interpret differently, such as huge
numbers or escaped invalid codepoints. There is no expected outcome, it is
only meant to observe and compare parser behaviour.