# json4s AST

[![Build Status](https://travis-ci.org/json4s/json4s-ast.svg?branch=master)](https://travis-ci.org/json4s/json4s-ast)

Two minimal implementations of a [JSON](https://en.wikipedia.org/wiki/JSON) `AST`, one that is designed for
performance and another that is designed for correctness/purity.

# Installation

json4s-ast is currently published as a SNAPSHOT under sonatype with the following details

```scala
"org.json4s" %% "json4s-ast" % "4.0.0-M1"
```

If you are using `Scala.js`, it's at

```scala
"org.json4s" %%% "json4s-ast" % "4.0.0-M1"
```

Add this setting to your build to include the Sonatype snapshot repository:

```scala
resolvers += { Opts.resolver.sonatypeSnapshots }
```

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
such as [Spray](http://spray.io/), [Play](https://www.playframework.com/) and [Liftweb](http://liftweb.net/) 
provide their own `AST`'s, mainly due to address issues of the 
[JSON](https://en.wikipedia.org/wiki/JSON) `AST`'s in the past.

A lot of previously mentioned [JSON](https://en.wikipedia.org/wiki/JSON) library's also fall into weird middle
ground position regarding performance versus correctness, in many cases either not satisfying either camps **or** 
forcing web frameworks to roll their own approaches (which end up being very similar).

`json4s-ast` is an attempt to provide a stable pure correct implementation as well as a high performance implementation of a
[JSON](https://en.wikipedia.org/wiki/JSON) value, called a `JValue`. This means, that when a user works with a `JValue`
(either the `org.json4s.ast.fast` or `org.json4s.ast.safe` version), they can be sure that they can freely pass it around, 
through various `Scala` [JSON](https://en.wikipedia.org/wiki/JSON) parsers/serializers/libraries/frameworks without 
having to worry about compatibility issues.

If a user uses a `org.json4s.ast.fast.JValue` directly/indirectly, they will have a pretty good guarantee about performance 
(can't guarantee good performance for indirect use).
If a user uses a `org.json4s.ast.safe.JValue` directly, they will have a guarantee that the `JValue` is a correct representation of
[JSON](https://en.wikipedia.org/wiki/JSON) standard.

## json4s Fast AST
Implementation is in `org.json4s.ast.fast`

### Goals
- Uses the best performing datastructure's for high performance in construction of a `JValue`
    - `JArray` stored as an `Array`
    - `JObject` stored as an `Array`
    - `JNumber` stored as a `String`
- Doesn't use `Scala`'s `stdlib` collection's library
- Low memory allocation (due to usage of `Array`). When `Scala` provides better support for `Value` types, we will use
those

## json4s Safe AST
Implementation is in `org.json4s.ast.safe`

### Goals
- Fully immutable (all collections/types used are immutable)
- `constant`/`effective constant` lookup time for `JArray`/`JObject`
- Strict adherence to the [JSON](https://en.wikipedia.org/wiki/JSON) standard.
    - Number representation for `JNumber` is a `BigDecimal` (http://stackoverflow.com/a/13502497/1519631)
    - `JObject` is an actual `Map[String,JValue]`
    - `JArray` is an `Vector`
- Strictly pure. Library has no side effects/throwing errors (even when constructing various `JValue`'s), and hence we can
guarantee that a `JValue` will always contain a valid structure that can be 
serialized/rendered into [JSON](https://en.wikipedia.org/wiki/JSON). There is one exception, and that is for `org.json4s.ast.safe.JNumber` 
in `Scala.js` (see `Scala.js` section for more info)

## Conversion between org.json4s.ast.safe and org.json4s.ast.fast

Any `org.json4s.ast.safe.JValue` implements a conversion to `org.json4s.ast.fast.JValue` with a `toFast` method, and vice versa with a
`toSafe` method. These conversion methods have been written to be as fast as possible.

There are some peculiarities when converting between the two AST's. When converting a `org.json4s.ast.fast.JNumber` to a 
`org.json4s.ast.safe.JNumber`, it is possible for this to fail at runtime (since the internal representation of 
`org.json4s.ast.fast.JNumber` is a string). It is up to the caller on how to handle this error (and when), 
a runtime check is deliberately avoided on our end for performance reasons.

Converting from a `org.json4s.ast.safe.JObject` to a `org.json4s.ast.fast.JObject` will produce 
an `org.json4s.ast.fast.JObject` with an undefined ordering for its internal `Array`/`js.Array` representation.
This is because a `Map` has no predefined ordering. If you wish to provide ordering, you will either need
to write your own custom conversion to handle this case.

Do note that according to the JSON spec, ordering for JObject is not defined. Also note that `Map` 
disregards ordering for equality, however `Array`/`js.Array` equals obviously takes ordering into account.

## .to[T] Conversion

Both `org.json4s.ast.safe.JValue` and `org.json4s.ast.fast.JValue` provide conversions using a `.to[T]` method. So far, these are only
implemented for `JNumber`, and its to provide default fast implementations for converting between different number types (as well
as stuff like bytes). You can provide your own implementations of a `.to[T]` 
conversion by creating an `val` that implements a JNumberConverter, i.e.

```scala
implicit val myNumberConverter = new JNumberConverter[SomeNumberType]{
  def apply(b: BigDecimal): SomeNumberType = ???
}
```

Then you just need to provide this implementation in scope for usage

## Scala.js
`json4s-ast` also provides support for [Scala.js](https://github.com/scala-js/scala-js). 
There is even a separate `AST` implementation specifically for `Scala.js` with `@JSExport` for the various `JValue` types, 
which means you are able to construct a `JValue` in `Javascript`in the rare cases that you may need to do so. 
Hence there are added constructors for various `JValue` subtypes, i.e. you can pass in a `Javascript` `array` (i.e. `[]`) 
to construct a `JArray`, as well as a constructor for `JObject` that allows you to pass in a standard `Javascript` 
object with `JValue` as keys (i.e. `{}`).

Examples of constructing various `JValue`'s are given below.

```javascript
var jArray = new org.json4s.ast.safe.JArray([new JString("test")]);

var jObject = new org.json4s.ast.safe.JObject({"someString" : jArray});

var jObjectWithBool = new org.json4s.ast.safe.JObject({
    "someString" : jArray,
    "someBool" : org.json4s.ast.safe.JTrue()
});

var jObjectWithBoolAndNumber = new org.json4s.ast.safe.JObject({
    "someString" : jArray,
    "someBool" : org.json4s.ast.safe.JTrue(),
    "someNumber" : new org.json4s.ast.safe.JNumber(324324.324)
});

var jObjectWithBoolAndNumberAndNull = new org.json4s.ast.safe.JObject({
    "someString" : jArray,
    "someBool" : org.json4s.ast.safe.JTrue(),
    "someNumber" : new org.json4s.ast.safe.JNumber(324324.324),
    "null": org.json4s.ast.safe.JNull()
});
```

### Differences
There is one major difference that people need to be aware of when using `json4s-ast` with `Scala.js`, and that is an
exception may be thrown when using the `JNumber` `String` constructor for the safe version of `json4s-ast` (`org.json4s.ast.safe`). 
Unfortunately there is no real way around this. `Javascript` doesn't have a standard `BigDecimal` 
(i.e. unbounded real number type), so the only way to construct a `JNumber` larger than specified in the IEEE 754 
in `Javascript` is to use a `String` representation ([JSON](https://en.wikipedia.org/wiki/JSON) 
[specification](http://stackoverflow.com/a/13502497/1519631) is that the 
number can be of any size, unlike the `Javascript` [specification](http://stackoverflow.com/a/3605960/1519631)). 
This means that if you don't put a valid number as a `String` when calling the `JNumber` constructor 
in `Javascript`/`Scala.js`, it will error out. As an example below

```javascript
// How to construct a really large JNumber in Javascript
var jNumber = new org.json4s.ast.safe.JNumber("34235325322353257498327423.23532875932598234783252325");
// Understandably, this will error
var jNumber = new org.json4s.ast.safe.JNumber("this will error");
```

Obviously in `Javascript`, this will always error out in runtime, but since the `String` constructor is exported for `Scala`
as well (only in the `Scala.js` artifact, not the `Scala` `JVM` one), you can do this when writing `Scala` with `Scala.js`
```scala
val jNumber = new org.json4s.ast.safe.JNumber("this will error")
```
This will error out with an exception at runtime. Note that the actual exception is not known (this depends on the `Scala.js`
implementation of `BigDecimal` which may change) so you should **NOT** try and catch it.

You just need to be strict and not use the `JNumber` `String` constructor in `Scala.js` so that this error is never thrown.

When using `Scala` on the `JVM` there is no exported `String` method for `JNumber`. Also when using the `org.json4s.ast.fast`
library, you can expect runtime errors for incorrect usage (however this is implied by design of the library).

For the `org.json4s.ast.fast`, the API is the same as `org.json4s.ast.safe`. One major difference is that while `org.json4s.ast.fast`
uses `Array` on the JVM, on `Scala.js` it uses `js.Array`. This is because `org.json4s.ast.fast` is focused on performance, and
`js.Array` is by far the best performing linear data structure for `Scala.js` on `Javascript`. If you have common code that uses
`org.json4s.ast.fast` in both `Scala` `JVM` and `Scala.js` **and** you wish to retain performance (i.e. no usage of 
`toArray`/`toJSArray`), you need to refactor that common code so you can handle this.

As an added note, there is an extra constructor for a `Javascript` number type in `org.json4s.ast.fast` (i.e. you can 
do `var jNumber = new org.json4s.ast.fast.JNumber(3254);`)
