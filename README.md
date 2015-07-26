# JSON4S AST

A minimal implementation of a [JSON](https://en.wikipedia.org/wiki/JSON) `AST` that is designed to be commonly used by other libraries

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
    - Number representation for `JNumber` is a `BigDecimal`
    - `JObject` is an actual `Map[String,JValue]`
    - `JArray` is an `immutable.Seq`. This allows [JSON](https://en.wikipedia.org/wiki/JSON) 
    libraries to provide the best default immutable data structure, with a default that is very good in general cases
    (`Vector`)
- Support for `Scala` 2.10.x, `Scala` 2.11.x and `Scala.js` 0.6.x
- Strictly pure. Library has no side effects, and we guarantee that a `JValue` will always contain a valid structure 
that can be serialized/rendered into [JSON](https://en.wikipedia.org/wiki/JSON)
- Public methods are `@inline`. Due to us being very strict on binary releases, we can afford to `@inline` our various
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
There is even a separate `AST` specifically for `Scala.js` with `@JSExport` for the various `JValue` types, 
which means you are able to construct a `JValue` in `Javascript`in the rare cases that you may need to do so. 
Hence there are added constructors for `JArray`, that lets you pass in a `Javascript` `array` (i.e. `[]`) 
to construct a `JArray`, as well as a constructor for `JObject` that allows you to pass in a standard `Javascript` 
object with `JValue` as keys (i.e. `{}`)

Examples of constructing various `JValue`'s are given below

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
    "someNumber" : JNumber(324324.324)
});

var jObjectWithBoolAndNumberAndNull = new JObject({
    "someString" : jArray,
    "someBool" : JTrue(),
    "someNumber" : JNumber(324324.324),
    "null: JNull()
});
```