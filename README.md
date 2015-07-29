# json4s AST

Two minimal implementations of a [JSON](https://en.wikipedia.org/wiki/JSON) `AST`, one that is designed for
performance and another that is designed for correctness/purity.

## Common Goals between both AST's
- [Scala.js](https://github.com/scala-js/scala-js) support, allowing the possibility of 
libraries to seamlessly work with `JValue` on `Javascript` clients as well as the `JVM`
- Strictly zero dependencies
- Strictly one release per major `Scala`/`Scala.js` release. Ideally, `json4s-ast` should only update when a new major 
version for `Scala`/`Scala.js` is released. There may be exceptions to this (i.e. `Scala` has in the past accidentally
brought in breaking changes in minor releases)
- High emphasis on binary compatibility (use of `sealed abstract class` in top level `JValue`)
- Support for `Scala` 2.10.x, `Scala` 2.11.x and `Scala.js` 0.6.x

## Why
Scala is in a bit of an unfortunate position when it comes to [JSON](https://en.wikipedia.org/wiki/JSON) libraries and
compatibility. On last count, we have around 5 commonly used `AST`'s. A lot of web frameworks,
such as [Spray](http://spray.io/),[Play](https://www.playframework.com/) and [Liftweb](https://www.playframework.com/) 
provide their own `AST`'s, mainly due to address issue of the 
[JSON](https://en.wikipedia.org/wiki/JSON) `AST`'s in the past.

A lot of previously mentioned [JSON](https://en.wikipedia.org/wiki/JSON) library's also fall into weird middle
ground position regarding performance versus correctness, in many cases either not satisfying either camps **or** 
forcing web frameworks to roll their own approaches (which end up being very similar).

`json4s-ast` is an attempt to provide a stable pure correct implementation as well as a high performance implementation of a
[JSON](https://en.wikipedia.org/wiki/JSON) value, called a `JValue`. This means, that when a user works with a `JValue`
(either the `org.json4s.basic.ast` or `org.json4s.ast` version), they can be sure that they can freely pass it around, 
through various `Scala` [JSON](https://en.wikipedia.org/wiki/JSON) parsers/serializers/libraries/frameworks without 
having to worry about compatibility issues.

If a user uses a `org.json4s.basic.ast.JValue` directly/indirectly, they will have a pretty good guarantee about performance 
(can't guarantee good performance for indirect use).
If a user uses a `org.json4s.ast.JValue` directly, they will have a guarantee that the `JValue` is a correct representation of
[JSON](https://en.wikipedia.org/wiki/JSON) standard.

## json4s Basic AST
Implementation is in `org.json4s.basic.ast`

### Goals
- Uses the best performing datastructure's for high performance in construction of a `JValue`
    - `JArray` stored as an `Array`
    - `JObject` stored as an `Array`
    - `JNumber` stored as a `String`
- Doesn't use `Scala`'s `stdlib` collection's library
- Low memory allocation (due to usage of `Array`). When `Scala` provides better support for `Value` types, we will use
those

## json4s AST
Implementation is in `org.json4s.ast`

### Goals
- Fully immutable (all collections/types used are immutable)
- `constant`/`effective constant` lookup time for `JArray`/`JObject`
- Strict adherence to the [JSON](https://en.wikipedia.org/wiki/JSON) standard. 
    - No `JNothing`,`JUndefined` (i.e. no abstraction for a concept of `null` that isn't a `Javascript` `null`, which is
    represented as a `JNull`)
    - Number representation for `JNumber` is a `BigDecimal` (http://stackoverflow.com/a/13502497/1519631)
    - `JObject` is an actual `Map[String,JValue]`
    - `JArray` is an `Vector`
- Strictly pure. Library has no side effects/throwing errors (even when constructing various `JValue`'s), and hence we can
guarantee that a `JValue` will always contain a valid structure that can be 
serialized/rendered into [JSON](https://en.wikipedia.org/wiki/JSON). There is one exception, and that is for `org.json4s.ast.JNumber` 
in `Scala.js` (see `Scala.js` section for more info)

## Scala.js
`json4s-ast` also provides support for [Scala.js](https://github.com/scala-js/scala-js). 
There is even a separate `AST` implementation specifically for `Scala.js` with `@JSExport` for the various `JValue` types, 
which means you are able to construct a `JValue` in `Javascript`in the rare cases that you may need to do so. 
Hence there are added constructors for various `JValue` subtypes, i.e. you can pass in a `Javascript` `array` (i.e. `[]`) 
to construct a `JArray`, as well as a constructor for `JObject` that allows you to pass in a standard `Javascript` 
object with `JValue` as keys (i.e. `{}`).

Examples of constructing various `JValue`'s are given below.

```javascript
var jArray = new org.json4s.ast.JArray([new JString("test")]);

var jObject = new org.json4s.ast.JObject({"someString" : jArray});

var jObjectWithBool = new org.json4s.ast.JObject({
    "someString" : jArray,
    "someBool" : org.json4s.ast.JTrue()
});

var jObjectWithBoolAndNumber = new org.json4s.ast.JObject({
    "someString" : jArray,
    "someBool" : org.json4s.ast.JTrue(),
    "someNumber" : new org.json4s.ast.JNumber(324324.324)
});

var jObjectWithBoolAndNumberAndNull = new org.json4s.ast.JObject({
    "someString" : jArray,
    "someBool" : org.json4s.ast.JTrue(),
    "someNumber" : new org.json4s.ast.JNumber(324324.324),
    "null: org.json4s.ast.JNull()
});
```

### Differences
There is one major difference that people need to be aware of when using `json4s-ast` with `Scala.js`, and that is an
exception may be thrown when using the `JNumber` `String` constructor for the pure version of `json4s-ast` (`org.json4s.ast`). 
Unfortunately there is no real way around this. `Javascript` doesn't have a standard `BigDecimal` 
(i.e. unbounded real number type), so the only way to construct a `JNumber` larger than specified in the IEEE 754 
in `Javascript` is to use a `String` representation ([JSON](https://en.wikipedia.org/wiki/JSON) 
[specification](http://stackoverflow.com/a/13502497/1519631) is that the 
number can be of any size, unlike the `Javascript` [specification](http://stackoverflow.com/a/3605960/1519631)). 
This means that if you don't put a valid number as a `String` when calling the `JNumber` constructor 
in `Javascript`/`Scala.js`, it will error out. As an example below

```javascript
// How to construct a really large JNumber in Javascript
var jNumber = new org.json4s.ast.JNumber("34235325322353257498327423.23532875932598234783252325");
// Understandably, this will error
var jNumber = new org.json4s.ast.JNumber("this will error");
```

Obviously in `Javascript`, this will always error out in runtime, but since the `String` constructor is exported for `Scala`
as well (only in the `Scala.js` artifact, not the `Scala` `JVM` one), you can do this when writing `Scala` with `Scala.js`
```scala
val jNumber = new org.json4s.ast.JNumber("this will error")
```
This will error out with an exception at runtime. Note that the actual exception is not known (this depends on the `Scala.js`
implementation of `BigDecimal` which may change) so you should **NOT** try and catch it.

You just need to be strict and not use the `JNumber` `String` constructor in `Scala.js` so that this error is never thrown.

When using `Scala` on the `JVM` there is no exported `String` method for `JNumber`. Also when using the `org.json4s.ast.basic`
library, you can expect runtime errors for incorrect usage (however this is implied by design of the library).

For the `org.json4s.basic.ast`, the API is the same as `org.json4s.ast`. One major difference is that while `org.json4s.basic.ast`
uses `Array` on the JVM, on `Scala.js` it uses `js.Array`. This is because `org.json4s.basic.ast` is focused on performance, and
`js.Array` is by far the best performing linear data structure for `Scala.js` on `Javascript`. If you have common code that uses
`org.json4s.basic.ast` in both `Scala` `JVM` and `Scala.js` **and** you wish to retain performance (i.e. no usage of 
`toArray`/`toJSArray`), you need to refactor that common code so you can handle this.

As an added note, there is an extra constructor for a `Javascript` number type in `org.json4s.basic.ast` (i.e. you can 
do `var jNumber = new org.json4s.basic.ast.JObject.JNumber(3254);`)