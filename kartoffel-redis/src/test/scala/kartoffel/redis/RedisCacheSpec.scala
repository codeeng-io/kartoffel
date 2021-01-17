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

package kartoffel.redis

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.concurrent.ScalaFutures
import kartoffel.formats.CacheSerializer
import io.lettuce.core.RedisClient
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import kartoffel.formats.CacheDeserializer
import com.dimafeng.testcontainers.DockerComposeContainer
import java.io.File
import com.dimafeng.testcontainers.ExposedService
import com.dimafeng.testcontainers.ContainerDef
import com.dimafeng.testcontainers.scalatest.TestContainerForAll
import org.scalatest.BeforeAndAfterAll

class RedisCacheSpec
    extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with TestContainerForAll
    with BeforeAndAfterAll {

  override val containerDef: ContainerDef =
    DockerComposeContainer.Def(
      new File("docker-compose.yml"),
      exposedServices = Seq(ExposedService("redis", 6379))
    )

  private case class Dog(name: String, age: Int)

  // Serialize as JSON
  private implicit val dogSerializer = new CacheSerializer[Dog, String] {
    override def serialize(value: Dog): String = value.asJson.noSpaces
  }

  private implicit val dogDeserializer = new CacheDeserializer[Dog, String] {
    override def deserialize(serialized: String): Dog =
      decode[Dog](serialized).getOrElse(throw new Exception("Cannot deserialize an object"))
  }

  "Redis cache" should {
    import scala.concurrent.ExecutionContext.Implicits.global
    import kartoffel.async.futureAsync

    "put an object" in {
      withRedisCache { cache =>
        val dog = Dog("Fido", 2)
        whenReady(cache.put("fido", dog))(_ shouldBe dog)
      }
    }

    "get an object from cache" in {
      withRedisCache { cache =>
        val dog = Dog("Fido", 2)
        whenReady(cache.put("fido", dog))(_ shouldBe dog)
        whenReady(cache.get("fido"))(_ shouldBe Some(dog))
      }
    }

    "get None when an object is not present in the cache" in {
      withRedisCache { cache =>
        whenReady(cache.get("fido69"))(_ shouldBe None)
      }
    }
  }

  private def withRedisCache(assertions: (RedisCache) => Unit): Unit = {
    val client        = RedisClient.create("redis://localhost:6379")
    val asyncCommands = client.connect().async()
    val cache         = new RedisCache(asyncCommands)
    assertions(cache)
    client.shutdown()
  }

}
