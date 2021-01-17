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

import kartoffel.CacheAlgebra
import kartoffel.formats.CacheSerializer
import kartoffel.async.Async
import kartoffel.formats.CacheDeserializer
import io.lettuce.core.api.async.RedisAsyncCommands
import io.lettuce.core.RedisFuture

class RedisCache(commands: RedisAsyncCommands[String, String]) extends CacheAlgebra[String] {

  override def put[F[_], V](key: String, value: V)(implicit
      cacheSerializer: CacheSerializer[V, String],
      async: Async[F]
  ): F[V] = {
    val redisAsync = redisFutureToAsync(commands.set(key, cacheSerializer.serialize(value)), async)
    async.map(redisAsync)(_ => value)
  }
  override def get[F[_], V](
      key: String
  )(implicit cacheDeserializer: CacheDeserializer[V, String], async: Async[F]): F[Option[V]] = {
    val redisAsync = redisFutureToAsync(commands.get(key), async)
    async.map(redisAsync)(s => Option(s).map(cacheDeserializer.deserialize))
  }

  private def redisFutureToAsync[F[_], R](redisFuture: RedisFuture[R], async: Async[F]): F[R] =
    async.handle[R] { callback =>
      redisFuture.handle[Unit] { (result, t) =>
        t match {
          case null => callback(Right(result))
          case ex   => callback(Left(ex))
        }
      }
    }
}
