/*
 * Copyright (c) 2021 codeeng
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package kartoffel.caffeine

import kartoffel.formats.{ CacheDeserializer, CacheSerializer }
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers

class CaffeineCacheSpec extends AnyWordSpec with Matchers with ScalaFutures {
  private case class Dog(name: String, age: Int)

  private implicit val dogSerializer: CacheSerializer[Dog, Dog] = (value: Dog) => value

  private implicit val dogDeserializer: CacheDeserializer[Dog, Dog] = (serialized: Dog) => serialized

  "Caffeine" should {
    import scala.concurrent.ExecutionContext.Implicits.global
    import kartoffel.async.futureAsync
    "put an object in cache" in {
      val cache = CaffeineCache[Dog]()

      val dog = Dog("Fido", 2)
      whenReady(cache.put("fido", dog))(_ shouldBe dog)
    }

    "get an object from cache" in {
      val cache = CaffeineCache[Dog]()

      val dog = Dog("Fido", 2)
      whenReady(cache.put("fido", dog))(_ shouldBe dog)
      whenReady(cache.get("fido"))(_ shouldBe Some(dog))
    }

    "get None when an object is not present in the cache" in {
      val cache = CaffeineCache[Dog]()

      whenReady(cache.get("fido"))(_ shouldBe None)
    }

    "overwrite an existing object" in {
      val cache = CaffeineCache[Dog]()

      val fido = Dog("Fido", 2)
      whenReady(cache.put("fido", fido))(_ shouldBe fido)
      whenReady(cache.get("fido"))(_ shouldBe Some(fido))
      val pluto = Dog("Pluto", 1)
      whenReady(cache.put("fido", pluto))(_ shouldBe pluto)
      whenReady(cache.get("fido"))(_ shouldBe Some(pluto))
    }
  }

  "Caffeine using Cats IO" should {
    import kartoffel.async.cats.catsAsync
    "put and get an object" in {
      val cache = CaffeineCache[Dog]()

      val dog = Dog("Fido", 2)
      cache.put("fido", dog).unsafeRunSync() shouldBe dog
      cache.get("fido").unsafeRunSync() shouldBe Some(dog)
    }
  }

  "Caffeine using ZIO" should {
    val runtime = zio.Runtime.default
    import kartoffel.async.zio.zioAsync
    "put and get an object" in {
      val cache = CaffeineCache[Dog]()

      val dog = Dog("Fido", 2)
      runtime.unsafeRun(cache.put("fido", dog)) shouldBe dog
      runtime.unsafeRun(cache.get("fido")) shouldBe Some(dog)
    }
  }
}
