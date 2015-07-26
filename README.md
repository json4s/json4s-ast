# JSON4S AST

A minimal implementation of a [JSON](https://en.wikipedia.org/wiki/JSON) `AST` that is designed 
to be commonly used by other libraries

## Goals
- [Scala.js](https://github.com/scala-js/scala-js) support, allowing the possibility of 
libraries to seamlessly work with `JValue` on `Javascript` clients as well as the `JVM`
- Strictly zero dependencies
- Strictly one release per major `Scala`/`Scala.js` release. Ideally, `json4s-ast` should only update when a new major 
version for `Scala`/`Scala.js` is released. There may be exceptions to this (i.e. `Scala` has in the past accidentally
brought in breaking changes in minor releases)
- High emphasis on binary compatibility (use of `sealed abstract class` in top level `JValue`)
- Strict adherence to the [JSON](https://en.wikipedia.org/wiki/JSON) standard. 
    - No `JNothing`,`JUndefined` (i.e. no abstraction for a concept of `null` that isn't a `Javascript` `null`, which is
    represented as a `JNull`)
    - Number representation for `JNumber` is a `BigDecimal` (http://stackoverflow.com/a/13502497/1519631)
    - `JObject` is an actual `Map[String,JValue]`
    - `JArray` is an `immutable.Seq`. This allows [JSON](https://en.wikipedia.org/wiki/JSON) 
    libraries to provide the best default immutable data structure, with a default that is very good in general cases
    (`Vector`)
- Support for `Scala` 2.10.x, `Scala` 2.11.x and `Scala.js` 0.6.x
- Strictly pure. Library has no side effects/throwing errors, and we guarantee that a `JValue` will 
always contain a valid structure that can be serialized/rendered into [JSON](https://en.wikipedia.org/wiki/JSON). There
is one exception, and that is for `JNumber` in `Scala.js` (see `Scala.js` section for more info)
- Fully immutable (all collections/types used are immutable)
`apply` methods, providing good performance for using `json4s-ast` before the `JVM` warms up

## Why
Scala is in a bit of an unfortunate position when it comes to [JSON](https://en.wikipedia.org/wiki/JSON) libraries and
compatibility. On last count, we have around 5 commonly used `AST`'s, which in design are incredibly similar (this happened
naturally over time). A lot of web frameworks, such as [Spray](http://spray.io/),[Play](https://www.playframework.com/) and
[Liftweb](https://www.playframework.com/) provide their own `AST`'s, mainly due to address issue of the 
[JSON](https://en.wikipedia.org/wiki/JSON) `AST`'s in the past.

`json4s-ast` is an attempt to provide a commonly used `JSON` value i.e. a `JValue` to be used by the various `Scala`
[JSON](https://en.wikipedia.org/wiki/JSON) libraries. This means, that when a user works with a `JValue`, 
they can be sure that they can freely pass it around, through various `Scala` 
[JSON](https://en.wikipedia.org/wiki/JSON) parsers/serializers/libraries/frameworks without having to worry about 
compatibility issues.

## Scala.js
`json4s-ast` also provides support for [Scala.js](https://github.com/scala-js/scala-js). 
There is even a separate `AST` implementation specifically for `Scala.js` with `@JSExport` for the various `JValue` types, 
which means you are able to construct a `JValue` in `Javascript`in the rare cases that you may need to do so. 
Hence there are added constructors for various `JValue` subtypes, i.e. you can pass in a `Javascript` `array` (i.e. `[]`) 
to construct a `JArray`, as well as a constructor for `JObject` that allows you to pass in a standard `Javascript` 
object with `JValue` as keys (i.e. `{}`).

Examples of constructing various `JValue`'s are given below.

```javascript
var jArray = new JArray([new JString("test")]);

var jObject = new JObject({"someString" : jArray});

var jObjectWithBool = new JObject({
    "someString" : jArray,
    "someBool" : JTrue()
});

var jObjectWithBoolAndNumber = new JObject({
    "someString" : jArray,
    "someBool" : JTrue(),
    "someNumber" : new JNumber(324324.324)
});

var jObjectWithBoolAndNumberAndNull = new JObject({
    "someString" : jArray,
    "someBool" : JTrue(),
    "someNumber" : new JNumber(324324.324),
    "null: JNull()
});
```

### Differences
There is one major difference that people need to be aware of when using `json4s-ast` with `Scala.js`, and that is an
exception may be thrown when using the `JNumber` `String` constructor. Unfortunately there is no real way around this.
`Javascript` doesn't have a standard `BigDecimal` (i.e. unbounded real number type), so the only way to construct a `JNumber`
larger than specified in the IEEE 754 in `Javascript` is to use a `String` representation 
([JSON](https://en.wikipedia.org/wiki/JSON) [specification](http://stackoverflow.com/a/13502497/1519631) is that the 
number can be of any size, unlike the `Javascript` [specification](http://stackoverflow.com/a/3605960/1519631)). 
This means that if you don't put a valid number as a `String` when calling the `JNumber` constructor 
in `Javascript`/`Scala.js`, it will error out. As an example below

```javascript
// How to construct a really large JNumber in Javascript
var jNumber = new JNumber("34235325322353257498327423.23532875932598234783252325");
// Understandably, this will error
var jNumber = new JNumber("this will error");
```

Obviously in `Javascript`, this will always error out in runtime, but since the `String` constructor is exported for `Scala`
as well (only in the `Scala.js` artifact, not the `Scala` `JVM` one), you can do this when writing `Scala` with `Scala.js`
```scala
val jNumber = new JNumber("this will error")
```
This will error out with an exception at runtime. Note that the actual exception is not known (this depends on the `Scala.js`
implementation of `BigDecimal` which may change) so you should **NOT** try and catch it.

You just need to be strict and not use the `JNumber` `String` constructor in `Scala.js` so that this error is never thrown.

When using `Scala` on the `JVM` there is no exported `String` method for `JNumber`.